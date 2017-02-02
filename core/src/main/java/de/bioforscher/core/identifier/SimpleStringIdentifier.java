package de.bioforscher.core.identifier;

import de.bioforscher.core.identifier.model.AbstractIdentifier;

import java.util.regex.Pattern;

/**
 * Created by Christoph on 04/11/2016.
 */
public class SimpleStringIdentifier extends AbstractIdentifier {

    public static final Pattern PATTERN = Pattern.compile(".*");

    public SimpleStringIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier,  PATTERN);
    }



}
