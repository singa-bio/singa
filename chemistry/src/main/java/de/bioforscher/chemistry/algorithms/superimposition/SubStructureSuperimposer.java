package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.physical.model.SubStructure;

/**
 * Created by fkaiser on 10.11.16.
 */
public class SubStructureSuperimposer {

    private SubStructure reference;
    private SubStructure candidate;

    public SubStructureSuperimposer(SubStructure reference, SubStructure candidate) {
        this.reference = reference;
        this.candidate = candidate;

        center();
        calculateRotation();
        calculateTranslation();
        applyMapping();
        calculateRMSD();
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
