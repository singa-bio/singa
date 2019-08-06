package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cl
 */
public class RuleBasedReactantBehavior implements ReactantBehavior {

    private List<ReactantSet> reactantSets;

    public RuleBasedReactantBehavior(List<ReactantSet> reactantSets) {
        this.reactantSets = reactantSets;
    }

    public List<ReactantSet> getReactantSets() {
        return reactantSets;
    }

    public void setReactantSets(List<ReactantSet> reactantSets) {
        this.reactantSets = reactantSets;
    }

    @Override
    public List<ReactantSet> generateReactantSets(Updatable updatable) {
        return reactantSets;
    }

    @Override
    public List<ChemicalEntity> getReferencedEntities() {
        return Stream.concat(Stream.concat(getSubstrates().stream(), getProducts().stream()), getCatalysts().stream())
                .distinct()
                .map(Reactant::getEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void addReactant(Reactant reactant) {
        return;
    }

    @Override
    public List<Reactant> getSubstrates() {
        return reactantSets.stream()
                .flatMap(reactantSet -> reactantSet.getSubstrates().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Reactant> getProducts() {
        return reactantSets.stream()
                .flatMap(reactantSet -> reactantSet.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Reactant> getCatalysts() {
        return reactantSets.stream()
                .flatMap(reactantSet -> reactantSet.getCatalysts().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
