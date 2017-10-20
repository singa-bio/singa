package de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations;

import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.mathematics.vectors.Vectors3D;
import de.bioforscher.singa.structure.model.graph.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.graph.model.StructuralEntityFilter;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An implementation to represent a given {@link GraphLeafSubstructure} by its sidechain centroid. This is only available for
 * {@link GraphAminoAcid}s. For glycine this defaults to {@link BetaCarbonRepresentationScheme}.
 *
 * @author fk
 */
public class SideChainCentroidRepresentationScheme extends AbstractRepresentationScheme {

    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?> leafSubstructure) {
        // immediately return atom if part of structure
        final Optional<Atom> optionalSC = leafSubstructure.getAtomByName("SC");
        if (optionalSC.isPresent()) {
            return optionalSC.get();
        }
        if (!(leafSubstructure instanceof GraphAminoAcid)) {
            return determineCentroid(leafSubstructure);
        }
        if (leafSubstructure.getFamily() == AminoAcidFamily.GLYCINE) {
            return new BetaCarbonRepresentationScheme().determineCentroid(leafSubstructure);
        }
        // fallback if no sidechain atoms exist
        if (leafSubstructure.getAllAtoms().stream()
                .filter(StructuralEntityFilter.AtomFilter.isSideChain())
                .count() == 0) {
            return determineCentroid(leafSubstructure);
        }
        List<Vector3D> atomPositions = leafSubstructure.getAllAtoms().stream()
                .filter(StructuralEntityFilter.AtomFilter.isSideChain().and(StructuralEntityFilter.AtomFilter.isHydrogen()
                        .negate()))
                .map(Atom::getPosition)
                .collect(Collectors.toList());
        return new UncertainAtom(leafSubstructure.getAllAtoms().get(0).getIdentifier(),
                ElementProvider.UNKOWN,
                RepresentationSchemeType.SIDE_CHAIN_CENTROID.getAtomNameString(),
                Vectors3D.getCentroid(atomPositions));
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.SIDE_CHAIN_CENTROID;
    }
}
