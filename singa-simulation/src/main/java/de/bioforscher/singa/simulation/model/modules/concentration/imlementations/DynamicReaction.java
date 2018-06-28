package de.bioforscher.singa.simulation.model.modules.concentration.imlementations;

import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.simulation.features.scale.AppliedScale;
import de.bioforscher.singa.simulation.model.modules.concentration.functions.SectionDeltaFunction;
import de.bioforscher.singa.simulation.model.modules.concentration.reactants.CatalyticReactant;
import de.bioforscher.singa.simulation.model.modules.concentration.reactants.KineticLaw;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cl
 */
public class DynamicReaction extends Reaction {

    /**
     * The catalytic reactants for this reaction.
     */
    private List<CatalyticReactant> catalyticReactants;

    /**
     * The kinetic law for this reaction.
     */
    private KineticLaw kineticLaw;

    @Override
    public void initialize() {
        // apply
        setApplicationCondition(updatable -> true);
        // function
        SectionDeltaFunction function = new SectionDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(RateConstant.class);
        // reference module in simulation
        addModuleToSimulation();
    }

    /**
     * Returns the kinetic law.
     * @return The kinetic law.
     */
    public KineticLaw getKineticLaw() {
        return kineticLaw;
    }

    /**
     * Sets the kinetic law.
     * @param kineticLaw The kinetic law.
     */
    public void setKineticLaw(KineticLaw kineticLaw) {
        this.kineticLaw = kineticLaw;
    }

    /**
     * Returns the catalytic reactants.
     * @return The catalytic reactants.
     */
    public List<CatalyticReactant> getCatalyticReactants() {
        return catalyticReactants;
    }

    /**
     * Sets the catalytic reactants.
     * @param catalyticReactants The catalytic reactants.
     */
    public void setCatalyticReactants(List<CatalyticReactant> catalyticReactants) {
        this.catalyticReactants = catalyticReactants;
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return new HashSet<>();
    }

    @Override
    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        kineticLaw.setCurrentCellSection(supplier.getCurrentSubsection());
        kineticLaw.setAppliedScale(getScaledFeature(AppliedScale.class).getValue().doubleValue());
        return kineticLaw.calculateVelocity(concentrationContainer);
    }

}
