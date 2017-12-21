package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.simulation.features.scale.AppliedScale;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.model.CatalyticReactant;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class DynamicReaction extends Reaction {

    private List<CatalyticReactant> catalyticReactants;
    private DynamicKineticLaw kineticLaw;

    public DynamicReaction(Simulation simulation, DynamicKineticLaw kineticLaw) {
        super(simulation);
        initialize(kineticLaw);
    }

    private void initialize(DynamicKineticLaw kineticLaw) {
        this.kineticLaw = kineticLaw;
        catalyticReactants = new ArrayList<>();
        // features
        availableFeatures.add(AppliedScale.class);
        setFeature(new AppliedScale());
        // deltas
        addDeltaFunction(this::calculateDeltas, bioNode -> true);
    }

    public DynamicKineticLaw getKineticLaw() {
        return kineticLaw;
    }

    public void setKineticLaw(DynamicKineticLaw kineticLaw) {
        this.kineticLaw = kineticLaw;
    }

    public List<CatalyticReactant> getCatalyticReactants() {
        return catalyticReactants;
    }

    public void setCatalyticReactants(List<CatalyticReactant> catalyticReactants) {
        this.catalyticReactants = catalyticReactants;
    }

    @Override
    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        kineticLaw.setCurrentCellSection(getCurrentCellSection());
        kineticLaw.setAppliedScale(getScaledFeature(AppliedScale.class));
        return kineticLaw.calculateVelocity(concentrationContainer);
    }

}
