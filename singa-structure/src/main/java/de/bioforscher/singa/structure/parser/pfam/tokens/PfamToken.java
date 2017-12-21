package de.bioforscher.singa.structure.parser.pfam.tokens;

/**
 * A token describing the Pfam mapping file.
 *
 * @author fk
 */
public interface PfamToken {

    String PFAM_MAPPING_FILE_SEPARATOR = "\t";

    static String extractValueFromPfamLine(String line, PfamToken token) {
        return line.split(PFAM_MAPPING_FILE_SEPARATOR)[token.getColumn()].trim();
    }

    int getColumn();

    default String extract(String line) {
        return extractValueFromPfamLine(line, this);
    }
}
