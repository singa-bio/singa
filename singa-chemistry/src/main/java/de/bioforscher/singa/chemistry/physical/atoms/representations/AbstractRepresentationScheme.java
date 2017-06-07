package de.bioforscher.singa.chemistry.physical.atoms.representations;


import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.AtomName;
import de.bioforscher.singa.chemistry.physical.atoms.RegularAtom;
import de.bioforscher.singa.chemistry.physical.atoms.UncertainAtom;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter;

/**
 * This abstract implementation provide a fallback solution to compute the centroid of a {@link LeafSubstructure},
 * iff the specified representation fails.
 *
 * @author fk
 */
public abstract class AbstractRepresentationScheme implements RepresentationScheme {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractRepresentationScheme.class);

    /**
     * Determines the centroid (<b>EXCLUDING</b> hydrogen atoms) of the given {@link LeafSubstructure}.
     *
     * @param leafSubstructure The {@link LeafSubstructure} for which the centroid should be computed.
     * @return The centroid as an {@link UncertainAtom}.
     */
    Atom determineCentroid(LeafSubstructure<?, ?> leafSubstructure) {
        // immediately return atom if part of structure
        if (leafSubstructure.containsAtomWithName(AtomName.CO)) {
            return leafSubstructure.getAtomByName(AtomName.CO);
        }
        logger.debug("obtaining centroid representation for {}", leafSubstructure);
        return new UncertainAtom(leafSubstructure.getAllAtoms().get(0).getIdentifier(),
                ElementProvider.UNKOWN,
                RepresentationSchemeType.CENTROID.getAtomNameString(),
                Vectors.getCentroid(leafSubstructure.getAllAtoms().stream()
                        .filter(AtomFilter.isHydrogen().negate())
                        .map(Atom::getPosition)
                        .collect(Collectors.toList()))
                        .as(Vector3D.class));
    }
}
