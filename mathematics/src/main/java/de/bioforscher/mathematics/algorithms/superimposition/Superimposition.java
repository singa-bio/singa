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
     * returns copied mapped candidates that were used to compute this superimposition
     *
     * @return the candidates that were used for superimposition
     */
    List<T> getMappedCandidate();

    /**
     * applies this superimposition to a list of candidate
     *
     * @param candidate the candidate to which the superimposition should be applied
     * @return a new copy of the superimposed candidates
     */
    List<T> applyTo(List<T> candidate);
}
