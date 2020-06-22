package bio.singa.simulation.reactions.modifications;

import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ComplexEntity;

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
        ComplexEntity first = getCandidates().get(0);
        ComplexEntity second = getCandidates().get(1);
        apply(first, second);
    }

    public void apply(ComplexEntity first, ComplexEntity second) {
        first.bind(second, getBindingSite())
                .ifPresent(this::addResult);
    }

    @Override
    public String toString() {
        return String.format("binding at %s", getBindingSite());
    }

    @Override
    public ComplexEntityModification invert() {
        ComplexEntityModification modification = ComplexEntityModificationBuilder.release(getBindingSite());
        modification.setPrimaryEntity(getPrimaryEntity());
        modification.setSecondaryEntity(getSecondaryEntity());
        return modification;
    }

}
