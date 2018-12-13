package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.AbstractIdentifier;

import java.util.regex.Pattern;

/**
 * This identifier is just an encapsulated string. Take care the the identifier is nevertheless used to identify any
 * object, so using each identifier once is advisable.
 *
 * @author cl
 */
public class SimpleStringIdentifier extends AbstractIdentifier{

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile(".*");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public SimpleStringIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

}
