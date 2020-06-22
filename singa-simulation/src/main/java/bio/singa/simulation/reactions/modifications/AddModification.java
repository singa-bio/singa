package bio.singa.simulation.reactions.modifications;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ComplexEntity;

/**
 * @author cl
 */
public class AddModification extends AbstractComplexEntityModification {

    public AddModification(BindingSite bindingSite, ChemicalEntity complexToAdd) {
        super(bindingSite);
        setSecondaryEntity(complexToAdd);
    }

    @Override
    public void apply() {
        if (getCandidates().size() != 1) {
            logger.warn("Add modifications only accept one candidate and return one result.");
        }
        ComplexEntity candidate = getCandidates().get(0);
        apply(candidate);
    }

    public void apply(ComplexEntity complex) {
        complex.add(getSecondaryEntity(), getBindingSite())
                .ifPresent(this::addResult);
    }

    @Override
    public String toString() {
        return String.format("adding %s at %s", getSecondaryEntity(), getBindingSite());
    }

    @Override
    public ComplexEntityModification invert() {
        throw new IllegalStateException("Not yet implemented");
    }

}
