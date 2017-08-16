package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.implementations.DynamicKineticLaw;
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
        this.kineticLaw = kineticLaw;
        this.catalyticReactants = new ArrayList<>();
    }

    public DynamicReaction(DynamicKineticLaw kineticLaw) {
        super();
        this.kineticLaw = kineticLaw;
        this.catalyticReactants = new ArrayList<>();
    }

    public DynamicKineticLaw getKineticLaw() {
        return this.kineticLaw;
    }

    public void setKineticLaw(DynamicKineticLaw kineticLaw) {
        this.kineticLaw = kineticLaw;
    }

    public List<CatalyticReactant> getCatalyticReactants() {
        return this.catalyticReactants;
    }

    public void setCatalyticReactants(List<CatalyticReactant> catalyticReactants) {
        this.catalyticReactants = catalyticReactants;
    }

    @Override
    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        kineticLaw.setCurrentCellSection(getCurrentCellSection());
        return this.kineticLaw.calculateVelocity(concentrationContainer);
    }

}
