package bio.singa.chemistry.reactions.conditions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.complex.GraphComplex;

/**
 * @author cl
 */
public class HasNoMoreThanNumberOfPartners implements CandidateCondition {

    private final ChemicalEntity chemicalEntity;
    private final int numberOfPartners;

    public HasNoMoreThanNumberOfPartners(ChemicalEntity chemicalEntity, int numberOfPartners) {
        this.chemicalEntity = chemicalEntity;
        this.numberOfPartners = numberOfPartners;
    }

    @Override
    public boolean test(GraphComplex graphComplex) {
        return graphComplex.getNodes().stream()
                .filter(node -> node.isEntity(chemicalEntity))
                .anyMatch(match -> match.getNeighbours().size() <= numberOfPartners);
    }

}
