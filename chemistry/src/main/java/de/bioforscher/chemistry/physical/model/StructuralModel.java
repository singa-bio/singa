package de.bioforscher.chemistry.physical.model;

/**
 * To handle structures with multiple models (from NMR).
 * TODO not yet implemented.
 */
public class StructuralModel extends SubStructure {

    /**
     * Creates a new SubStructure. The identifier is considered in the superordinate SubStructure.
     *
     * @param identifier The identifier of this SubStructure.
     */
    public StructuralModel(int identifier) {
        super(identifier);
    }

}
