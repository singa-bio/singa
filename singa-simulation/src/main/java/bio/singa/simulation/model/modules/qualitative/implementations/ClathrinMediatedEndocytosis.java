package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import bio.singa.simulation.model.sections.CellRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;

/**
 * @author cl
 */
public class ClathrinMediatedEndocytosis extends QualitativeModule {

    private static final Logger logger = LoggerFactory.getLogger(ClathrinMediatedEndocytosis.class);

    private List<MembraneSegment> segments;
    private Map<ChemicalEntity, AbstractMap.Entry<Quantity<Area>, Double>> initialMembraneCargo;

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

    public ClathrinMediatedEndocytosis() {
        initialMembraneCargo = new HashMap<>();
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
        getRequiredFeatures().add(VesicleRadius.class);
        getRequiredFeatures().add(CargoAdditionRate.class);
        getRequiredFeatures().add(EndocytosisCheckpointTime.class);
        getRequiredFeatures().add(EndocytosisCheckpointConcentration.class);
        getRequiredFeatures().add(Cargo.class);
        getRequiredFeatures().add(MaturationTime.class);
    }

    @Override
    public void calculateUpdates() {
        // determine if new pits will form during this time interval
        prepareAspiringPits();
        // check if vesicles will spawn during this time interval
        prepareMaturePits();
        // update existing pits
        determineCollectedCargo();
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
                initializeAspiringPit(segment);
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
        // initial concentration
        double pitArea = spawnRadius.multiply(spawnRadius).multiply(Math.PI).getValue().doubleValue();
        ChemicalEntity cargo = getFeature(Cargo.class).getContent();
        double totalConcentration = segment.getNode().getConcentrationContainer().get(MEMBRANE, cargo);
        double totalArea = segment.getNode().getMembraneArea().to(UnitRegistry.getAreaUnit()).getValue().doubleValue();
        // initial concentration = pit area * total cell membrane concentration / total cell membrane area
        double concentration = pitArea * totalConcentration / totalArea;
        ConcentrationDelta concentrationDelta = new ConcentrationDelta(this, segment.getNode().getConcentrationContainer().getMembraneSubsection(), cargo, -concentration);
        // return event
        preAspiringPits.add(new Pit(checkpointTime, spawnSite, spawnRadius, concentration, segment.getNode(), concentrationDelta));
    }

    /**
     * Finalizes the prepared aspiring pits.
     * moves pre aspiring to aspiring and clears pre aspiring
     */
    private void spawnAspiringPits() {
        for (Pit preAspiringPit : preAspiringPits) {
            aspiringPits.add(preAspiringPit);
            logger.trace("Clathrin-coated pit formed at {}.", preAspiringPit.spawnSite);
            preAspiringPit.getAssociatedNode().addPotentialDelta(preAspiringPit.getAdditionDelta());
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
            logger.trace("Clathrin-coated pit at {} formed vesicle with {} cargo molecules.", maturedPit.spawnSite, MolarConcentration.concentrationToMolecules(maturedPit.getCargoConcentration()).getValue());
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
        for (Map.Entry<ChemicalEntity, Map.Entry<Quantity<Area>, Double>> entry : initialMembraneCargo.entrySet()) {
            // get values
            ChemicalEntity chemicalEntity = entry.getKey();
            Quantity<Area> area = entry.getValue().getKey();
            double number = entry.getValue().getValue();
            // scale to vesicle surface
            double molecules = vesicle.getArea().multiply(number / area.to(vesicle.getArea().getUnit())
                    .getValue().doubleValue()).getValue().doubleValue();
            // convert to concentration
            double concentration = MolarConcentration.moleculesToConcentration(molecules);
            // set concentration
            vesicle.getConcentrationContainer().initialize(MEMBRANE, chemicalEntity, UnitRegistry.concentration(concentration));
        }
        ChemicalEntity cargo = getFeature(Cargo.class).getContent();
        vesicle.getConcentrationContainer().initialize(MEMBRANE, cargo, UnitRegistry.concentration(maturedPit.getCargoConcentration()));
    }

    /**
     * Determines the amount of cargo moving to pits.
     * This only calculates the potential cargo moving into this pit.
     */
    private void determineCollectedCargo() {
        double additionRate = getScaledFeature(CargoAdditionRate.class);
        ChemicalEntity cargo = getFeature(Cargo.class).getContent();
        for (Pit aspiringPit : aspiringPits) {
            double membraneConcentration = aspiringPit.getAssociatedNode().getConcentrationContainer().get(MEMBRANE, cargo);
            double concentrationDelta = additionRate * membraneConcentration;
            aspiringPit.setAdditionDelta(new ConcentrationDelta(this, aspiringPit.getAssociatedNode().getConcentrationContainer().getMembraneSubsection(), cargo, -concentrationDelta));
        }
    }

    /**
     * Actually changes concentrations.
     */
    private void moveCargoToPits() {
        for (Pit aspiringPit : aspiringPits) {
            logger.trace("Clathrin-coated pit at {} caught {} cargo molecules.", aspiringPit.spawnSite, MolarConcentration.concentrationToMolecules(-aspiringPit.getAdditionDelta().getValue()).getValue());
            // remove from node
            aspiringPit.getAssociatedNode().addPotentialDelta(aspiringPit.getAdditionDelta());
            // add to pit (negate actual value)
            aspiringPit.setCargoConcentration(aspiringPit.getCargoConcentration() - aspiringPit.getAdditionDelta().getValue());
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
            if (aspiringPit.getCargoConcentration() >= criticalConcentration) {
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
        ChemicalEntity cargo = getFeature(Cargo.class).getContent();
        for (Pit abortedPit : abortedPits) {
            logger.trace("Clathrin-coated pit at {} was aborted.", abortedPit.spawnSite);
            // free reserved concentration
            AutomatonNode associatedNode = abortedPit.getAssociatedNode();
            ConcentrationDelta delta = new ConcentrationDelta(this, associatedNode.getConcentrationContainer().getMembraneSubsection(), cargo, abortedPit.getCargoConcentration());
            associatedNode.addPotentialDelta(delta);
            aspiringPits.remove(abortedPit);
        }
        abortedPits.clear();
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

    public void addMembraneCargo(Quantity<Area> referenceArea, double numberOfEntities, ChemicalEntity chemicalEntity) {
        initialMembraneCargo.put(chemicalEntity, new AbstractMap.SimpleEntry<>(referenceArea, numberOfEntities));
    }

    public List<Pit> getAspiringPits() {
        return aspiringPits;
    }

    @Override
    public String toString() {
        return "Clathrin-mediated endocytosis of " + getFeature(Cargo.class) + " vesicles";
    }

    public class Pit {

        // randomized next spawn time
        private Quantity<Time> checkpointTime;
        // randomized next spawn site
        private Vector2D spawnSite;
        // randomized radius
        private Quantity<Length> spawnRadius;
        // current cargo concentration
        private double cargoConcentration;
        // concentration delta
        private ConcentrationDelta additionDelta;
        // associated node
        private AutomatonNode associatedNode;

        public Pit(Quantity<Time> checkpointTime, Vector2D spawnSite, Quantity<Length> spawnRadius, double cargoConcentration, AutomatonNode associatedNode, ConcentrationDelta additionDelta) {
            this.checkpointTime = checkpointTime;
            this.spawnSite = spawnSite;
            this.spawnRadius = spawnRadius;
            this.cargoConcentration = cargoConcentration;
            this.associatedNode = associatedNode;
            this.additionDelta = additionDelta;
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

        public double getCargoConcentration() {
            return cargoConcentration;
        }

        public void setCargoConcentration(double cargoConcentration) {
            this.cargoConcentration = cargoConcentration;
        }

        public ConcentrationDelta getAdditionDelta() {
            return additionDelta;
        }

        public void setAdditionDelta(ConcentrationDelta additionDelta) {
            this.additionDelta = additionDelta;
        }

        public AutomatonNode getAssociatedNode() {
            return associatedNode;
        }
    }


}
