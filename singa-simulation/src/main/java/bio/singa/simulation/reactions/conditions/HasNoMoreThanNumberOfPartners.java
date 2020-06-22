package bio.singa.simulation.reactions.conditions;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ComplexEntity;

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
    public boolean test(ComplexEntity graphComplex) {
        return graphComplex.getNodes().stream()
                .filter(node -> node.isEntity(chemicalEntity))
                .anyMatch(match -> match.getNeighbours().size() <= numberOfPartners);
    }

    @Override
    public boolean concerns(ChemicalEntity chemicalEntity) {
        return chemicalEntity.equals(this.chemicalEntity);
    }

    @Override
    public boolean concerns(BindingSite bindingSite) {
        return false;
    }
}
