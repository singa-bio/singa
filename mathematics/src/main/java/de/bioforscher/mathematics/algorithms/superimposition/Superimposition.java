package de.bioforscher.mathematics.algorithms.superimposition;

import de.bioforscher.mathematics.matrices.Matrix;
import de.bioforscher.mathematics.vectors.Vector;

import java.util.List;

/**
 * Representing a superimposition that is defined by a root-mean-squared deviation, a translation vector and a rotation
 * matrix. A superimposition can be applied to arbitrary candidates
 *
 * @author fk
 */
public interface Superimposition<T> {

    /**
     * returns the root-mean-squared deviation of this superimposition
     *
     * @return the root-mean-squared deviation
     */
    double getRmsd();

    /**
     * returns the translation {@link Vector} of this superimposition
     *
     * @return the translation vector
     */
    Vector getTranslation();

    /**
     * returns the rotation {@link Matrix} of this superimposition
     *
     * @return the rotation matrix
     */
    Matrix getRotation();

    /**
     * Returns copied mapped candidates that were used to compute this superimposition, only containing the objects
     * that were used to compute this superimposition.
     *
     * @return the candidates that were used for superimposition
     */
    List<T> getMappedCandidate();

    /**
     * Returns the full copied mapped candidates, which contain all objects of type T, regardless of the ones used to calculate this
     * superimpositon. The default method simply returns the mapped candidates as is.
     *
     * @return the candidates with their original objects that were used for superimposition
     */
    default List<T> getMappedFullCandidate(){
        return getMappedCandidate();
    }


    /**
     * applies this superimposition to a list of candidate
     *
     * @param candidate the candidate to which the superimposition should be applied
     * @return a new copy of the superimposed candidates
     */
    List<T> applyTo(List<T> candidate);
}
