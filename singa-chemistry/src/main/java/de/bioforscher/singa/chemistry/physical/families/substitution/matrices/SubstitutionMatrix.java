package de.bioforscher.singa.chemistry.physical.families.substitution.matrices;

import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.model.StructuralFamily;
import de.bioforscher.singa.core.utility.TestUtils;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.matrices.Matrices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fk
 */
public enum SubstitutionMatrix {

    BLOSUM_45("de/bioforscher/singa/chemistry/physical/families/substitution/matrices/blosum45.csv"),
    MC_LACHLAN("de/bioforscher/singa/chemistry/physical/families/substitution/matrices/mclachlan.csv");

    private LabeledSymmetricMatrix<StructuralFamily> matrix;

    SubstitutionMatrix(String resourceLocation) {

        Logger logger = LoggerFactory.getLogger(SubstitutionMatrix.class);

        // get resource
        InputStream stream = TestUtils.getResourceAsStream(resourceLocation);
        LabeledSymmetricMatrix<String> stringLabeledMatrix = null;
        // parse matrix
        try {
            stringLabeledMatrix = (LabeledSymmetricMatrix<String>) Matrices
                    .readLabeledMatrixFromCSV(stream);
        } catch (IOException e) {
            logger.error("failed to read subsitution matrices from resources", e);
        }
        // replace string labels with amino acid families
        List<StructuralFamily> structuralFamilyLabels = new ArrayList<>();
        assert stringLabeledMatrix != null;
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

    public LabeledSymmetricMatrix<StructuralFamily> getMatrix() {
        return this.matrix;
    }
}
