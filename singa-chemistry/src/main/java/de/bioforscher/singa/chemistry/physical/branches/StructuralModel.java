package de.bioforscher.singa.chemistry.physical.branches;

import de.bioforscher.singa.mathematics.vectors.Vector3D;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.BranchFilter.isChain;

/**
 * To handle structures with multiple models (of NMR).
 */
public class StructuralModel extends BranchSubstructure<StructuralModel, Integer> {

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

    public Chain getFirstChain() {
        return getBranchSubstructures().stream()
                .filter(isChain())
                .map(Chain.class::cast)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("The model " + this + " does not contain a chain."));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StructuralModel that = (StructuralModel) o;

        return this.identifier != null ? this.identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return this.identifier != null ? this.identifier.hashCode() : 0;
    }

    @Override
    public void setPosition(Vector3D position) {
        //FIXME not yet implemented
        throw new UnsupportedOperationException();
    }

}
