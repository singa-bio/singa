package bio.singa.features.identifiers;

import bio.singa.features.identifiers.model.AbstractIdentifier;
import bio.singa.features.model.Evidence;

import java.util.regex.Pattern;

/**
 * This mnemonic entry name is used by the <a href="http://www.uniprot.org/">Uniprot Database</a> to identify proteins.
 * <p>
 * The identifier is a combination of numbers and letters, separated by underscore.
 *
 * @author cl
 * @see <a href="https://www.uniprot.org/help/entry_name">Uniprot entry name</a>
 */
public class UniProtEntryName extends AbstractIdentifier<UniProtEntryName> {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("([A-Z0-9]{1,5}_[A-Z0-9]{1,5}|[A-Z0-9]{1,5})");

    public UniProtEntryName(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    public UniProtEntryName(String identifier, Evidence evidence) throws IllegalArgumentException {
        super(identifier, PATTERN, evidence);
    }

    @Override
    public UniProtEntryName getFeatureContent() {
        return this;
    }
}
