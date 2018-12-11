package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.AbstractIdentifier;
import bio.singa.features.model.Evidence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This identifier is used by the <a href="https://www.ebi.ac.uk/chebi/">ChEBI Database</a> to identify small molecules.
 * <p>
 * The identifier consists of a unique number preceded by the "CHEBI:" suffix.
 *
 * @author cl
 * @see <a href="https://www.ebi.ac.uk/chebi/faqForward.do#5">ChEBI identifier</a>
 */
public class ChEBIIdentifier extends AbstractIdentifier<ChEBIIdentifier> {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("CHEBI:([\\d]+)");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public ChEBIIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    public ChEBIIdentifier(String identifier, Evidence evidence) throws IllegalArgumentException {
        super(identifier, PATTERN, evidence);
    }

    /**
     * Returns the consecutive number without the "CHEBI:" part.
     *
     * @return The consecutive number without the "CHEBI:" part.
     */
    public int getConsecutiveNumber() {
        Matcher matcherCHEBI = PATTERN.matcher(getIdentifier());
        if (matcherCHEBI.matches()) {
            return Integer.parseInt(matcherCHEBI.group(1));
        } else {
            // should no be possible
            throw new IllegalStateException("This identifier has been created with an unexpected pattern.");
        }
    }

    @Override
    public ChEBIIdentifier getFeatureContent() {
        return this;
    }
}
