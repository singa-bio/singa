package de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations;

import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;

/**
 * An implementation to represent a given {@link GraphLeafSubstructure} by its centroid <b>EXCLUDING</b> hydrogen.
 * This is the same as {@link AbstractRepresentationScheme#determineCentroid(GraphLeafSubstructure)}.
 *
 * @author fk
 */
public class CentroidRepresentationScheme extends AbstractRepresentationScheme {

    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?> leafSubstructure) {
        return determineCentroid(leafSubstructure);
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.CENTROID;
    }
}
