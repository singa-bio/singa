package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.ComplexEntity;
import bio.singa.simulation.reactions.reactors.ReactionChain;
import bio.singa.simulation.reactions.reactors.ReactionElement;
import bio.singa.simulation.model.sections.CellTopology;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cl
 */
public class RuleBasedReactantBehavior implements ReactantBehavior {

    private ReactionChain reactionChain;
    private List<ReactantSet> reactantSets;

    public RuleBasedReactantBehavior(ReactionChain reactionChain) {
        this.reactionChain = reactionChain;
        reactantSets = new ArrayList<>();
    }

    public RuleBasedReactantBehavior() {
        reactantSets = new ArrayList<>();
    }

    public void prepareReactionSets() {
        for (ReactionElement reactantElement : reactionChain.getReactantElements()) {
            List<Reactant> substrates = new ArrayList<>();
            for (ComplexEntity substrate : reactantElement.getSubstrates()) {
                Reactant reactant = new Reactant(substrate, ReactantRole.SUBSTRATE, determineNativeTopology(substrate));
                substrates.add(reactant);
            }

            List<Reactant> products = new ArrayList<>();
            for (ComplexEntity substrate : reactantElement.getProducts()) {
                Reactant reactant = new Reactant(substrate, ReactantRole.PRODUCT, determineNativeTopology(substrate));
                products.add(reactant);
            }
            reactantSets.add(new ReactantSet(substrates, products));
        }
    }

    private CellTopology determineNativeTopology(ChemicalEntity entity) {
        if (entity.isMembraneBound()) {
            return CellTopology.MEMBRANE;
        } else {
            return CellTopology.INNER;
        }
    }

    public void setReactantSets(List<ReactantSet> reactantSets) {
        this.reactantSets = reactantSets;
    }

    public void addReactantSet(ReactantSet reactantSet) {
        reactantSets.add(reactantSet);
    }

    @Override
    public List<ReactantSet> getReactantSets() {
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
