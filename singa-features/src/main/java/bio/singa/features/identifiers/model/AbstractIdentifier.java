package bio.singa.features.identifiers.model;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.StringFeature;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The most basic, abstract implementation of {@link Identifier}. Validates the identifier during instantiation, also
 * overrides equals, hashcode and toString methods.
 *
 * @author cl
 */
public abstract class AbstractIdentifier extends StringFeature implements Identifier  {

    public AbstractIdentifier(String identifier, Pattern pattern) {
        this(identifier, pattern, Evidence.NO_EVIDENCE);
    }

    public AbstractIdentifier(String identifier, Pattern pattern, Evidence evidence) throws IllegalArgumentException {
        super(identifier, evidence);
        if (!pattern.matcher(identifier).matches()) {
            throw new IllegalArgumentException("The identifer \"" + identifier + "\" is no valid " +
                    getClass().getSimpleName() + ".");
        }
    }

    public AbstractIdentifier(String identifier, Pattern pattern, Evidence ... evidence) throws IllegalArgumentException {
        super(identifier, Arrays.asList(evidence));
        if (!pattern.matcher(identifier).matches()) {
            throw new IllegalArgumentException("The identifer \"" + identifier + "\" is no valid " +
                    getClass().getSimpleName() + ".");
        }
    }

    @Override
    public String toString() {
        return getContent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractFeature<?> that = (AbstractFeature<?>) o;
        return Objects.equals(featureContent, that.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureContent);
    }

}
