package de.bioforscher.chemistry.parser.pdb.tokens;

import de.bioforscher.core.utility.Range;

import java.util.regex.Pattern;

/**
 * Tokens are used to extract information from PDB lines.
 */
public interface PDBToken {

    Pattern getRecordNamePattern();

    Range<Integer> getColumns();

    default String extract(String line) {
        return extractValueFromPDBLine(line, this);
    }

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
