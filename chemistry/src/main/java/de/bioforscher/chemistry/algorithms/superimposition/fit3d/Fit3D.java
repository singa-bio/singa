package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.algorithms.superimposition.SubstructureSuperimposition;

import java.util.TreeMap;

/**
 * Represents an instance of the Fit3D algorithm. This can either be a one target one alignment ({@link Fit3DAlignment})
 * or an alignment batch ({@link Fit3DAlignmentBatch}).
 *
 * @author fk
 */
public interface Fit3D {
    /**
     * Returns the matches that were found by this Fit3D search.
     *
     * @return The matches found in the target structure(s).
     */
    TreeMap<Double, SubstructureSuperimposition> getMatches();
}
