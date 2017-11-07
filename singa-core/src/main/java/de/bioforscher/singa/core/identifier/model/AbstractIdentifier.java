package de.bioforscher.singa.core.identifier.model;

import java.util.regex.Pattern;

/**
 * The most basic, abstract implementation of {@link Identifier}. Validates the identifier during instantiation, also
 * overrides equals, hashcode and toString methods.
 *
 * @author cl
 */
public class AbstractIdentifier implements Identifier {

    /**
     * The identifier in string form.
     */
    private final String identifier;

    /**
     * Creates a new identifier by validating it with the given pattern.
     * @param identifier The new identifier.
     * @param pattern A pattern to validate with.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public AbstractIdentifier(String identifier, Pattern pattern) throws IllegalArgumentException {
        if (pattern.matcher(identifier).matches()) {
            this.identifier = identifier;
        } else {
            throw new IllegalArgumentException("The identifer \"" + identifier + "\" is no valid " +
                    getClass().getSimpleName() + ".");
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractIdentifier that = (AbstractIdentifier) o;
        return identifier != null ? identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }
}
