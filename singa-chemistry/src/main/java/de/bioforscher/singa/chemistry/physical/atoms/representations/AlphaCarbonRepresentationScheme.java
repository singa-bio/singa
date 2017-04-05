package de.bioforscher.singa.chemistry.physical.atoms.representations;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leafes.LeafSubstructure;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its alpha carbon. This is only available for
 * {@link AminoAcid}s.
 *
 * @author fk
 */
public class AlphaCarbonRepresentationScheme extends AbstractRepresentationScheme {
    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?, ?> leafSubstructure) {
        if(!(leafSubstructure instanceof AminoAcid)){
            logger.warn("fallback for ", leafSubstructure);
            return determineCentroid(leafSubstructure);
        }
       return leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isAlphaCarbon())
                .findAny()
                .orElseGet(() -> determineCentroid(leafSubstructure));
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.CA;
    }
}
