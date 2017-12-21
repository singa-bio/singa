package de.bioforscher.singa.structure.parser.pfam.tokens;

/**
 * @author fk
 */
public enum PdbToken implements PfamToken {

    PDB_IDENTIFIER(2),
    CHAIN_IDENTIFIER(5),
    PDB_RESIDUE_START(10),
    PDB_RESIDUE_END(11);

    private final int column;

    PdbToken(int column) {
        this.column = column;
    }

    @Override
    public int getColumn() {
        return column;
    }
}
