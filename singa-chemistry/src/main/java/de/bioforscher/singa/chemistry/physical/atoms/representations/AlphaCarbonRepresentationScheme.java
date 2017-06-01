package de.bioforscher.singa.chemistry.physical.atoms.representations;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.AtomName;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;

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
        // immediately return atom if part of structure
        if (leafSubstructure.containsAtomWithName(AtomName.CA)) {
            return leafSubstructure.getAtomByName(AtomName.CA);
        }
        if (!(leafSubstructure instanceof AminoAcid)) {
            logger.warn("fallback for {} because it is no amino acid", leafSubstructure);
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
