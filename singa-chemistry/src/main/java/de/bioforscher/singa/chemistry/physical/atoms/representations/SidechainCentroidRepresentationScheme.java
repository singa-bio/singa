package de.bioforscher.singa.chemistry.physical.atoms.representations;

import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.UncertainAtom;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.singa.mathematics.vectors.Vector;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.mathematics.vectors.Vectors;

import java.util.List;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its sidechain centroid. This is only available for
 * {@link AminoAcid}s. For glycine this defaults to {@link BetaCarbonRepresentationScheme}.
 *
 * @author fk
 */
public class SidechainCentroidRepresentationScheme extends AbstractRepresentationScheme {
    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?, ?> leafSubstructure) {
        if (!(leafSubstructure instanceof AminoAcid)) {
            return determineCentroid(leafSubstructure);
        }
        if (leafSubstructure.getFamily() == AminoAcidFamily.GLYCINE) {
            return new BetaCarbonRepresentationScheme().determineCentroid(leafSubstructure);
        }
        // fallback if no sidechain atoms exist
        if (leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isSidechain())
                .count() == 0) {
            return determineCentroid(leafSubstructure);
        }
        List<Vector> atomPositions = leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isSidechain().and(AtomFilter.isHydrogen()
                        .negate()))
                .map(Atom::getPosition)
                .collect(Collectors.toList());
        return new UncertainAtom(leafSubstructure.getAllAtoms().get(0).getIdentifier(),
                ElementProvider.UNKOWN,
                RepresentationSchemeType.SIDECHAIN_CENTROID.getAtomNameString(),
                Vectors.getCentroid(atomPositions).as(Vector3D.class));
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.SIDECHAIN_CENTROID;
    }
}
