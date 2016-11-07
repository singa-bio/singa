package de.bioforscher.core.identifier.model;

import java.util.regex.Pattern;

public class AbstractIdentifier implements Identifier {

    private final String identifier;

    public AbstractIdentifier(String identifier, Pattern pattern) throws IllegalArgumentException {
        if (pattern.matcher(identifier).matches()) {
            this.identifier = identifier;
        } else {
            throw new IllegalArgumentException("The identifer \"" + identifier + "\" is no valid " + this.getClass()
                    .getSimpleName() + ".");
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
        return this.identifier != null ? this.identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return this.identifier != null ? this.identifier.hashCode() : 0;
    }
}
