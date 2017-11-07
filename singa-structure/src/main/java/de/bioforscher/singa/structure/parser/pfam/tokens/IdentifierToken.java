package de.bioforscher.singa.structure.parser.pfam.tokens;

/**
 * @author fk
 */
public enum IdentifierToken implements PfamToken {

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
