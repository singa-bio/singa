package de.bioforscher.singa.chemistry.physical.atoms.representations;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its centroid <b>EXCLUDING</b> hydrogen.
 * This is the same as {@link AbstractRepresentationScheme#determineCentroid(LeafSubstructure)}.
 *
 * @author fk
 */
public class CentroidRepresentationScheme extends AbstractRepresentationScheme {
    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?, ?> leafSubstructure) {
        return determineCentroid(leafSubstructure);
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.CENTROID;
    }
}
