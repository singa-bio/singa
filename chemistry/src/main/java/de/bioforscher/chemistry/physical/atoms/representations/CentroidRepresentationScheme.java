package de.bioforscher.chemistry.physical.atoms.representations;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its centroid <b>EXCLUDING</b> hydrogens.
 * This is the same as {@link AbstractRepresentationScheme#determineCentroid(LeafSubstructure)}.
 *
 * @author fk
 */
public class CentroidRepresentationScheme extends AbstractRepresentationScheme {
    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?, ?> leafSubstructure) {
        return determineCentroid(leafSubstructure);
    }
}
