package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.AbstractIdentifier;

import java.util.regex.Pattern;

/**
 * @author cl
 */
public class ENAAccessionNumber extends AbstractIdentifier<ENAAccessionNumber> {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("[A-Z]{3}\\d{5}\\.\\d+");

    /**
     * Creates a new identifier by validating it with the given pattern.
     *
     * @param identifier The new identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public ENAAccessionNumber(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    @Override
    public ENAAccessionNumber getFeatureContent() {
        return this;
    }


}
