package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.deltas;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;

/**
 * @author cl
 */
public class ReactantConcentration {

    private final Reactant reactant;
    private final double concentration;

    public ReactantConcentration(Reactant reactant, double concentration) {
        this.reactant = reactant;
        this.concentration = concentration;
    }

    public Reactant getReactant() {
        return reactant;
    }

    public double getConcentration() {
        return concentration;
    }

}
