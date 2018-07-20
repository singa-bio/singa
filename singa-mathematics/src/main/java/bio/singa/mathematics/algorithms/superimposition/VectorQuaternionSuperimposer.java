package bio.singa.mathematics.algorithms.superimposition;

import bio.singa.mathematics.matrices.Matrix;
import bio.singa.mathematics.matrices.SquareMatrix;
import bio.singa.mathematics.quaternions.Quaternion;
import bio.singa.mathematics.quaternions.Quaternions;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.mathematics.vectors.Vectors3D;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class VectorQuaternionSuperimposer extends AbstractSuperimposer<Vector3D> {

    public VectorQuaternionSuperimposer(List<Vector3D> reference, List<Vector3D> candidate) {
        super(reference, candidate);
    }

    public static VectorSuperimposition<Vector3D> calculateVectorSuperimposition(List<Vector3D> reference, List<Vector3D> candidate) {
        return new VectorQuaternionSuperimposer(reference, candidate).calculateSuperimposition();
    }

    private static SquareMatrix toTranslationMatrix(Vector3D translation) {
        // generate translation matrix representation
        double m03 = translation.getX();
        double m13 = translation.getY();
        double m23 = translation.getZ();
        double[][] translationElements = new double[][]{{1.0, 0.0, 0.0, m03}, {0.0, 1.0, 0.0, m13},
                {0.0, 0.0, 1.0, m23}, {0.0, 0.0, 0.0, 1.0}};
        return new SquareMatrix(translationElements);
    }

    @Override
    protected void center() {
        referenceCentroid = Vectors3D.getCentroid(reference);
        shiftedReference = reference.stream()
                .map(vector -> vector.subtract(referenceCentroid))
                .collect(Collectors.toList());
        candidateCentroid = Vectors3D.getCentroid(candidate);
        shiftedCandidate = candidate.stream()
                .map(vector -> vector.subtract(candidateCentroid))
                .collect(Collectors.toList());
    }

    @Override
    protected VectorSuperimposition<Vector3D> calculateSuperimposition() {
        // center point clouds at origin
        center();
        // do magic quaternion stuff
        Quaternion quaternion = Quaternions.relativeOrientation(shiftedReference, shiftedCandidate);
        Vector3D ytrans = candidateCentroid.additivelyInvert();
        SquareMatrix quaternionMatrix = quaternion.toMatrixRepresentation();
        Matrix rottrans = toTranslationMatrix(referenceCentroid).multiply(quaternionMatrix.multiply(toTranslationMatrix(ytrans)));
        double[][] rotationElements = new double[][]{
                {rottrans.getElement(0, 0), rottrans.getElement(1, 0), rottrans.getElement(2, 0)},
                {rottrans.getElement(0, 1), rottrans.getElement(1, 1), rottrans.getElement(2, 1)},
                {rottrans.getElement(0, 2), rottrans.getElement(1, 2), rottrans.getElement(2, 2)}};
        rotation = new SquareMatrix(rotationElements);
        translation = new Vector3D(rottrans.getElement(0, 3),
                rottrans.getElement(1, 3), rottrans.getElement(2, 3));
        //apply mappings and calculate RMSD
        applyMapping();
        calculateRMSD();

        return new VectorSuperimposition<>(rmsd, translation, rotation, reference, candidate, mappedCandidate);
    }

    @Override
    protected void applyMapping() {
        mappedCandidate = candidate.stream()
                .map(vector -> rotation.transpose().multiply(vector).add(translation))
                .map(vector -> vector.as(Vector3D.class))
                .collect(Collectors.toList());
    }
}
