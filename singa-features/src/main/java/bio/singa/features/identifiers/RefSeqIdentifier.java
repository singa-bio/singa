package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.AbstractIdentifier;

import java.util.regex.Pattern;

/**
 * A RefSeq identifier as specified by NCBI.
 *
 * @see <a href="https://www.ncbi.nlm.nih.gov/books/NBK50679/">RefSeq Frequently Asked Questions</a>
 */
public class RefSeqIdentifier extends AbstractIdentifier{

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("[A-Z]{2}_([\\d]+)\\.\\d+");

    public RefSeqIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

}
