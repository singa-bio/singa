package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationScheme;

/**
 * A class to represent settings of the {@link Fit3DAlignment}.
 * <p>
 * TODO implement using a step builder pattern and utilize to parametrize a {@link Fit3DAlignment}
 *
 * @author fk
 */
public class Fit3DAlignmentParameters {

    double rmsdCutoff;
    double distanceTolerance;
    AtomFilter atomFilter;
    RepresentationScheme representationScheme;

    private Fit3DAlignmentParameters() {

    }
}
