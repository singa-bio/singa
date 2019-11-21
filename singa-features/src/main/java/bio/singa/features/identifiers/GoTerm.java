package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.AbstractIdentifier;
import bio.singa.features.model.Evidence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static bio.singa.features.model.Evidence.*;

/**
 * @author cl
 */
public class GoTerm extends AbstractIdentifier {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("GO:(\\d{7})");
    /**
     * The full term.
     */
    private String term;

    public static final Evidence GOA_DATABASE = new Evidence(SourceType.DATABASE, "Huntley 2014", "Huntley, Rachael P., et al. \"The GOA database: gene ontology annotation updates for 2015.\" Nucleic acids research 43.D1 (2014): D1057-D1063.");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public GoTerm(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    public GoTerm(String identifier, String term) throws IllegalArgumentException {
        super(identifier, PATTERN);
        this.term = term;
    }

    public GoTerm(String identifier, String term, Evidence evidence) throws IllegalArgumentException {
        super(identifier, PATTERN, evidence);
        this.term = term;
    }

    /**
     * Returns the human-readable description of this term.
     *
     * @return A human-readable description.
     */
    public String getTerm() {
        return term;
    }

    /**
     * Returns the consecutive number without the "GO:" part.
     *
     * @return The consecutive number without the "GO:" part.
     */
    public int getConsecutiveNumber() {
        Matcher matcherGo = PATTERN.matcher(getContent());
        if (matcherGo.matches()) {
            return Integer.parseInt(matcherGo.group(1));
        } else {
            // should not be possible
            throw new IllegalStateException("This identifier has been created with an unexpected pattern.");
        }
    }

}
