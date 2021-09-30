package bio.singa.structure.algorithms.superimposition.scores;

import bio.singa.core.utility.Resources;
import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.mathematics.matrices.Matrices;
import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.RegularMatrix;
import bio.singa.structure.model.families.StructuralFamilies;
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

    BLOSUM_30("bio/singa/structure/algorithms/superimposition/scores/blosum30.csv"),
    BLOSUM_35("bio/singa/structure/algorithms/superimposition/scores/blosum35.csv"),
    BLOSUM_40("bio/singa/structure/algorithms/superimposition/scores/blosum40.csv"),
    BLOSUM_45("bio/singa/structure/algorithms/superimposition/scores/blosum45.csv"),
    BLOSUM_50("bio/singa/structure/algorithms/superimposition/scores/blosum50.csv"),
    BLOSUM_55("bio/singa/structure/algorithms/superimposition/scores/blosum55.csv"),
    BLOSUM_60("bio/singa/structure/algorithms/superimposition/scores/blosum60.csv"),
    BLOSUM_62("bio/singa/structure/algorithms/superimposition/scores/blosum62.csv"),
    BLOSUM_65("bio/singa/structure/algorithms/superimposition/scores/blosum65.csv"),
    BLOSUM_70("bio/singa/structure/algorithms/superimposition/scores/blosum70.csv"),
    BLOSUM_75("bio/singa/structure/algorithms/superimposition/scores/blosum75.csv"),
    BLOSUM_80("bio/singa/structure/algorithms/superimposition/scores/blosum80.csv"),
    BLOSUM_85("bio/singa/structure/algorithms/superimposition/scores/blosum85.csv"),
    BLOSUM_90("bio/singa/structure/algorithms/superimposition/scores/blosum90.csv"),
    BLOSUM_100("bio/singa/structure/algorithms/superimposition/scores/blosum100.csv"),

    PAM_10("bio/singa/structure/algorithms/superimposition/scores/pam10.csv"),
    PAM_20("bio/singa/structure/algorithms/superimposition/scores/pam20.csv"),
    PAM_30("bio/singa/structure/algorithms/superimposition/scores/pam30.csv"),
    PAM_40("bio/singa/structure/algorithms/superimposition/scores/pam40.csv"),
    PAM_50("bio/singa/structure/algorithms/superimposition/scores/pam50.csv"),
    PAM_60("bio/singa/structure/algorithms/superimposition/scores/pam60.csv"),
    PAM_70("bio/singa/structure/algorithms/superimposition/scores/pam70.csv"),
    PAM_80("bio/singa/structure/algorithms/superimposition/scores/pam80.csv"),
    PAM_90("bio/singa/structure/algorithms/superimposition/scores/pam90.csv"),
    PAM_100("bio/singa/structure/algorithms/superimposition/scores/pam100.csv"),
    PAM_110("bio/singa/structure/algorithms/superimposition/scores/pam110.csv"),
    PAM_130("bio/singa/structure/algorithms/superimposition/scores/pam130.csv"),
    PAM_140("bio/singa/structure/algorithms/superimposition/scores/pam140.csv"),
    PAM_150("bio/singa/structure/algorithms/superimposition/scores/pam150.csv"),
    PAM_160("bio/singa/structure/algorithms/superimposition/scores/pam160.csv"),
    PAM_170("bio/singa/structure/algorithms/superimposition/scores/pam170.csv"),
    PAM_180("bio/singa/structure/algorithms/superimposition/scores/pam180.csv"),
    PAM_190("bio/singa/structure/algorithms/superimposition/scores/pam190.csv"),
    PAM_200("bio/singa/structure/algorithms/superimposition/scores/pam200.csv"),
    PAM_210("bio/singa/structure/algorithms/superimposition/scores/pam210.csv"),
    PAM_220("bio/singa/structure/algorithms/superimposition/scores/pam220.csv"),
    PAM_230("bio/singa/structure/algorithms/superimposition/scores/pam230.csv"),
    PAM_240("bio/singa/structure/algorithms/superimposition/scores/pam240.csv"),
    PAM_250("bio/singa/structure/algorithms/superimposition/scores/pam250.csv"),
    PAM_260("bio/singa/structure/algorithms/superimposition/scores/pam260.csv"),
    PAM_270("bio/singa/structure/algorithms/superimposition/scores/pam270.csv"),
    PAM_280("bio/singa/structure/algorithms/superimposition/scores/pam280.csv"),
    PAM_290("bio/singa/structure/algorithms/superimposition/scores/pam290.csv"),
    PAM_300("bio/singa/structure/algorithms/superimposition/scores/pam300.csv"),
    PAM_310("bio/singa/structure/algorithms/superimposition/scores/pam310.csv"),
    PAM_320("bio/singa/structure/algorithms/superimposition/scores/pam320.csv"),
    PAM_330("bio/singa/structure/algorithms/superimposition/scores/pam330.csv"),
    PAM_340("bio/singa/structure/algorithms/superimposition/scores/pam340.csv"),
    PAM_350("bio/singa/structure/algorithms/superimposition/scores/pam350.csv"),
    PAM_360("bio/singa/structure/algorithms/superimposition/scores/pam360.csv"),
    PAM_370("bio/singa/structure/algorithms/superimposition/scores/pam370.csv"),
    PAM_380("bio/singa/structure/algorithms/superimposition/scores/pam380.csv"),
    PAM_390("bio/singa/structure/algorithms/superimposition/scores/pam390.csv"),
    PAM_400("bio/singa/structure/algorithms/superimposition/scores/pam400.csv"),
    PAM_410("bio/singa/structure/algorithms/superimposition/scores/pam410.csv"),
    PAM_420("bio/singa/structure/algorithms/superimposition/scores/pam420.csv"),
    PAM_430("bio/singa/structure/algorithms/superimposition/scores/pam430.csv"),
    PAM_440("bio/singa/structure/algorithms/superimposition/scores/pam440.csv"),
    PAM_450("bio/singa/structure/algorithms/superimposition/scores/pam450.csv"),
    PAM_460("bio/singa/structure/algorithms/superimposition/scores/pam460.csv"),
    PAM_470("bio/singa/structure/algorithms/superimposition/scores/pam470.csv"),
    PAM_480("bio/singa/structure/algorithms/superimposition/scores/pam480.csv"),
    PAM_490("bio/singa/structure/algorithms/superimposition/scores/pam490.csv"),
    PAM_500("bio/singa/structure/algorithms/superimposition/scores/pam500.csv"),

    DAYHOFF("bio/singa/structure/algorithms/superimposition/scores/dayhoff.csv"),

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
            structuralFamilyLabels.add(StructuralFamilies.AminoAcids.getOrUnknown(stringLabeledMatrix.getRowLabel(i)));
        }
        matrix = new LabeledSymmetricMatrix<>(stringLabeledMatrix.getCompleteElements());
        matrix.setRowLabels(structuralFamilyLabels);
    }

    public LabeledSymmetricMatrix<StructuralFamily> toCostMatrix() {
        switch (this) {
            case BLOSUM_30:
            case BLOSUM_40:
            case BLOSUM_45:
            case BLOSUM_50:
            case BLOSUM_55:
            case BLOSUM_60:
            case BLOSUM_62:
            case BLOSUM_65:
            case BLOSUM_70:
            case BLOSUM_75:
            case BLOSUM_80:
            case BLOSUM_85:
            case BLOSUM_90:
            case BLOSUM_100:
                // additively invert matrix
                Matrix invertedMatrix = BLOSUM_45.getMatrix().additivelyInvert();
                // find minimal element
                double minimalElement = Matrices.getPositionOfMinimalElement(invertedMatrix)
                        .map(position -> invertedMatrix.getElements()[position.getFirst()][position.getSecond()])
                        .orElse(Double.NaN);
                // add minimal element to each value
                double[][] matrixWithMinimalElement = new double[invertedMatrix.getRowDimension()][invertedMatrix.getColumnDimension()];
                for (double[] doubles : matrixWithMinimalElement) {
                    Arrays.fill(doubles, minimalElement);
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
