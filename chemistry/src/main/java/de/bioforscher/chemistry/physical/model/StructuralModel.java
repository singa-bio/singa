package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.physical.proteins.Chain;

import java.util.List;
import java.util.stream.Collectors;

/**
 * To handle structures with multiple models (from NMR).
 * TODO not yet implemented
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

    public List<Chain> getAllChains() {
        return this.getSubstructures().stream()
                .map(Chain.class::cast)
                .collect(Collectors.toList());
    }


}
