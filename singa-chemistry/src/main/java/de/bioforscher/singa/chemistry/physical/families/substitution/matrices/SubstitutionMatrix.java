package de.bioforscher.singa.chemistry.physical.families.substitution.matrices;

import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.model.StructuralFamily;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.matrices.Matrices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fk
 */
public enum SubstitutionMatrix {

    BLOSUM_45("physical/families/substitution/matrices/blosum45.csv"),
    MC_LACHLAN("physical/families/substitution/matrices/mclachlan.csv");

    private LabeledSymmetricMatrix<StructuralFamily> matrix;

    SubstitutionMatrix(String resourceLocation) {
        try {
            InputStream resource = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(resourceLocation);
            if (resource != null) {
                try (BufferedReader buffer = new BufferedReader(new InputStreamReader(resource))) {
                    LabeledSymmetricMatrix<String> stringLabeledMatrix = (LabeledSymmetricMatrix<String>) Matrices
                            .readLabeledMatrixFromCSV(buffer.lines());
                    List<StructuralFamily> structuralFamilyLabels = new ArrayList<>();
                    for (int i = 0; i < stringLabeledMatrix.getRowDimension(); i++) {
                        String matrixLabel = stringLabeledMatrix.getRowLabel(i);
                        if (Arrays.stream(AminoAcidFamily.values())
                                .map(AminoAcidFamily::name)
                                .anyMatch(name -> name.equals(matrixLabel))) {
                            structuralFamilyLabels.add(AminoAcidFamily.valueOf(stringLabeledMatrix.getRowLabel(i)));
                        }
                    }
                    this.matrix = new LabeledSymmetricMatrix<>(stringLabeledMatrix.getCompleteElements());
                    this.matrix.setRowLabels(structuralFamilyLabels);
                }
            }
        } catch (IOException ignored) {

        }
    }

    public LabeledSymmetricMatrix<StructuralFamily> getMatrix() {
        return this.matrix;
    }
}
