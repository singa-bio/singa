package bio.singa.chemistry.entities.graphcomplex.modifications;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;

/**
 * @author cl
 */
public class RemoveModification extends AbstractComplexEntityModification {


    public RemoveModification(BindingSite bindingSite, ChemicalEntity entityToRemove) {
        super(bindingSite);
        setSecondaryEntity(entityToRemove);
    }

    @Override
    public void apply() {
        if (getCandidates().size() != 1) {
            logger.warn("Remove modifications only accept one candidate and return one result.");
        }
        GraphComplex candidate = getCandidates().get(0);
        apply(candidate);
    }

    public void apply(GraphComplex complex) {
        complex.remove(getSecondaryEntity(), getBindingSite())
                .ifPresent(this::addResult);
    }

    @Override
    public String toString() {
        return String.format("removing %s at %s", getSecondaryEntity(), getBindingSite());
    }

    @Override
    public ComplexEntityModification invert() {
        throw new IllegalStateException("Not yet implemented");
    }

}
