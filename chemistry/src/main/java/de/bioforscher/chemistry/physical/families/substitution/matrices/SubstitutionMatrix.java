package de.bioforscher.chemistry.physical.families.substitution.matrices;

import de.bioforscher.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.chemistry.physical.model.StructuralFamily;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.mathematics.matrices.Matrices;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
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
            URL resource = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(resourceLocation);
            if (resource != null) {
                LabeledSymmetricMatrix<String> stringLabeledMatrix = (LabeledSymmetricMatrix<String>) Matrices
                        .readLabeledMatrixFromCSV(Paths.get(resource.toURI()));
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
        } catch (URISyntaxException | IOException ignored) {

        }
    }

    public LabeledSymmetricMatrix<StructuralFamily> getMatrix() {
        return this.matrix;
    }
}
