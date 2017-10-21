package de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations;

import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.oak.StructuralEntityFilter;

import java.util.Optional;

/**
 * An implementation to represent a given {@link GraphLeafSubstructure} by its alpha carbon. This is only available for
 * {@link GraphAminoAcid}s.
 *
 * @author fk
 */
public class AlphaCarbonRepresentationScheme extends AbstractRepresentationScheme {

    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?> leafSubstructure) {
        // immediately return atom if part of structure
        final Optional<Atom> optionalCA = leafSubstructure.getAtomByName("CA");
        if (optionalCA.isPresent()) {
            return optionalCA.get();
        }
        if (!(leafSubstructure instanceof AminoAcid)) {
            logger.warn("fallback for {} because it is no amino acid", leafSubstructure);
            return determineCentroid(leafSubstructure);
        }
        // TODO maybe we need copy here
        return leafSubstructure.getAllAtoms().stream()
                .filter(StructuralEntityFilter.AtomFilter.isAlphaCarbon())
                .findAny()
                .orElseGet(() -> determineCentroid(leafSubstructure));
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.CA;
    }
}
