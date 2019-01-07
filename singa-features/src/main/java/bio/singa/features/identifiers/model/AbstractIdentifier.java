package bio.singa.features.identifiers.model;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.StringFeature;

import java.util.regex.Pattern;

/**
 * The most basic, abstract implementation of {@link Identifier}. Validates the identifier during instantiation, also
 * overrides equals, hashcode and toString methods.
 *
 * @author cl
 */
public abstract class AbstractIdentifier extends StringFeature implements Identifier  {

    public AbstractIdentifier(String identifier, Pattern pattern) {
        this(identifier, pattern, null);
    }

    public AbstractIdentifier(String identifier, Pattern pattern, Evidence evidence) throws IllegalArgumentException {
        super(identifier, evidence);
        if (!pattern.matcher(identifier).matches()) {
            throw new IllegalArgumentException("The identifer \"" + identifier + "\" is no valid " +
                    getClass().getSimpleName() + ".");
        }
    }

    @Override
    public String toString() {
        return getContent();
    }
}
