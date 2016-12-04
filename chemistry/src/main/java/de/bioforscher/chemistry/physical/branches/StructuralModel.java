package de.bioforscher.chemistry.physical.branches;

import java.util.List;
import java.util.stream.Collectors;

/**
 * To handle structures with multiple models (from NMR).
 */
public class StructuralModel extends BranchSubstructure<StructuralModel> {

    /**
     * Creates a new BranchSubstructure. The identifier is considered in the superordinate BranchSubstructure.
     *
     * @param identifier The identifier of this BranchSubstructure.
     */
    public StructuralModel(int identifier) {
        super(identifier);
    }

    public StructuralModel(StructuralModel structuralModel) {
        super(structuralModel);
    }

    public List<Chain> getAllChains() {
        return this.getSubstructures().stream()
                .map(Chain.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public StructuralModel getCopy() {
        return new StructuralModel(this);
    }

}
