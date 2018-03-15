package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;

import java.util.Collection;
import java.util.Optional;
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

    /**
     * Returns true, if the identifier is valid.
     *
     * @param identifier The identifier.
     * @return True, if the identifier is valid.
     */
    public static boolean check(Identifier identifier) {
        return PATTERN.matcher(identifier.toString()).matches();
    }

    /**
     * Searches a valid UniProt identifier in a collection of identifiers and returns it.
     *
     * @param identifiers A collection of identifiers.
     * @return The first UniProt identifier or an empty optional if no identifier could be found.
     */
    public static Optional<Identifier> find(Collection<Identifier> identifiers) {
        for (Identifier identifier : identifiers) {
            if (check(identifier)) {
                return Optional.of(identifier);
            }
        }
        return Optional.empty();
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
