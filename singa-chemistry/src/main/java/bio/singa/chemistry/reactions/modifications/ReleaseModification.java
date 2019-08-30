package bio.singa.chemistry.reactions.modifications;

import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;

/**
 * @author cl
 */
public class ReleaseModification extends AbstractComplexEntityModification {

    public ReleaseModification(BindingSite bindingSite) {
        super(bindingSite);
    }

    @Override
    public void apply() {
        if (getCandidates().size() != 1) {
            logger.warn("Release modifications only accept one candidate and return two results.");
        }
        ComplexEntity candidate = getCandidates().get(0);
        apply(candidate);
    }

    public void apply(ComplexEntity complex) {
        complex.unbind(getBindingSite())
                .ifPresent(this::addAllResults);
    }

    @Override
    public String toString() {
        return String.format("releasing at %s", getBindingSite());
    }

    @Override
    public ComplexEntityModification invert() {
        throw new IllegalStateException("Not yet implemented");
    }

}
