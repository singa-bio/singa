package de.bioforscher.singa.chemistry.parser.pdb.structures.tokens;

import de.bioforscher.singa.core.utility.Range;

import java.util.regex.Pattern;

/**
 * Tokens are used to extract information from PDB lines. Each token is identified by a static record pattern.
 * Afterwards the information from the line can be extracted.
 *
 * @author cl
 */
public interface PDBToken {

    /**
     * Returns the record name for this token.
     * @return The record name for this token.
     */
    Pattern getRecordNamePattern();

    /**
     * Gets the columns (the staring and ending index) where a value can be extracted.
     * @return The columns where a value can be extracted.
     */
    Range<Integer> getColumns();

    /**
     * Extracts a value from pdb line.
     * @param line The line to extract from.
     * @return The trimmed value.
     */
    default String extract(String line) {
        return extractValueFromPDBLine(line, this);
    }

    /**
     * Extracts a value from a pdb line, given a token and line to extract from.
     * @param line The line to extract from.
     * @param token The value for the token to extract.
     * @return The trimmed value.
     */
    static String extractValueFromPDBLine(String line, PDBToken token) {
        // pdb numbering starts at column 1 - string starts at 0 - therefore -1
        // pdb numbering is including the last letter  - substring is excluding the last letter this account for the
        // offset
        if (line.length() >= token.getColumns().getUpperBound()) {
            return line.substring(
                    token.getColumns().getLowerBound() - 1, token.getColumns().getUpperBound()).trim();
        } else {
            return "";
        }
    }

}
