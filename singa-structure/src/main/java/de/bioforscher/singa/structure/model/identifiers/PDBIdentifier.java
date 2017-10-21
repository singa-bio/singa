package de.bioforscher.singa.structure.model.identifiers;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This identifier is used by the <a href="http://www.rcsb.org">Protein Database</a> to identify structures.
 * <p>
 * The identifier is a 4 character code, where the first character is any number followed by three alphanumeric
 * characters.
 *
 * @author cl
 * @see <a href="http://www.rcsb.org/pdb/staticHelp.do?p=help/advancedsearch/pdbIDs.html">PDB identifier</a>
 */
public class PDBIdentifier extends AbstractIdentifier {

    /**
     * The pattern to verify the identifier.
     */
    public static final Pattern PATTERN = Pattern.compile("[0-9][A-Za-z0-9]{3}");

    /**
     * Creates a new identifier.
     *
     * @param identifier The identifier.
     * @throws IllegalArgumentException If the identifier not valid.
     */
    public PDBIdentifier(String identifier) throws IllegalArgumentException {
        super(identifier, PATTERN);
    }

    /**
     * Extracts the first occurrence of any PDB identifier from a string.
     *
     * @param line The string to extract from.
     * @return The first occurrence of any PDB identifier from a string, and {@code null} if no PDB identifier could be
     * found.
     */
    public static String extractFirst(String line) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

}
