package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.physical.model.SubStructure;

import java.util.List;

/**
 * Created by fkaiser on 10.11.16.
 */
public class SubStructureSuperimposer {

    private List<SubStructure> reference;
    private List<SubStructure> candidate;

    public SubStructureSuperimposer(SubStructure reference, SubStructure candidate) {
        this.reference = reference.getAtomContainingSubstructures();
        this.candidate = candidate.getAtomContainingSubstructures();

        if(this.reference.size() != this.candidate.size())
            throw new IllegalArgumentException("Two lists of substructures cannot be superimposed if they " +
                    "differ in size.");
    }

    private void initialize() {
    }

    private void calculateRMSD() {

    }

    private void applyMapping() {

    }

    private void calculateTranslation() {
    }

    private void calculateRotation() {
    }

    private void center() {

    }
}
