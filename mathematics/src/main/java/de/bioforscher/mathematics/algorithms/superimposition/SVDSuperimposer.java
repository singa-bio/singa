package de.bioforscher.mathematics.algorithms.superimposition;

import de.bioforscher.mathematics.algorithms.matrix.SVDecomposition;
import de.bioforscher.mathematics.combinatorics.StreamPermutations;
import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.matrices.MatrixUtilities;
import de.bioforscher.mathematics.matrices.SquareMatrix;
import de.bioforscher.mathematics.metrics.model.VectorMetricProvider;
import de.bioforscher.mathematics.vectors.Vector;
import de.bioforscher.mathematics.vectors.VectorUtilities;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by fkaiser on 19.10.16.
 */
public class SVDSuperimposer {

    private final List<Vector> reference;
    private final List<Vector> candidate;

    private List<Vector> shiftedReference;
    private List<Vector> shiftedCandidate;

    private Matrix rotation;
    private Vector referenceCentroid;
    private Vector candidateCentroid;
    private Vector translation;
    private List<Vector> mappedCandidate;
    private double rmsd;

    private SVDSuperimposer(List<Vector> reference, List<Vector> candidate) {
        this.reference = reference;
        this.candidate = candidate;
        if (this.reference.size() != this.candidate.size())
            throw new IllegalArgumentException("Two lists of vectors cannot be superimposed if they differ in size.");
    }

    public static Superimposition calculateSVDSuperimposition(List<Vector> reference, List<Vector> candidate) {
        return new SVDSuperimposer(reference, candidate).calculateSuperimposition();
    }

    public static Superimposition calculateIdealSVDSuperimposition(List<Vector> reference, List<Vector> candidate) {
        return new SVDSuperimposer(reference, candidate).calculateIdealSuperimposition();
    }

    public Superimposition calculateSuperimposition() {
        center();
        calculateRotation();
        calculateTranslation();
        applyMapping();
        calculateRMSD();
        return new Superimposition(this.rmsd, this.translation, this.rotation, this.mappedCandidate);
    }

    private void calculateRMSD() {
        this.rmsd = 0.0;
        int referenceSize = this.reference.size();
        for (int i = 0; i < referenceSize; i++) {
            Vector referenceEntity = this.reference.get(i);
            Vector candidateEntity = this.mappedCandidate.get(i);
            this.rmsd +=
                    VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC.calculateDistance(referenceEntity, candidateEntity);
        }
        this.rmsd = Math.sqrt(this.rmsd / referenceSize);
    }

    private void applyMapping() {
        this.mappedCandidate =
                this.candidate.stream()
                        .map(vector -> this.rotation.transpose().multiply(vector).add(this.translation))
                        .collect(Collectors.toList());
    }

    private void calculateTranslation() {
        // calculate translation vector t = ca - R' * cb
        this.translation = this.referenceCentroid.subtract(this.rotation.transpose().multiply(this.candidateCentroid));
    }

    private void calculateRotation() {
        Matrix referenceMatrix = MatrixUtilities.assembleMatrixFromRows(this.shiftedReference);
        Matrix candidateMatrix = MatrixUtilities.assembleMatrixFromRows(this.shiftedCandidate);

        // calculate covariance
        Matrix covarianceMatrix = MatrixUtilities.calculateCovarianceMatrix(referenceMatrix, candidateMatrix);

        // solve using SVD
        SVDecomposition svd = new SVDecomposition(covarianceMatrix);
        Matrix u = svd.getMatrixU();
        Matrix v = svd.getMatrixV();
        Matrix ut = u.transpose();

        // calculate actual rotation matrix
        this.rotation = v.multiply(ut).transpose();

        // check for possible reflection
        if (this.rotation.as(SquareMatrix.class).determinant() < 0) {

            // get copy of V matrix
            Matrix matrixV = svd.getMatrixV().getCopy().transpose();
            matrixV.getElements()[2][0] = 0 - matrixV.getElement(2, 0);
            matrixV.getElements()[2][1] = 0 - matrixV.getElement(2, 1);
            matrixV.getElements()[2][2] = 0 - matrixV.getElement(2, 2);

            this.rotation = matrixV.transpose().multiply(ut).transpose();
        }
    }

    private void center() {
        this.referenceCentroid = VectorUtilities.getCentroid(this.reference);
        this.shiftedReference = this.reference.stream().map(vector -> vector.subtract(this.referenceCentroid))
                .collect(Collectors.toList());
        this.candidateCentroid = VectorUtilities.getCentroid(this.candidate);
        this.shiftedCandidate = this.candidate.stream().map(vector -> vector.subtract(this.candidateCentroid))
                .collect(Collectors.toList());
    }

    /**
     * finds the ideal superimposition (LRMSD = min(RMSD)) for a list of candidate vectors
     *
     * @return the ideal superimposition
     */
    public Superimposition calculateIdealSuperimposition() {
        Optional<Superimposition> optionalSuperimposition = StreamPermutations.of(
                this.candidate.toArray(new Vector[this.candidate.size()]))
                .parallel()
                .map(s -> s.collect(Collectors.toList()))
                .map(permutedCandidates -> new SVDSuperimposer(this.reference, permutedCandidates).calculateSuperimposition())
                .reduce((Superimposition s1, Superimposition s2) -> s1.getRmsd() < s2.getRmsd() ? s1 : s2);
        return optionalSuperimposition.orElse(null);
    }
}
