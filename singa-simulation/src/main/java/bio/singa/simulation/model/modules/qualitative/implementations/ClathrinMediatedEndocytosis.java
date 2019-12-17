package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.concentrations.InitialConcentration;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import bio.singa.simulation.model.sections.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;

/**
 * @author cl
 */
public class ClathrinMediatedEndocytosis extends QualitativeModule {

    private static final Logger logger = LoggerFactory.getLogger(ClathrinMediatedEndocytosis.class);

    private VesicleLayer vesicleLayer;
    private List<MembraneSegment> segments;
    // pits that collect cargo
    private List<EndocytoticPit> preAspiringPits;
    // pits that are maturing
    private List<EndocytoticPit> preMaturingPits;
    // pits that ripened to vesicles
    private List<EndocytoticPit> maturedPits;
    // pits that will be aborted
    private List<EndocytoticPit> abortedPits;

    private CellRegion pitRegion;

    private boolean limitPits = false;

    public ClathrinMediatedEndocytosis(VesicleLayer vesicleLayer) {
        this.vesicleLayer = vesicleLayer;
        segments = new ArrayList<>();
        // pits
        preAspiringPits = new ArrayList<>();
        abortedPits = new ArrayList<>();
        preMaturingPits = new ArrayList<>();
        maturedPits = new ArrayList<>();
        // features
        getRequiredFeatures().add(PitFormationRate.class);
        getRequiredFeatures().add(AffectedRegion.class);
        getRequiredFeatures().add(VesicleRadius.class);
        getRequiredFeatures().add(EndocytosisCheckpointTime.class);
        getRequiredFeatures().add(EndocytosisCheckpointConcentration.class);
        getRequiredFeatures().add(Cargo.class);
        getRequiredFeatures().add(Cargoes.class);
        getRequiredFeatures().add(MaturationTime.class);
        getRequiredFeatures().add(InitialConcentrations.class);

    }

    void limitPitsToOneAtATime() {
        limitPits = true;
    }

    @Override
    public void initialize() {
        CellRegion region = getFeature(AffectedRegion.class).getContent();
        initializeMembraneRegion(region);
        initializePitRegion(region);
    }

    private void initializeMembraneRegion(CellRegion region) {
        for (AutomatonNode node : getSimulation().getGraph().getNodes()) {
            // skip nodes with wrong region
            if (!node.getCellRegion().equals(region)) {
                continue;
            }
            segments.addAll(node.getMembraneSegments());
        }
    }

    private void initializePitRegion(CellRegion region) {
        pitRegion = new CellRegion("endocytotic pit");
        pitRegion.addSubsection(CellTopology.OUTER, region.getMembraneSubsection());
        pitRegion.addSubsection(CellTopology.MEMBRANE, CellSubsections.PIT_MEMBRANE);
        pitRegion.addSubsection(CellTopology.INNER, region.getInnerSubsection());
    }

    @Override
    public void calculateUpdates() {
        // determine if new pits will form during this time interval
        prepareAspiringPits();
        // check if vesicles will spawn during this time interval
        prepareMaturePits();
        // check if pits should be aborted, mature or continue to grow
        checkPitState();
        // set state
        setState(ModuleState.SUCCEEDED_WITH_PENDING_CHANGES);
    }

    public void optimizeTimeStep() {

    }

    @Override
    public void onReset() {
        preAspiringPits.clear();
        for (EndocytoticPit aspiringPit : vesicleLayer.getAspiringPits()) {
            aspiringPit.getConcentrationDeltaManager().clearPotentialDeltas();
        }
        preMaturingPits.clear();
        abortedPits.clear();
        maturedPits.clear();
    }

    @Override
    public void onCompletion() {
        // spawn new pits
        spawnAspiringPits();
        // move aspiring pits to maturing
        spawnMaturingPits();
        // scission old pits
        spawnVesicles();
        // abort failed pits
        abortPits();
    }

    /**
     * Checks if aspiring pits should be spawned.
     * creates pre aspiring pits
     */
    private void prepareAspiringPits() {
        for (MembraneSegment segment : segments) {
            // probability = rate (1/area*time) * area * time step
            double probability = getFeature(PitFormationRate.class).getContent()
                    .multiply(segment.getArea())
                    .multiply(UnitRegistry.getTime())
                    .getValue().doubleValue();
            // roll, deciding if event happens
            if (ThreadLocalRandom.current().nextDouble() < probability) {
                if (limitPits) {
                    if (vesicleLayer.getAspiringPits().size() < 1 && vesicleLayer.getMaturingPits().size() < 1) {
                        initializeAspiringPit(segment);
                    }
                } else {
                    initializeAspiringPit(segment);
                }
            }
        }
    }

    /**
     * Prepares a new aspiring pit.
     *
     * @param segment The segment where the pit should spawn.
     */
    private void initializeAspiringPit(MembraneSegment segment) {
        // initialize pit
        EndocytoticPit pit = new EndocytoticPit(segment.getNode());
        pit.setCellRegion(pitRegion);
        pit.setCollecting(true);

        // choose random point on that site
        Vector2D spawnSite = segment.getSegment().getRandomPoint();
        // move a tiny bit towards the center of the cell
        spawnSite = spawnSite.add(getSimulation().getMembraneLayer().getMicrotubuleOrganizingCentre().getCircleRepresentation().getMidpoint().subtract(spawnSite).normalize());
        pit.setSpawnSite(spawnSite);

        // checkpoint time
        Quantity<Time> checkpointTime = getSimulation().getElapsedTime().add(FeatureRandomizer.varyTime(getFeature(EndocytosisCheckpointTime.class).getContent()));
        pit.setCheckpointTime(checkpointTime);

        // vesicle radius
        Quantity<Length> spawnRadius = getFeature(VesicleRadius.class).getContent().to(UnitRegistry.getSpaceUnit());
        pit.setRadius(spawnRadius);

        // add pit
        preAspiringPits.add(pit);
    }

    /**
     * Finalizes the prepared aspiring pits.
     * moves pre aspiring to aspiring and clears pre aspiring
     */
    private void spawnAspiringPits() {
        for (EndocytoticPit preAspiringPit : preAspiringPits) {
            // initial concentrations
            List<ChemicalEntity> entities = getFeature(Cargoes.class).getContent();
            preAspiringPit.initializeConcentrations(entities, this);
            preAspiringPit.getConcentrationManager().backupConcentrations();
            // add to pit
            vesicleLayer.getAspiringPits().add(preAspiringPit);
//            vesicleLayer.getSimulation().getUpdatables().add(preAspiringPit);
            logger.debug("Clathrin-coated pit formed at {}.", preAspiringPit.getSpawnSite());
        }
        preAspiringPits.clear();
    }


    /**
     * Checks if maturing pits have reached their maturation age.
     * moves maturing to matured
     */
    private void prepareMaturePits() {
        for (EndocytoticPit maturingPit : vesicleLayer.getMaturingPits()) {
            // check each event if it should spawn
            if (getSimulation().getElapsedTime().isGreaterThanOrEqualTo(maturingPit.getCheckpointTime())) {
                // move to completing events
                maturedPits.add(maturingPit);
            }
        }
    }

    /**
     * Move pre maturing to maturing
     * adds pre maturing pit to maturing pit and it removes from aspiring pit, also clears pre maturing pits
     */
    private void spawnMaturingPits() {
        for (EndocytoticPit preMaturingPit : preMaturingPits) {
            logger.debug("Clathrin-coated pit at {} entered maturation stage.", preMaturingPit.getSpawnSite());
            // determine new checkpoint
            preMaturingPit.setCheckpointTime(getSimulation().getElapsedTime().add(FeatureRandomizer.varyTime(getFeature(MaturationTime.class).getContent())));
            preMaturingPit.setCollecting(false);
            vesicleLayer.getMaturingPits().add(preMaturingPit);
            vesicleLayer.getAspiringPits().remove(preMaturingPit);
        }
        preMaturingPits.clear();
    }

    /**
     * Creates a vesicle from matured pits.
     * removes matured from maturing and clears matured
     */
    private void spawnVesicles() {
        for (EndocytoticPit maturedPit : maturedPits) {
            logger.debug("Clathrin-coated pit at {} formed vesicle with cargo.", maturedPit.getSpawnSite());
            Vesicle vesicle = new Vesicle(maturedPit.getSpawnSite(), maturedPit.getRadius());
            vesicle.setState(VesicleStateRegistry.ACTIN_PROPELLED);
            initializeCargo(vesicle, maturedPit);
            vesicleLayer.addVesicle(vesicle);
            vesicleLayer.getMaturingPits().remove(maturedPit);
        }
        maturedPits.clear();
    }

    /**
     * Creates the cargo for the given vesicle.
     *
     * @param vesicle The vesicle.
     * @param maturedPit The original pit
     */
    private void initializeCargo(Vesicle vesicle, EndocytoticPit maturedPit) {
        // initialize fixed concentrations
        List<InitialConcentration> concentrations = getFeature(InitialConcentrations.class).getContent();
        // apply each concentration
        if (concentrations != null) {
            for (InitialConcentration initialConcentration : concentrations) {
                initialConcentration.apply(vesicle);
            }
        }
        // move concentrations from pit membrane to vesicle membrane
        Map.Entry<CellSubsection, ConcentrationPool> pitPool = maturedPit.getConcentrationContainer().getPool(MEMBRANE);
        for (Map.Entry<ChemicalEntity, Double> entry : pitPool.getValue().getConcentrations().entrySet()) {
            vesicle.addPotentialDelta(new ConcentrationDelta(this, vesicle.getConcentrationContainer().getMembraneSubsection(), entry.getKey(), entry.getValue()));
        }
        // fix concentrations
        vesicle.getConcentrationManager().shiftDeltas();
    }

    /**
     * Checks state of aspiring pits.
     * Moves aspiring to pre maturing pits or aborting pits.
     */
    private void checkPitState() {
        double criticalConcentration = getFeature(EndocytosisCheckpointConcentration.class).getContent().getValue().doubleValue();
        ChemicalEntity entity = getFeature(Cargo.class).getContent();
        for (EndocytoticPit aspiringPit : vesicleLayer.getAspiringPits()) {
            // check if critical concentration has been reached
            double concentration = aspiringPit.getConcentrationContainer().get(MEMBRANE, entity);
            if (concentration >= criticalConcentration) {
                // move to pre maturing pits
                preMaturingPits.add(aspiringPit);
                continue;
            }
            // check it maximal aspiration time has been reached
            if (getSimulation().getElapsedTime().isGreaterThanOrEqualTo(aspiringPit.getCheckpointTime())) {
                // move to aborting pits
                abortedPits.add(aspiringPit);
            }
        }
    }

    private void abortPits() {
        for (EndocytoticPit abortedPit : abortedPits) {
            AutomatonNode associatedNode = abortedPit.getAssociatedNode();
            Map.Entry<CellSubsection, ConcentrationPool> pool = abortedPit.getConcentrationDeltaManager().getConcentrationContainer().getPool(MEMBRANE);
            double cargo = pool.getValue().get(getFeature(Cargoes.class).getContent().get(0));
            logger.debug("Clathrin-coated pit at {} was aborted with {} molecules cargo.", abortedPit.getSpawnSite(), MolarConcentration.concentrationToMolecules(cargo));
            // free reserved concentration
            for (Map.Entry<ChemicalEntity, Double> entry : pool.getValue().getConcentrations().entrySet()) {
                associatedNode.addPotentialDelta(new ConcentrationDelta(this, associatedNode.getCellRegion().getMembraneSubsection(), entry.getKey(), entry.getValue()));
            }
            vesicleLayer.getAspiringPits().remove(abortedPit);
        }
        abortedPits.clear();
    }

}
