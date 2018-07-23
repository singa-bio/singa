package bio.singa.structure.algorithms.superimposition.scores;

import bio.singa.core.utility.Resources;
import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.mathematics.matrices.Matrices;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.RegularMatrix;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.families.StructuralFamily;
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

    BLOSUM_45("bio/singa/structure/algorithms/superimposition/scores/blosum45.csv"),
    MC_LACHLAN("bio/singa/structure/algorithms/superimposition/scores/mclachlan.csv");

    private LabeledSymmetricMatrix<StructuralFamily> matrix;

    SubstitutionMatrix(String resourceLocation) {

        Logger logger = LoggerFactory.getLogger(SubstitutionMatrix.class);

        // get resource
        InputStream stream = Resources.getResourceAsStream(resourceLocation);
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
        matrix = new LabeledSymmetricMatrix<>(stringLabeledMatrix.getCompleteElements());
        matrix.setRowLabels(structuralFamilyLabels);
    }

    public LabeledSymmetricMatrix<StructuralFamily> toCostMatrix() {
        switch (this) {
            case BLOSUM_45:
                // additively invert matrix
                Matrix invertedMatrix = BLOSUM_45.getMatrix().additivelyInvert();
                // find minimal element
                double minimalElement = Matrices.getPositionOfMinimalElement(invertedMatrix)
                        .map(position -> invertedMatrix.getElements()[position.getFirst()][position.getSecond()])
                        .orElse(Double.NaN);
                // add minimal element to each value
                double[][] matrixWithMinimalElement = new double[invertedMatrix.getRowDimension()][invertedMatrix.getColumnDimension()];
                for (int i = 0; i < matrixWithMinimalElement.length; i++) {
                    for (int j = 0; j < matrixWithMinimalElement[i].length; j++) {
                        matrixWithMinimalElement[i][j] = minimalElement;
                    }
                }
                RegularMatrix summand = new RegularMatrix(matrixWithMinimalElement);
                LabeledSymmetricMatrix<StructuralFamily> costMatrix = new LabeledSymmetricMatrix<>(
                        invertedMatrix.subtract(summand).getElements());
                costMatrix.setRowLabels(BLOSUM_45.getMatrix().getRowLabels());
                costMatrix.setColumnLabels(BLOSUM_45.getMatrix().getColumnLabels());
                return costMatrix;
            case MC_LACHLAN:
                // TODO figure out correct transformation for matrix
                return getMatrix();
            default:
                return getMatrix();
        }
    }

    public LabeledSymmetricMatrix<StructuralFamily> getMatrix() {
        return matrix;
    }
}
