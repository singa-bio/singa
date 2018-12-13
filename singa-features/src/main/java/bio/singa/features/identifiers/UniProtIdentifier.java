package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.AbstractIdentifier;
import bio.singa.features.model.Evidence;

import java.util.regex.Pattern;

/**
 * This identifier is used by the <a href="http://www.uniprot.org/">Uniprot Database</a> to identify proteins.
 * <p>
 * The identifier is a combination of numbers and letters.
 *
 * @author cl
 * @see <a href="http://www.uniprot.org/help/accession_numbers">Uniprot identifier</a>
 */
public class UniProtIdentifier extends AbstractIdentifier {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public UniProtIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    public UniProtIdentifier(String identifier, Evidence evidence) throws IllegalArgumentException {
        super(identifier, PATTERN, evidence);
    }


}
