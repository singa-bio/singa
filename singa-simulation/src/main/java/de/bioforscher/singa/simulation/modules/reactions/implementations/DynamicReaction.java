package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.simulation.features.scale.AppliedScale;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.model.CatalyticReactant;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The velocity of dynamic reactions is determined by {@link DynamicKineticLaw}s that can be defined by arbitrary
 * functions.
 *
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
    private DynamicKineticLaw kineticLaw;

    /**
     * Creates a new dynamic reaction.
     * @param simulation The associated simulation.
     * @param kineticLaw The kinetic law for this reaction.
     */
    public DynamicReaction(Simulation simulation, DynamicKineticLaw kineticLaw) {
        super(simulation);
        this.kineticLaw = kineticLaw;
        catalyticReactants = new ArrayList<>();
        // assign dimensionless scaling factor as feature that is rescaled with time steps
        availableFeatures.add(AppliedScale.class);
        setFeature(new AppliedScale());
        // delta function
        addDeltaFunction(this::calculateDeltas, bioNode -> true);
    }

    /**
     * Returns the kinetic law.
     * @return The kinetic law.
     */
    public DynamicKineticLaw getKineticLaw() {
        return kineticLaw;
    }

    /**
     * Sets the kinetic law.
     * @param kineticLaw The kinetic law.
     */
    public void setKineticLaw(DynamicKineticLaw kineticLaw) {
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
        kineticLaw.setCurrentCellSection(getCurrentCellSection());
        kineticLaw.setAppliedScale(getScaledFeature(AppliedScale.class).getValue().doubleValue());
        return kineticLaw.calculateVelocity(concentrationContainer);
    }

}
