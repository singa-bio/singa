package bio.singa.structure.parser.pfam.tokens;

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

    enum PDBToken implements PfamToken {

        PDB_IDENTIFIER(2),
        CHAIN_IDENTIFIER(5),
        PDB_RESIDUE_START(10),
        PDB_RESIDUE_END(11);

        private final int column;

        PDBToken(int column) {
            this.column = column;
        }

        @Override
        public int getColumn() {
            return column;

        }
    }

    enum IdentifierToken implements PfamToken {

        PFAM_IDENTIFIER(3),
        UNIPROT_IDENTIFIER(4);

        private final int column;

        IdentifierToken(int column) {
            this.column = column;
        }

        @Override
        public int getColumn() {
            return column;
        }
    }
}
