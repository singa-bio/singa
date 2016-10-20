package de.bioforscher.mathematics.algorithms.superimposition;

import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.matrices.MatrixUtilities;
import de.bioforscher.mathematics.matrices.SquareMatrix;
import de.bioforscher.mathematics.vectors.Vector;
import de.bioforscher.mathematics.vectors.VectorUtilities;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fkaiser on 19.10.16.
 */
public class SVDSuperimposer {

    private final List<Vector> reference;
    private final List<Vector> candidate;

    private List<Vector> shiftedReference;
    private List<Vector> shiftedCandidate;

    public SVDSuperimposer(List<Vector> reference, List<Vector> candidate) {
        this.reference = reference;
        this.candidate = candidate;
    }

    public Superimposition calculateSuperimposition() {

        center();
        calculateRotation();
//        calculateTranslation();
//        applyMapping();
//        calculateRMSD();

        return null;
    }

    private void calculateRotation() {

        Matrix referenceMatrix = MatrixUtilities.assembleMatrixFromRows(this.shiftedReference);
        Matrix candidateMatrix = MatrixUtilities.assembleMatrixFromRows(this.shiftedCandidate);

        // calculate covariance
        Matrix covarianceMatrix = MatrixUtilities.calculateCovarianceMatrix(referenceMatrix, candidateMatrix);

        // solve using SVD
        SVDecomposition svd = new SVDecomposition(covarianceMatrix);
        Matrix u = svd.getU();
        Matrix v = svd.getV();
        Matrix ut = svd.getUT();

        // calculate actual rotation matrix
        Matrix rotation = v.multiply(ut).transpose();

        // check for possible reflection
        if (((SquareMatrix) rotation).determinant() < 0) {

        }
    }

    private void center() {

        Vector referenceCentroid = VectorUtilities.getCentroid(this.reference);
        this.shiftedReference = this.reference.stream().map(vector -> vector.subtract(referenceCentroid))
                .collect(Collectors.toList());
        Vector candidateCentroid = VectorUtilities.getCentroid(this.reference);
        this.shiftedCandidate = this.candidate.stream().map(vector -> vector.subtract(candidateCentroid))
                .collect(Collectors.toList());


    }

    public Superimposition calculateIdealSuperimposition() {

        return null;

    }
}
