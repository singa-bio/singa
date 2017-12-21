package de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations;


import de.bioforscher.singa.mathematics.vectors.Vectors3D;
import de.bioforscher.singa.structure.elements.ElementProvider;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.oak.OakAtom;
import de.bioforscher.singa.structure.model.oak.StructuralEntityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This abstract implementation provide a fallback solution to compute the centroid of a {@link LeafSubstructure}, iff
 * the specified representation fails.
 *
 * @author fk
 */
public abstract class AbstractRepresentationScheme implements RepresentationScheme {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractRepresentationScheme.class);

    /**
     * Determines the centroid (<b>EXCLUDING</b> hydrogen atoms) of the given {@link LeafSubstructure}.
     *
     * @param leafSubstructure The {@link LeafSubstructure} for which the centroid should be computed.
     * @return The centroid as an {@link Atom}.
     */
    Atom determineCentroid(LeafSubstructure<?> leafSubstructure) {
        final Optional<Atom> optionalCO = leafSubstructure.getAtomByName("CO");
        // immediately return atom if part of structure
        if (optionalCO.isPresent()) {
            return optionalCO.get();
        }
        logger.debug("obtaining centroid representation for {}", leafSubstructure);
        return new OakAtom(leafSubstructure.getAllAtoms().get(0).getAtomIdentifier(),
                ElementProvider.UNKOWN,
                RepresentationSchemeType.CENTROID.getAtomNameString(),
                Vectors3D.getCentroid(leafSubstructure.getAllAtoms().stream()
                        .filter(StructuralEntityFilter.AtomFilter.isHydrogen().negate())
                        .map(Atom::getPosition)
                        .collect(Collectors.toList())));
    }
}
