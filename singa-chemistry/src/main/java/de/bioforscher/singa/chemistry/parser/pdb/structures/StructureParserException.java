package de.bioforscher.singa.chemistry.parser.pdb.structures;

/**
 * Exceptions encountered during parsing of molecular structures.
 *
 * @author fk
 */
public class StructureParserException extends RuntimeException {

    private static final long serialVersionUID = -5031672427116227877L;

    /**
     * Creates a new structure parser exception.
     * @param message The message to display.
     */
    public StructureParserException(String message) {
        super(message);
    }

}
