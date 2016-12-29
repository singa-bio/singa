package de.bioforscher.chemistry.physical.atoms.representations;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Residue;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its alpha carbon. This is only available for
 * {@link Residue}s.
 *
 * @author fk
 */
public class AlphaCarbonRepresentationScheme extends AbstractRepresentationScheme {
    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?, ?> leafSubstructure) {
        if(!(leafSubstructure instanceof Residue)){
            logger.warn("fallback for ", leafSubstructure);
            return determineCentroid(leafSubstructure);
        }
       return leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isAlphaCarbon())
                .findAny()
                .orElse(determineCentroid(leafSubstructure));
    }
}
