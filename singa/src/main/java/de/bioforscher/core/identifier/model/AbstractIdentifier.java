package de.bioforscher.core.identifier.model;

import java.util.regex.Pattern;

public class AbstractIdentifier implements Identifier {

    private final String identifier;

    public AbstractIdentifier(String identifier, Pattern pattern) throws IllegalArgumentException {
        if (pattern.matcher(identifier).matches()) {
            this.identifier = identifier;
        } else {
            throw new IllegalArgumentException("The identifer \"" + identifier + "\" is no valid " + this.getClass().getSimpleName().toString() + ".");
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

}
