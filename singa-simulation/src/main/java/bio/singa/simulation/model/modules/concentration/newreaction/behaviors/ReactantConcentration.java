package bio.singa.simulation.model.modules.concentration.newreaction.behaviors;

import bio.singa.simulation.model.modules.concentration.reactants.Reactant;

/**
 * @author cl
 */
public class ReactantConcentration {

    private final Reactant entity;
    private final double concentration;

    public ReactantConcentration(Reactant entity, double concentration) {
        this.entity = entity;
        this.concentration = concentration;
    }

    public Reactant getEntity() {
        return entity;
    }

    public double getConcentration() {
        return concentration;
    }

}
