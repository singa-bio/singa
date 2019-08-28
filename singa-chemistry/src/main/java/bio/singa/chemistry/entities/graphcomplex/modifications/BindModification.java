package bio.singa.chemistry.entities.graphcomplex.modifications;

import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;

/**
 * @author cl
 */
public class BindModification extends AbstractComplexEntityModification {

    public BindModification(BindingSite bindingSite) {
        super(bindingSite);
    }

    @Override
    public void apply() {
        if (getCandidates().size() != 2) {
            logger.warn("Bind modifications only accept two candidates and return one result.");
        }
        GraphComplex first = getCandidates().get(0);
        GraphComplex second = getCandidates().get(1);
        apply(first, second);
    }

    public void apply(GraphComplex first, GraphComplex second) {
        first.bind(second, getBindingSite())
                .ifPresent(this::addResult);
    }

    @Override
    public String toString() {
        return String.format("binding at %s", getBindingSite());
    }
}
