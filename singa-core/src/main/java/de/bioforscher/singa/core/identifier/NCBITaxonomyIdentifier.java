package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;

import java.util.regex.Pattern;

/**
 * @author cl
 */
public class NCBITaxonomyIdentifier extends AbstractIdentifier {

    public static final Pattern PATTERN = Pattern.compile("[\\d]+");

    public NCBITaxonomyIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }
}
