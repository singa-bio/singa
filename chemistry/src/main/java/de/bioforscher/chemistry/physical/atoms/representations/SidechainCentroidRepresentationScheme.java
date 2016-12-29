package de.bioforscher.chemistry.physical.atoms.representations;

import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.atoms.UncertainAtom;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.mathematics.vectors.Vector;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.Vectors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its sidechain centroid. This is only available for
 * {@link Residue}s. For glycine this defaults to {@link BetaCarbonRepresentationScheme}.
 *
 * @author fk
 */
public class SidechainCentroidRepresentationScheme extends AbstractRepresentationScheme {
    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?, ?> leafSubstructure) {
        if (!(leafSubstructure instanceof Residue)) {
            return determineCentroid(leafSubstructure);
        }
        if (leafSubstructure.getFamily() == ResidueFamily.GLYCINE) {
            return new BetaCarbonRepresentationScheme().determineCentroid(leafSubstructure);
        }
        // fallback if no sidechain atoms exist
        if (leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isSidechain())
                .count() == 0) {
            return determineCentroid(leafSubstructure);
        }
        List<Vector> atomPositions = leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isSidechain().and(AtomFilter.isHydrogen().negate()))
                .map(Atom::getPosition)
                .collect(Collectors.toList());
        return new UncertainAtom(leafSubstructure.getAllAtoms().get(0).getIdentifier(),
                ElementProvider.UNKOWN,
                RepresentationSchemeType.SIDECHAIN_CENTROID.getAtomNameString(),
                Vectors.getCentroid(atomPositions).as(Vector3D.class));
    }
}
