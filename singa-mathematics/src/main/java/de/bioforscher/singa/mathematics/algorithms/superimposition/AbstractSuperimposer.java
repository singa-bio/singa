package de.bioforscher.singa.mathematics.algorithms.superimposition;

import de.bioforscher.singa.mathematics.matrices.Matrix;
import de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider;
import de.bioforscher.singa.mathematics.vectors.Vector;

import java.util.List;

/**
 * @author fk
 */
abstract class AbstractSuperimposer<VectorType extends Vector> {

    protected final List<VectorType> reference;
    protected final List<VectorType> candidate;
    protected Vector translation;
    protected double rmsd;
    VectorType referenceCentroid;
    VectorType candidateCentroid;
    List<VectorType> shiftedReference;
    List<VectorType> shiftedCandidate;
    List<VectorType> mappedCandidate;
    Matrix rotation;

    AbstractSuperimposer(List<VectorType> reference, List<VectorType> candidate) {
        this.reference = reference;
        this.candidate = candidate;
        if (this.reference.size() != this.candidate.size())
            throw new IllegalArgumentException("Two lists of vectors cannot be superimposed if they differ in size.");
    }

    void calculateRMSD() {
        rmsd = 0.0;
        int referenceSize = reference.size();
        for (int i = 0; i < referenceSize; i++) {
            Vector referenceEntity = reference.get(i);
            Vector candidateEntity = mappedCandidate.get(i);
            rmsd += VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC
                    .calculateDistance(referenceEntity, candidateEntity);
        }
        rmsd = Math.sqrt(rmsd / referenceSize);
    }

    abstract protected void center();

    protected abstract VectorSuperimposition calculateSuperimposition();

    protected abstract void applyMapping();
}