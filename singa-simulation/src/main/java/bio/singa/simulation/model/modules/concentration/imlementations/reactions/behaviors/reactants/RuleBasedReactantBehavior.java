package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.complex.GraphComplex;
import bio.singa.chemistry.reactions.reactors.ReactionChain;
import bio.singa.chemistry.reactions.reactors.ReactionElement;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Updatable;

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

    public void prepareReactionSets() {
        for (ReactionElement reactantElement : reactionChain.getReactantElements()) {
            List<Reactant> substrates = new ArrayList<>();
            for (GraphComplex substrate : reactantElement.getSubstrates()) {
                Reactant reactant = new Reactant(substrate, ReactantRole.SUBSTRATE, determineNativeTopology(substrate));
                substrates.add(reactant);
            }

            List<Reactant> products = new ArrayList<>();
            for (GraphComplex substrate : reactantElement.getProducts()) {
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
