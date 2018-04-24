package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Receptor;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.ForwardsRateConstant;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractSectionSpecificModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author cl
 */
public class MonovalentReceptorBinding extends AbstractSectionSpecificModule {

    public static ReceptorStep inSimulation(Simulation simulation) {
        return new MonovalentReceptorBindingBuilder(simulation);
    }

    private Receptor receptor;

    private MonovalentReceptorBinding(Simulation simulation) {
        super(simulation);
        // only in membranes
        onlyApplyIf(node -> node.getState().equals(CellSectionState.MEMBRANE));
        // change of outer phase
        addDeltaFunction(this::calculateDeltas, this::onlyOuterPhase);
    }

    private void initialize() {
        // reference module in simulation
        addModuleToSimulation();
    }

    private List<Delta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        List<Delta> deltas = new ArrayList<>();
        MembraneContainer container = (MembraneContainer) concentrationContainer;
        for (ChemicalEntity ligand : receptor.getLigands()) {
            double velocity = calculateVelocity(container, ligand);
            // change ligand concentration
            deltas.add(new Delta(this, getCurrentCellSection(), ligand,  Quantities.getQuantity(-velocity, EnvironmentalParameters.getTransformedMolarConcentration())));
            // change unbound receptor concentration
            deltas.add(new Delta(this, container.getOuterLayerSection(), receptor,  Quantities.getQuantity(-velocity, EnvironmentalParameters.getTransformedMolarConcentration())));
            // change bound receptor concentration
            deltas.add(new Delta(this, container.getOuterLayerSection(), receptor.getReceptorStateFor(ligand),  Quantities.getQuantity(velocity, EnvironmentalParameters.getTransformedMolarConcentration())));
        }
        return deltas;
    }

    private double calculateVelocity(MembraneContainer membraneContainer, ChemicalEntity ligand) {
        // get rates
        final double forwardsRateConstant = getScaledFeature(ligand, ForwardsRateConstant.class).getValue().doubleValue();
        final double backwardsRateConstant = getScaledFeature(ligand, BackwardsRateConstant.class).getValue().doubleValue();
        // get concentrations
        final double freeLigandConcentration = membraneContainer.getAvailableConcentration(getCurrentCellSection(), ligand).getValue().doubleValue();
        final double freeReceptorConcentration = membraneContainer.getAvailableConcentration(membraneContainer.getOuterLayerSection(), receptor).getValue().doubleValue();
        final double complexConcentration = membraneContainer.getAvailableConcentration(membraneContainer.getOuterLayerSection(), receptor.getReceptorStateFor(ligand)).getValue().doubleValue();
        // calculate velocity
        return forwardsRateConstant * freeReceptorConcentration * freeLigandConcentration - backwardsRateConstant * complexConcentration;
    }


    /**
     * Only apply, if this is the outer phase, the outer phase contains the cargo and the inner layer contains the
     * transporter.
     *
     * @param concentrationContainer
     * @return
     */
    private boolean onlyOuterPhase(ConcentrationContainer concentrationContainer) {
        MembraneContainer container = (MembraneContainer) concentrationContainer;
        return isOuterPhase(container) && containsLigand(container);
    }

    private boolean isOuterPhase(MembraneContainer container) {
        return getCurrentCellSection().equals(container.getOuterPhaseSection());
    }

    private boolean containsLigand(MembraneContainer container) {
        Set<ChemicalEntity> ligands = receptor.getLigands();
        for (ChemicalEntity ligand : ligands) {
            if (container.getAvailableConcentration(getCurrentCellSection(), ligand).getValue().doubleValue() != 0.0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+" ("+receptor.getName()+")";
    }

    public interface ReceptorStep {
        BuildStep receptor(Receptor receptor);
    }

    public interface BuildStep {
        MonovalentReceptorBinding build();
    }

    public static class MonovalentReceptorBindingBuilder implements ReceptorStep, BuildStep {

        private MonovalentReceptorBinding module;

        public MonovalentReceptorBindingBuilder(Simulation simulation) {
            module = new MonovalentReceptorBinding(simulation);
        }

        @Override
        public BuildStep receptor(Receptor receptor) {
            module.receptor = receptor;
            return this;
        }

        @Override
        public MonovalentReceptorBinding build() {
            module.initialize();
            return module;
        }
    }


}
