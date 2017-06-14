package de.bioforscher.singa.mathematics.algorithms.superimposition;

import de.bioforscher.singa.mathematics.algorithms.matrix.SVDecomposition;
import de.bioforscher.singa.mathematics.combinatorics.StreamPermutations;
import de.bioforscher.singa.mathematics.matrices.Matrices;
import de.bioforscher.singa.mathematics.matrices.Matrix;
import de.bioforscher.singa.mathematics.matrices.SquareMatrix;
import de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider;
import de.bioforscher.singa.mathematics.vectors.Vector;
import de.bioforscher.singa.mathematics.vectors.Vectors;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An implementation of the Kabsch algorithm that uses Singular Value Decomposition (SVD) to compute the ideal
 * superimposition of two point sets (a list of {@link Vector}s).
 *
 * @author fk
 * @see {https://en.wikipedia.org/wiki/Kabsch_algorithm}
 */
public class VectorSuperimposer {

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

    private VectorSuperimposer(List<Vector> reference, List<Vector> candidate) {
        this.reference = reference;
        this.candidate = candidate;
        if (this.reference.size() != this.candidate.size())
            throw new IllegalArgumentException("Two lists of vectors cannot be superimposed if they differ in size.");
    }

    public static VectorSuperimposition calculateVectorSuperimposition(List<Vector> reference, List<Vector> candidate) {
        return new VectorSuperimposer(reference, candidate).calculateSuperimposition();
    }

    public static VectorSuperimposition calculateIdealVectorSuperimposition(List<Vector> reference, List<Vector> candidate) {
        return new VectorSuperimposer(reference, candidate).calculateIdealSuperimposition();
    }

    private VectorSuperimposition calculateSuperimposition() {
        center();
        calculateRotation();
        calculateTranslation();
        applyMapping();
        calculateRMSD();
        return new VectorSuperimposition(this.rmsd, this.translation, this.rotation, this.reference, this.candidate,
                this.mappedCandidate);
    }

    private void calculateRMSD() {
        this.rmsd = 0.0;
        int referenceSize = this.reference.size();
        for (int i = 0; i < referenceSize; i++) {
            Vector referenceEntity = this.reference.get(i);
            Vector candidateEntity = this.mappedCandidate.get(i);
            this.rmsd += VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC
                    .calculateDistance(referenceEntity, candidateEntity);
        }
        this.rmsd = Math.sqrt(this.rmsd / referenceSize);
    }

    private void applyMapping() {
        this.mappedCandidate = this.candidate.stream()
                        .map(vector -> this.rotation.transpose().multiply(vector).add(this.translation))
                        .collect(Collectors.toList());
    }

    private void calculateTranslation() {
        // calculate translation vector t = ca - R' * cb
        this.translation = this.referenceCentroid.subtract(this.rotation.transpose().multiply(this.candidateCentroid));
    }

    private void calculateRotation() {
        Matrix referenceMatrix = Matrices.assembleMatrixFromRows(this.shiftedReference);
        Matrix candidateMatrix = Matrices.assembleMatrixFromRows(this.shiftedCandidate);

        // calculate covariance
        Matrix covarianceMatrix = Matrices.calculateCovarianceMatrix(referenceMatrix, candidateMatrix);

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
        this.referenceCentroid = Vectors.getCentroid(this.reference);
        this.shiftedReference = this.reference.stream()
                .map(vector -> vector.subtract(this.referenceCentroid))
                .collect(Collectors.toList());
        this.candidateCentroid = Vectors.getCentroid(this.candidate);
        this.shiftedCandidate = this.candidate.stream()
                .map(vector -> vector.subtract(this.candidateCentroid))
                .collect(Collectors.toList());
    }

    /**
     * Finds the ideal superimposition (LRMSD = min(RMSD)) for a list of candidate vectors.
     *
     * @return the ideal superimposition
     */
    private VectorSuperimposition calculateIdealSuperimposition() {
        Optional<VectorSuperimposition> optionalSuperimposition = StreamPermutations.of(
                this.candidate.toArray(new Vector[this.candidate.size()]))
                .parallel()
                .map(s -> s.collect(Collectors.toList()))
                .map(permutedCandidates -> new VectorSuperimposer(this.reference, permutedCandidates)
                        .calculateSuperimposition())
                .reduce((VectorSuperimposition s1, VectorSuperimposition s2) ->
                        s1.getRmsd() < s2.getRmsd() ? s1 : s2);
        return optionalSuperimposition.orElse(null);
    }
}
