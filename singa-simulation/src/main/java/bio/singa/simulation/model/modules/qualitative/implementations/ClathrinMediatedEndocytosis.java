package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaManager;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationPool;
import bio.singa.simulation.model.sections.concentration.InitialConcentration;
import bio.singa.simulation.model.sections.concentration.SectionConcentration;
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

    private List<MembraneSegment> segments;
    // private Map<ChemicalEntity, AbstractMap.Entry<Quantity<Area>, Double>> initialMembraneCargo;

    // pits that collect cargo
    private List<Pit> preAspiringPits;
    private List<Pit> aspiringPits;
    // pits that are maturing
    private List<Pit> preMaturingPits;
    private List<Pit> maturingPits;
    // pits that will be aborted
    private List<Pit> abortedPits;
    // pits that ripened to vesicles
    private List<Pit> maturedPits;

    private boolean test = false;

    public ClathrinMediatedEndocytosis() {
        segments = new ArrayList<>();
        // pits
        preAspiringPits = new ArrayList<>();
        aspiringPits = new ArrayList<>();
        abortedPits = new ArrayList<>();
        preMaturingPits = new ArrayList<>();
        maturingPits = new ArrayList<>();
        maturedPits = new ArrayList<>();
        // features
        getRequiredFeatures().add(PitFormationRate.class);
        getRequiredFeatures().add(AffectedRegion.class);
        getRequiredFeatures().add(VesicleRadius.class);
        getRequiredFeatures().add(CargoAdditionRate.class);
        getRequiredFeatures().add(EndocytosisCheckpointTime.class);
        getRequiredFeatures().add(EndocytosisCheckpointConcentration.class);
        getRequiredFeatures().add(Cargoes.class);
        getRequiredFeatures().add(MaturationTime.class);
    }

    void setTest() {
        test = true;
    }

    @Override
    public void initialize() {
        setMembraneRegion(getFeature(AffectedRegion.class).getContent());
    }

    public void setMembraneRegion(CellRegion region) {
        for (AutomatonNode node : simulation.getGraph().getNodes()) {
            // skip nodes with wrong region
            if (!node.getCellRegion().equals(region)) {
                continue;
            }
            segments.addAll(node.getMembraneSegments());
        }
    }

    @Override
    public void calculateUpdates() {
        // determine if new pits will form during this time interval
        prepareAspiringPits();
        // check if vesicles will spawn during this time interval
        prepareMaturePits();
        // update existing pits
        collectCargoes();
        // check if pits should be aborted, mature or continue to grow
        checkPitState();
        // set state
        state = ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;
    }

    public void optimizeTimeStep() {

    }

    @Override
    public void onReset() {
        preAspiringPits.clear();
        // TODO revert to original concentrations for pits
        // TODO remove previously calculated deltas
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
        // reserve cargo for aspiring pits
        moveCargoToPits();
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
            // roll if event happens
            if (ThreadLocalRandom.current().nextDouble() < probability) {
                if (test && aspiringPits.size() < 1) {
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
        // choose random point on that site, spawn a little on the inside
        Vector2D spawnSite = segment.getSegment().getRandomPoint();
        spawnSite = spawnSite.add(simulation.getMembraneLayer().getMicrotubuleOrganizingCentre().getCircleRepresentation().getMidpoint().subtract(spawnSite).normalize());
        // sample maturation time
        Quantity<Time> checkpointTime = simulation.getElapsedTime().add(FeatureRandomizer.varyTime(getFeature(EndocytosisCheckpointTime.class).getContent()));
        // sample vesicle radius
        Quantity<Length> spawnRadius = FeatureRandomizer.varyLength(getFeature(VesicleRadius.class).getContent()).to(UnitRegistry.getSpaceUnit());
        List<ConcentrationDelta> concentrationDeltas = determineInitialConcentrations(segment, spawnRadius);
        // return event
        Pit pit = new Pit(checkpointTime, spawnSite, spawnRadius, segment.getNode());
        pit.addDeltas(concentrationDeltas);
        preAspiringPits.add(pit);
    }

    private List<ConcentrationDelta> determineInitialConcentrations(MembraneSegment segment, Quantity<Length> spawnRadius) {
        List<ConcentrationDelta> cargoDeltas = new ArrayList<>();
        double pitArea = spawnRadius.multiply(spawnRadius).multiply(Math.PI).getValue().doubleValue();
        double totalArea = segment.getNode().getMembraneArea().to(UnitRegistry.getAreaUnit()).getValue().doubleValue();
        List<ChemicalEntity> cargoes = getFeature(Cargoes.class).getContent();
        for (ChemicalEntity cargo : cargoes) {
            double membraneConcentration = segment.getNode().getConcentrationContainer().get(MEMBRANE, cargo);
            // initial concentration = pit area * total cell membrane concentration / total cell membrane area
            double concentration = pitArea * membraneConcentration / totalArea;
            cargoDeltas.add(new ConcentrationDelta(this, CellRegions.VESICLE_REGION.getMembraneSubsection(), cargo, concentration));
        }
        return cargoDeltas;
    }


    /**
     * Finalizes the prepared aspiring pits.
     * moves pre aspiring to aspiring and clears pre aspiring
     */
    private void spawnAspiringPits() {
        for (Pit preAspiringPit : preAspiringPits) {
            aspiringPits.add(preAspiringPit);
            logger.trace("Clathrin-coated pit formed at {}.", preAspiringPit.spawnSite);
        }
        preAspiringPits.clear();
    }


    /**
     * Checks if maturing pits have reached their maturation age.
     * moves maturing to matured
     */
    private void prepareMaturePits() {
        for (Pit maturingPit : maturingPits) {
            // check each event if it should spawn
            if (simulation.getElapsedTime().isGreaterThanOrEqualTo(maturingPit.getCheckpointTime())) {
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
        for (Pit preMaturingPit : preMaturingPits) {
            logger.trace("Clathrin-coated pit at {} entered maturation stage.", preMaturingPit.spawnSite);
            // determine new checkpoint
            preMaturingPit.setCheckpointTime(simulation.getElapsedTime().add(FeatureRandomizer.varyTime(getFeature(MaturationTime.class).getContent())));
            maturingPits.add(preMaturingPit);
            aspiringPits.remove(preMaturingPit);
        }
        preMaturingPits.clear();
    }

    /**
     * Creates a vesicle from matured pits.
     * removes matured from maturing and clears matured
     */
    private void spawnVesicles() {
        for (Pit maturedPit : maturedPits) {
            logger.trace("Clathrin-coated pit at {} formed vesicle with cargo.", maturedPit.spawnSite);
            Vesicle vesicle = new Vesicle(maturedPit.getSpawnSite(), maturedPit.getSpawnRadius());
            vesicle.setState(VesicleStateRegistry.ACTIN_PROPELLED);
            initializeCargo(vesicle, maturedPit);
            simulation.getVesicleLayer().addVesicle(vesicle);
            maturingPits.remove(maturedPit);
        }
        maturedPits.clear();
    }

    /**
     * Creates the cargo for the given vesicle.
     *
     * @param vesicle The vesicle.
     * @param maturedPit The original pit
     */
    private void initializeCargo(Vesicle vesicle, Pit maturedPit) {
        for (InitialConcentration initialConcentration : simulation.getConcentrationInitializer().getInitialConcentrations()) {
            if (initialConcentration instanceof SectionConcentration) {
                SectionConcentration sectionConcentration = (SectionConcentration) initialConcentration;
                if (sectionConcentration.getCellRegion() != null && sectionConcentration.getCellRegion().getIdentifier().equals("Vesicle (endocytosis)")) {
                    initialConcentration.initializeUnchecked(vesicle, MEMBRANE);
                }
            }
        }
        Map.Entry<CellSubsection, ConcentrationPool> pool = maturedPit.getConcentrationDeltaManager().getConcentrationContainer().getPool(MEMBRANE);
        for (Map.Entry<ChemicalEntity, Double> entry : pool.getValue().getConcentrations().entrySet()) {
            vesicle.addPotentialDelta(new ConcentrationDelta(this, pool.getKey(), entry.getKey(), entry.getValue()));
        }
    }

    private void collectCargoes() {
        double additionRate = getScaledFeature(CargoAdditionRate.class);
        List<ChemicalEntity> cargoes = getFeature(Cargoes.class).getContent();
        for (Pit aspiringPit : aspiringPits) {
            aspiringPit.addDeltas(determineCollectedCargo(aspiringPit, additionRate, cargoes));
        }
    }

    /**
     * Determines the amount of cargo moving to pits.
     * This only calculates the potential cargo moving into this pit.
     *
     * @param aspiringPit the pit
     * @param additionRate the rate of cargo addition
     * @param cargoes the entities considered cargo
     * @return
     */
    private List<ConcentrationDelta> determineCollectedCargo(Pit aspiringPit, double additionRate, List<ChemicalEntity> cargoes) {
        List<ConcentrationDelta> cargoDeltas = new ArrayList<>();
        for (ChemicalEntity cargo : cargoes) {
            double membraneConcentration = aspiringPit.getAssociatedNode().getConcentrationContainer().get(MEMBRANE, cargo);
            double concentrationDelta = additionRate * membraneConcentration;
            cargoDeltas.add(new ConcentrationDelta(this, aspiringPit.getAssociatedNode().getConcentrationContainer().getMembraneSubsection(), cargo, concentrationDelta));
        }
        return cargoDeltas;
    }

    /**
     * Actually changes concentrations.
     */
    private void moveCargoToPits() {
        for (Pit aspiringPit : aspiringPits) {
            logger.trace("Clathrin-coated pit at {} caught cargo.", aspiringPit.spawnSite);
            // remove concentration that are going to be part of the pit from the node
            aspiringPit.getConcentrationDeltaManager().shiftDeltas();
            AutomatonNode associatedNode = aspiringPit.getAssociatedNode();
            for (ConcentrationDelta delta : aspiringPit.getConcentrationDeltaManager().getFinalDeltas()) {
                ConcentrationDelta concentrationDelta = new ConcentrationDelta(this, associatedNode.getConcentrationContainer().getMembraneSubsection(), delta.getChemicalEntity(), -delta.getValue());
                associatedNode.getConcentrationManager().addPotentialDelta(concentrationDelta);
            }
            // apply deltas to pit
            aspiringPit.getConcentrationDeltaManager().applyDeltas();
        }
    }

    /**
     * Checks state of aspiring pits.
     * Moves aspiring to pre maturing pits or aborting pits.
     */
    private void checkPitState() {
        double criticalConcentration = getFeature(EndocytosisCheckpointConcentration.class).getContent().getValue().doubleValue();
        for (Pit aspiringPit : aspiringPits) {
            // check if critical concentration has been reached
            if (aspiringPit.sumCargo() >= criticalConcentration) {
                // move to pre maturing pits
                preMaturingPits.add(aspiringPit);
                continue;
            }
            // check it maximal aspiration time has been reached
            if (simulation.getElapsedTime().isGreaterThanOrEqualTo(aspiringPit.getCheckpointTime())) {
                // move to aborting pits
                abortedPits.add(aspiringPit);
            }
        }
    }

    private void abortPits() {
        for (Pit abortedPit : abortedPits) {
            logger.trace("Clathrin-coated pit at {} was aborted.", abortedPit.spawnSite);
            // free reserved concentration
            AutomatonNode associatedNode = abortedPit.getAssociatedNode();
            Map.Entry<CellSubsection, ConcentrationPool> pool = abortedPit.getConcentrationDeltaManager().getConcentrationContainer().getPool(MEMBRANE);
            for (Map.Entry<ChemicalEntity, Double> entry : pool.getValue().getConcentrations().entrySet()) {
                associatedNode.addPotentialDelta(new ConcentrationDelta(this, pool.getKey(), entry.getKey(), entry.getValue()));
            }
            aspiringPits.remove(abortedPit);
        }
        abortedPits.clear();
    }

    public List<Pit> getAspiringPits() {
        return aspiringPits;
    }

    public static class Pit {

        // randomized next spawn time
        private Quantity<Time> checkpointTime;
        // randomized next spawn site
        private Vector2D spawnSite;
        // randomized radius
        private Quantity<Length> spawnRadius;
        // concentration delta
        private ConcentrationDeltaManager concentrationDeltaManager;
        // associated node
        private AutomatonNode associatedNode;

        public Pit(Quantity<Time> checkpointTime, Vector2D spawnSite, Quantity<Length> spawnRadius, AutomatonNode associatedNode) {
            this.checkpointTime = checkpointTime;
            this.spawnSite = spawnSite;
            this.spawnRadius = spawnRadius;
            this.associatedNode = associatedNode;
            concentrationDeltaManager = new ConcentrationDeltaManager(CellRegions.VESICLE_REGION.setUpConcentrationContainer());
            concentrationDeltaManager.backupConcentrations();
        }

        public Quantity<Time> getCheckpointTime() {
            return checkpointTime;
        }

        public void setCheckpointTime(Quantity<Time> checkpointTime) {
            this.checkpointTime = checkpointTime;
        }

        public Vector2D getSpawnSite() {
            return spawnSite;
        }

        public Quantity<Length> getSpawnRadius() {
            return spawnRadius;
        }

        public ConcentrationDeltaManager getConcentrationDeltaManager() {
            return concentrationDeltaManager;
        }

        public double sumCargo() {
            return concentrationDeltaManager.getConcentrationContainer()
                    .getPool(MEMBRANE).getValue().getConcentrations().values().stream()
                    .mapToDouble(entry -> entry)
                    .sum();
        }

        public void addDeltas(List<ConcentrationDelta> additionDelta) {
            additionDelta.forEach(concentrationDeltaManager::addPotentialDelta);
        }

        public AutomatonNode getAssociatedNode() {
            return associatedNode;
        }
    }


}
