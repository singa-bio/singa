package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.AbstractIdentifier;
import bio.singa.features.model.Evidence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class GoTerm extends AbstractIdentifier {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("GO:(\\d{7})");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public GoTerm(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    public GoTerm(String identifier, Evidence evidence) throws IllegalArgumentException {
        super(identifier, PATTERN, evidence);
    }

    /**
     * Returns the consecutive number without the "GO:" part.
     *
     * @return The consecutive number without the "GO:" part.
     */
    public int getConsecutiveNumber() {
        Matcher matcherCHEBI = PATTERN.matcher(getContent());
        if (matcherCHEBI.matches()) {
            return Integer.parseInt(matcherCHEBI.group(1));
        } else {
            // should not be possible
            throw new IllegalStateException("This identifier has been created with an unexpected pattern.");
        }
    }

}
