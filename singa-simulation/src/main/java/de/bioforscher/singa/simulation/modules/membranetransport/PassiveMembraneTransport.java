package de.bioforscher.singa.simulation.modules.membranetransport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
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
import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class PassiveMembraneTransport implements Module {

    private static final Logger logger = LoggerFactory.getLogger(PassiveMembraneTransport.class);
    private static final double epsilon = 0.0001;

    private Set<ChemicalEntity<?>> chemicalEntities;
    private double kIn;
    private double kOut;
    private double kFlip;

    private Set<Delta> temoraryDeltas;

    public PassiveMembraneTransport() {
        this.chemicalEntities = new HashSet<>();
        this.temoraryDeltas = new HashSet<>();
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
        this.temoraryDeltas.clear();
        // get current concentrations
        MembraneContainer concentrations = (MembraneContainer) node.getConcentrations();
        // create now container for temporary storage of half step concentrations
        MembraneContainer halfConcentrations = new MembraneContainer(concentrations.getOuterPhaseSection(), concentrations.getInnerPhaseSection(), concentrations.getMembrane());

        double outerPhaseDelta = 0.0;
        double outerLayerDelta = 0.0;
        double innerLayerDelta = 0.0;
        double innerPhaseDelta = 0.0;

        double localError = 1.0;

        while (localError > epsilon) {

            // set full time step
            Quantity<Time> fullTimeStep = EnvironmentalParameters.getInstance().getTimeStep();
            setUpParameters(entity, fullTimeStep);

            // outer phase half step concentrations
            double outerPhaseConcentration = concentrations.getOuterPhaseConcentration(entity).getValue().doubleValue();
            double outerPhaseHalfStepConcentration = outerPhaseConcentration + 0.5 * calculateOuterPhaseDelta(entity, concentrations);
            halfConcentrations.setAvailableConcentration(concentrations.getOuterPhaseSection(), entity, Quantities.getQuantity(outerPhaseHalfStepConcentration, MOLE_PER_LITRE));

            // outer layer half step concentrations
            double outerLayerConcentration = concentrations.getOuterMembraneLayerConcentration(entity).getValue().doubleValue();
            double outerLayerHalfStepConcentration = outerLayerConcentration + 0.5 * calculateOuterLayerDelta(entity, concentrations);
            halfConcentrations.setAvailableConcentration(concentrations.getOuterLayerSection(), entity, Quantities.getQuantity(outerLayerHalfStepConcentration, MOLE_PER_LITRE));

            // inner layer half step concentrations
            double innerLayerConcentration = concentrations.getInnerMembraneLayerConcentration(entity).getValue().doubleValue();
            double innerLayerHalfStepConcentration = innerLayerConcentration + 0.5 * calculateInnerLayerDelta(entity, concentrations);
            halfConcentrations.setAvailableConcentration(concentrations.getInnerLayerSection(), entity, Quantities.getQuantity(innerLayerHalfStepConcentration, MOLE_PER_LITRE));

            // inner phase half step concentrations
            double innerPhaseConcentration = concentrations.getInnerPhaseConcentration(entity).getValue().doubleValue();
            double innerPhaseFullDelta = calculateInnerPhaseDelta(entity, concentrations);
            double innerPhaseHalfStepConcentration = innerPhaseConcentration + 0.5 * innerPhaseFullDelta;
            halfConcentrations.setAvailableConcentration(concentrations.getInnerPhaseSection(), entity, Quantities.getQuantity(innerPhaseHalfStepConcentration, MOLE_PER_LITRE));

            // set half time step
            Quantity<Time> halfTimeStep = EnvironmentalParameters.getInstance().getTimeStep().divide(2.0);
            setUpParameters(entity, halfTimeStep);

            // calculate deltas
            outerPhaseDelta = 2.0 * calculateOuterPhaseDelta(entity, halfConcentrations);
            outerLayerDelta = 2.0 * calculateOuterLayerDelta(entity, halfConcentrations);
            innerLayerDelta = 2.0 * calculateInnerLayerDelta(entity, halfConcentrations);
            innerPhaseDelta = 2.0 * calculateInnerPhaseDelta(entity, halfConcentrations);

            // only if there is any change there can be a local error
            if (innerPhaseFullDelta != 0.0 && innerPhaseDelta != 0) {
                // calculate error
                localError = Math.abs(1 - (innerPhaseFullDelta / innerPhaseDelta));
                logger.trace("The local error is {}.", localError);
                // determine whether to increase or reduce time step size
                if (localError > epsilon) {
                    logger.trace("Reducing time step and trying again.");
                    EnvironmentalParameters.getInstance().setTimeStep(halfTimeStep.multiply(0.8));
                } else {
                    logger.trace("Increasing time step for the epoch.");
                    EnvironmentalParameters.getInstance().setTimeStep(fullTimeStep.multiply(1.2));
                }
            } else {
                logger.trace("No change has been detected, continuing.", localError);
                localError = 0.0;
            }
        }

        // register deltas
        registerDelta(concentrations.getOuterPhaseSection(), entity, outerPhaseDelta);
        registerDelta(concentrations.getOuterLayerSection(), entity, outerLayerDelta);
        registerDelta(concentrations.getInnerLayerSection(), entity, innerLayerDelta);
        registerDelta(concentrations.getInnerPhaseSection(), entity, innerPhaseDelta);

        return this.temoraryDeltas;
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
            this.temoraryDeltas.add(new Delta(cellSection, chemicalEntity, Quantities.getQuantity(delta, MOLE_PER_LITRE)));
        }
    }

    private double calculateOuterPhaseDelta(ChemicalEntity<?> entity, MembraneContainer concentrations) {
        // (outer phase) outer phase = -kIn * outer phase + kOut * outer layer
        return -kIn * concentrations.getOuterPhaseConcentration(entity).getValue().doubleValue() +
                kOut * concentrations.getOuterMembraneLayerConcentration(entity).getValue().doubleValue();
    }

    private double calculateOuterLayerDelta(ChemicalEntity<?> entity, MembraneContainer concentrations) {
        // (outer layer) outer layer = kIn * outer phase - (kOut + kFlip) * outer layer + kFlip * inner layer
        return kIn * concentrations.getOuterPhaseConcentration(entity).getValue().doubleValue() -
                (kOut + kFlip) * concentrations.getOuterMembraneLayerConcentration(entity).getValue().doubleValue() +
                kFlip * concentrations.getInnerMembraneLayerConcentration(entity).getValue().doubleValue();
    }

    private double calculateInnerLayerDelta(ChemicalEntity<?> entity, MembraneContainer concentrations) {
        // (inner layer) inner layer = kIn * inner phase - (kOut + kFlip) * inner layer + kFlip * outer layer
        return kIn * concentrations.getInnerPhaseConcentration(entity).getValue().doubleValue() -
                (kOut + kFlip) * concentrations.getInnerMembraneLayerConcentration(entity).getValue().doubleValue() +
                kFlip * concentrations.getOuterMembraneLayerConcentration(entity).getValue().doubleValue();
    }

    private double calculateInnerPhaseDelta(ChemicalEntity<?> entity, MembraneContainer concentrations) {
        // (inner phase) inner phase = -kIn * inner phase + kOut * inner layer
        return -kIn * concentrations.getInnerPhaseConcentration(entity).getValue().doubleValue() +
                kOut * concentrations.getInnerMembraneLayerConcentration(entity).getValue().doubleValue();
    }


    private void setUpParameters(ChemicalEntity<?> entity, Quantity<Time> timeStep) {
        // membrane entry (outer phase -> outer layer and inner phase -> inner layer) - kIn
        MembraneEntry membraneEntry = entity.getFeature(MembraneEntry.class);
        membraneEntry.scale(timeStep);
        kIn = membraneEntry.getScaledQuantity().getValue().doubleValue();
        // membrane exit (outer layer -> outer phase and inner layer -> inner phase) - kOut
        MembraneExit membraneExit = entity.getFeature(MembraneExit.class);
        membraneExit.scale(timeStep);
        kOut = membraneExit.getScaledQuantity().getValue().doubleValue();
        // flip-flip across membrane (outer layer <-> inner layer) - kFlip
        MembraneFlipFlop membraneFlipFlop = entity.getFeature(MembraneFlipFlop.class);
        membraneFlipFlop.scale(timeStep);
        kFlip = membraneFlipFlop.getScaledQuantity().getValue().doubleValue();
    }

}
