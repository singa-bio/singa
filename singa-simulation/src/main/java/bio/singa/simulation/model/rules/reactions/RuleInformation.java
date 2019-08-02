package bio.singa.simulation.model.rules.reactions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class RuleInformation {

    private List<Reactant> reactants;
    private List<ReactantModification> modifications;
    private List<ReactantCondition> conditions;

    public RuleInformation() {
        reactants = new ArrayList<>();
        modifications = new ArrayList<>();
        conditions = new ArrayList<>();
    }

    public boolean containsReactant(ChemicalEntity entity) {
        return reactants.stream()
                .map(Reactant::getEntity)
                .anyMatch(mappedEntity -> mappedEntity.equals(entity));
    }

    public List<Reactant> getReactants() {
        return reactants;
    }

    public void setReactants(List<Reactant> reactants) {
        this.reactants = reactants;
    }

    public List<ReactantModification> getModifications() {
        return modifications;
    }

    public void setModifications(List<ReactantModification> modifications) {
        this.modifications = modifications;
    }

    public List<ReactantCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<ReactantCondition> conditions) {
        this.conditions = conditions;
    }

}
