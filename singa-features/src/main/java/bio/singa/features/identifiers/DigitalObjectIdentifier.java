package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.AbstractIdentifier;

import java.util.regex.Pattern;

/**
 * @author cl
 */
public class DigitalObjectIdentifier extends AbstractIdentifier {


    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("^10.\\d{4,9}/[-._;()/:A-Za-z0-9]+$");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public DigitalObjectIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    /**
     * Returns the pattern used to validate the identifier.
     *
     * @return The pattern used to validate the identifier.
     */
    public static Pattern getPattern() {
        return PATTERN;
    }

}
