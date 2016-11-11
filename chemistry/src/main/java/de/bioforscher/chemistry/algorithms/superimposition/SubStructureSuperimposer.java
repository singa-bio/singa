package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.physical.model.SubStructure;
import de.bioforscher.chemistry.physical.proteins.Residue;
import de.bioforscher.mathematics.vectors.VectorUtilities;

import java.util.stream.Collectors;

/**
 * Created by fkaiser on 10.11.16.
 */
public class SubStructureSuperimposer {

    private SubStructure reference;
    private SubStructure candidate;

    public SubStructureSuperimposer(SubStructure reference, SubStructure candidate) {
        if(reference.getAllAtoms().isEmpty())  {

        }
        reference.getSubstructures().size();
        candidate.getSubstructures().size();
        this.candidate = candidate;

        initialize();
        center();
        calculateRotation();
        calculateTranslation();
        applyMapping();
        calculateRMSD();
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
