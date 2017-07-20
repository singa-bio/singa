package de.bioforscher.singa.simulation.modules.membranetransport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.features.permeability.MembraneEntry;
import de.bioforscher.singa.simulation.features.permeability.MembraneExit;
import de.bioforscher.singa.simulation.features.permeability.MembraneFlipFlop;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class PassiveMembraneTransport implements Module {

    private Quantity<Time> fullTimeStep;
    private Quantity<Time> halfTimeStep;

    enum DeltaSection {
        OUTER_PHASE, OUTER_LAYER, INNER_LAYER, INNER_PHASE
    }

    private static final Logger logger = LoggerFactory.getLogger(PassiveMembraneTransport.class);
    private static final double epsilon = 0.0001;

    private Set<ChemicalEntity<?>> chemicalEntities;
    private double kIn;
    private double kOut;
    private double kFlip;

    private Map<DeltaSection, Double> fullDeltas;
    private MembraneContainer fullConcentrations;
    private Map<DeltaSection, Double> halfDeltas;
    private MembraneContainer halfConcentrations;
    private Map<DeltaSection, CellSection> sectionMapping;

    private Set<Delta> temporaryDeltas;

    public PassiveMembraneTransport() {
        this.chemicalEntities = new HashSet<>();
        this.temporaryDeltas = new HashSet<>();
        this.fullDeltas = new EnumMap<>(DeltaSection.class);
        this.halfDeltas = new EnumMap<>(DeltaSection.class);
        this.sectionMapping = new EnumMap<>(DeltaSection.class);
    }

    @Override
    public void applyTo(AutomatonGraph graph) {
        for (BioNode node : graph.getNodes()) {
            if (node.getState() == NodeState.MEMBRANE) {
                for (ChemicalEntity<?> entity : node.getAllReferencedEntities()) {
                    applyDeltas(node, entity);
                }
            }
        }
    }

    @Override
    public Set<ChemicalEntity<?>> collectAllReferencedEntities() {
        return this.chemicalEntities;
    }

    public Set<Delta> determineUpdate(BioNode node, ChemicalEntity<?> entity) {
        // determines the update using the midpoint method

        // clear previous deltas
        this.temporaryDeltas.clear();
        // get current concentrations
        this.fullConcentrations = (MembraneContainer) node.getConcentrations();
        // create now container for temporary storage of half step concentrations
        this.halfConcentrations = new MembraneContainer(this.fullConcentrations.getOuterPhaseSection(), this.fullConcentrations.getInnerPhaseSection(), this.fullConcentrations.getMembrane());

        double localError = 1.0;

        while (localError > epsilon) {
            // set full time step
            this.fullTimeStep = EnvironmentalParameters.getInstance().getTimeStep();
            setUpParameters(entity, this.fullTimeStep);
            // determine deltas for full time step
            determineFullDeltas(entity);
            // set half time step
            this.halfTimeStep = EnvironmentalParameters.getInstance().getTimeStep().divide(2.0);
            setUpParameters(entity, this.halfTimeStep);
            // determine deltas for half time step
            determineHalfDeltas(entity);
            // determine biggest local error
            localError = determineBiggestError();
            // evaluate error by increasing or decreasing time step
            evaluateLocalError(localError);
        }

        // register deltas
        registerDelta(this.fullConcentrations.getOuterPhaseSection(), entity, this.halfDeltas.get(DeltaSection.OUTER_PHASE));
        registerDelta(this.fullConcentrations.getInnerLayerSection(), entity, this.halfDeltas.get(DeltaSection.INNER_LAYER));
        registerDelta(this.fullConcentrations.getOuterLayerSection(), entity, this.halfDeltas.get(DeltaSection.OUTER_LAYER));
        registerDelta(this.fullConcentrations.getInnerPhaseSection(), entity, this.halfDeltas.get(DeltaSection.INNER_PHASE));

        return this.temporaryDeltas;
    }


    public void applyDeltas(BioNode node, ChemicalEntity<?> entity) {
        // calculateDeltas
        Set<Delta> deltas = determineUpdate(node, entity);
        // add deltas tp node
        deltas.forEach(node::addDelta);
    }

    private void registerDelta(CellSection cellSection, ChemicalEntity<?> chemicalEntity, double delta) {
        if (delta != 0.0) {
            // only register, if there is any change
            this.temporaryDeltas.add(new Delta(cellSection, chemicalEntity, Quantities.getQuantity(delta, MOLE_PER_LITRE)));
        }
    }

    private void determineFullDeltas(ChemicalEntity<?> entity) {
        for (DeltaSection section : DeltaSection.values()) {
            determineFullDelta(section, entity);
        }
    }

    private void determineFullDelta(DeltaSection section, ChemicalEntity<?> entity) {
        final double fullConcentration = getConcentration(section, entity);
        final double fullDelta = calculateDelta(section, entity, this.fullConcentrations);
        this.fullDeltas.put(section, fullDelta);
        final double halfStepConcentration = fullConcentration + 0.5 * fullDelta;
        setConcentration(section, entity, halfStepConcentration);
    }

    private double getConcentration(DeltaSection section, ChemicalEntity<?> entity) {
        switch (section) {
            case OUTER_PHASE:
                return this.fullConcentrations.getOuterPhaseConcentration(entity).getValue().doubleValue();
            case OUTER_LAYER:
                return this.fullConcentrations.getOuterMembraneLayerConcentration(entity).getValue().doubleValue();
            case INNER_LAYER:
                return this.fullConcentrations.getInnerMembraneLayerConcentration(entity).getValue().doubleValue();
            case INNER_PHASE:
                return this.fullConcentrations.getInnerPhaseConcentration(entity).getValue().doubleValue();
        }
        throw new IllegalStateException("Could not get concentration for section " + section.name());
    }

    private void setConcentration(DeltaSection section, ChemicalEntity<?> entity, double concentration) {
        Quantity<MolarConcentration> quantity = Quantities.getQuantity(concentration, MOLE_PER_LITRE);
        switch (section) {
            case OUTER_PHASE:
                this.halfConcentrations.setAvailableConcentration(this.fullConcentrations.getOuterPhaseSection(), entity, quantity);
                break;
            case OUTER_LAYER:
                this.halfConcentrations.setAvailableConcentration(this.fullConcentrations.getOuterLayerSection(), entity, quantity);
                break;
            case INNER_LAYER:
                this.halfConcentrations.setAvailableConcentration(this.fullConcentrations.getInnerLayerSection(), entity, quantity);
                break;
            case INNER_PHASE:
                this.halfConcentrations.setAvailableConcentration(this.fullConcentrations.getInnerPhaseSection(), entity, quantity);
                break;
        }
    }

    private void determineHalfDeltas(ChemicalEntity<?> entity) {
        for (DeltaSection section : DeltaSection.values()) {
            determineHalfDelta(section, entity);
        }
    }

    private void determineHalfDelta(DeltaSection section, ChemicalEntity<?> entity) {
        double halfDelta = 2.0 * calculateDelta(section, entity, this.halfConcentrations);
        this.halfDeltas.put(section, halfDelta);
    }

    private double calculateDelta(DeltaSection section, ChemicalEntity<?> entity, MembraneContainer concentrations) {
        switch (section) {
            case OUTER_PHASE:
                return calculateOuterPhaseDelta(entity, concentrations);
            case OUTER_LAYER:
                return calculateOuterLayerDelta(entity, concentrations);
            case INNER_LAYER:
                return calculateInnerLayerDelta(entity, concentrations);
            case INNER_PHASE:
                return calculateInnerPhaseDelta(entity, concentrations);
        }
        throw new IllegalStateException("Could not calculate delta for section " + section.name());
    }

    private double calculateOuterPhaseDelta(ChemicalEntity<?> entity, MembraneContainer concentrations) {
        // (outer phase) outer phase = -kIn * outer phase + kOut * outer layer
        return -this.kIn * concentrations.getOuterPhaseConcentration(entity).getValue().doubleValue() +
                this.kOut * concentrations.getOuterMembraneLayerConcentration(entity).getValue().doubleValue();
    }

    private double calculateOuterLayerDelta(ChemicalEntity<?> entity, MembraneContainer concentrations) {
        // (outer layer) outer layer = kIn * outer phase - (kOut + kFlip) * outer layer + kFlip * inner layer
        return this.kIn * concentrations.getOuterPhaseConcentration(entity).getValue().doubleValue() -
                (this.kOut + this.kFlip) * concentrations.getOuterMembraneLayerConcentration(entity).getValue().doubleValue() +
                this.kFlip * concentrations.getInnerMembraneLayerConcentration(entity).getValue().doubleValue();
    }

    private double calculateInnerLayerDelta(ChemicalEntity<?> entity, MembraneContainer concentrations) {
        // (inner layer) inner layer = kIn * inner phase - (kOut + kFlip) * inner layer + kFlip * outer layer
        return this.kIn * concentrations.getInnerPhaseConcentration(entity).getValue().doubleValue() -
                (this.kOut + this.kFlip) * concentrations.getInnerMembraneLayerConcentration(entity).getValue().doubleValue() +
                this.kFlip * concentrations.getOuterMembraneLayerConcentration(entity).getValue().doubleValue();
    }

    private double calculateInnerPhaseDelta(ChemicalEntity<?> entity, MembraneContainer concentrations) {
        // (inner phase) inner phase = -kIn * inner phase + kOut * inner layer
        return -this.kIn * concentrations.getInnerPhaseConcentration(entity).getValue().doubleValue() +
                this.kOut * concentrations.getInnerMembraneLayerConcentration(entity).getValue().doubleValue();
    }

    private double determineBiggestError() {
        DeltaSection sectionWithLargestError = null;
        double largestError = -Double.MAX_VALUE;
        for (DeltaSection section : DeltaSection.values()) {
            double currentError = determineError(section);
            if (largestError < currentError) {
                largestError = currentError;
                sectionWithLargestError = section;
            }
        }
        logger.debug("The section with the largest local error of {} is {}.", largestError, sectionWithLargestError);
        return largestError;
    }

    private double determineError(DeltaSection section) {
        // only if there is any change there can be a local error
        double fullDelta = this.fullDeltas.get(section);
        double halfDelta = this.halfDeltas.get(section);
        if (fullDelta != 0.0 && halfDelta != 0) {
            // calculate error
            return Math.abs(1 - (fullDelta / halfDelta));

        } else {
            // no changes, no error
            return 0.0;
        }
    }

    private void evaluateLocalError(double localError) {
        // determine whether to increase or reduce time step size
        if (localError > epsilon) {
            logger.trace("Reducing time step and trying again.");
            EnvironmentalParameters.getInstance().setTimeStep(this.halfTimeStep.multiply(0.8));
        } else {
            logger.trace("Increasing time step for the epoch.");
            EnvironmentalParameters.getInstance().setTimeStep(this.fullTimeStep.multiply(1.2));
        }
    }

    private void setUpParameters(ChemicalEntity<?> entity, Quantity<Time> timeStep) {
        // membrane entry (outer phase -> outer layer and inner phase -> inner layer) - kIn
        MembraneEntry membraneEntry = entity.getFeature(MembraneEntry.class);
        membraneEntry.scale(timeStep);
        this.kIn = membraneEntry.getScaledQuantity().getValue().doubleValue();
        // membrane exit (outer layer -> outer phase and inner layer -> inner phase) - kOut
        MembraneExit membraneExit = entity.getFeature(MembraneExit.class);
        membraneExit.scale(timeStep);
        this.kOut = membraneExit.getScaledQuantity().getValue().doubleValue();
        // flip-flip across membrane (outer layer <-> inner layer) - kFlip
        MembraneFlipFlop membraneFlipFlop = entity.getFeature(MembraneFlipFlop.class);
        membraneFlipFlop.scale(timeStep);
        this.kFlip = membraneFlipFlop.getScaledQuantity().getValue().doubleValue();
    }

}
