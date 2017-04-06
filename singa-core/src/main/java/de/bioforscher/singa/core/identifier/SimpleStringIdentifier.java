package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;

import java.util.regex.Pattern;

/**
 * @author cl
 */
public class SimpleStringIdentifier extends AbstractIdentifier {

    public static final Pattern PATTERN = Pattern.compile(".*");

    public SimpleStringIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier,  PATTERN);
    }



}
