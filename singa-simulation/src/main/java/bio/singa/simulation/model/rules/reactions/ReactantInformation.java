package bio.singa.simulation.model.rules.reactions;

import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;

import java.util.List;

/**
 * @author cl
 */
public class ReactantInformation {

    private Reactant reactant;
    private List<ReactantCondition> conditions;
    private List<ReactantModification> modifications;

    public ReactantInformation(Reactant reactant) {
        this.reactant = reactant;
    }

    public ReactantInformation(Reactant reactant, List<ReactantCondition> conditions, List<ReactantModification> modifications) {
        this.reactant = reactant;
        this.conditions = conditions;
        this.modifications = modifications;
    }

    public Reactant getReactant() {
        return reactant;
    }

    public void setReactant(Reactant reactant) {
        this.reactant = reactant;
    }

    public List<ReactantCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<ReactantCondition> conditions) {
        this.conditions = conditions;
    }

    public List<ReactantModification> getModifications() {
        return modifications;
    }

    public void setModifications(List<ReactantModification> modifications) {
        this.modifications = modifications;
    }
}
