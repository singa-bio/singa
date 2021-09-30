package bio.singa.structure.algorithms.superimposition.fit3d.representations;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.mathematics.vectors.Vectors3D;
import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.oak.OakAtom;
import bio.singa.structure.model.oak.StructuralEntityFilter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.GLYCINE;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its sidechain centroid. This is only available for
 * {@link AminoAcid}s. For glycine this defaults to {@link BetaCarbonRepresentationScheme}.
 *
 * @author fk
 */
public class SideChainCentroidRepresentationScheme extends AbstractRepresentationScheme {

    @Override
    public Atom determineRepresentingAtom(LeafSubstructure leafSubstructure) {
        // immediately return atom if part of structure
        final Optional<Atom> optionalSC = leafSubstructure.getAtomByName("SC");
        if (optionalSC.isPresent()) {
            return optionalSC.get();
        }
        if (!(leafSubstructure instanceof AminoAcid)) {
            return determineCentroid(leafSubstructure);
        }
        if (leafSubstructure.getFamily() == GLYCINE) {
            return new BetaCarbonRepresentationScheme().determineCentroid(leafSubstructure);
        }
        // fallback if no sidechain atoms exist
        if (leafSubstructure.getAllAtoms().stream().noneMatch(StructuralEntityFilter.AtomFilter.isSideChain())) {
            return determineCentroid(leafSubstructure);
        }
        List<Vector3D> atomPositions = leafSubstructure.getAllAtoms().stream()
                .filter(StructuralEntityFilter.AtomFilter.isSideChain().and(StructuralEntityFilter.AtomFilter.isHydrogen()
                        .negate()))
                .map(Atom::getPosition)
                .collect(Collectors.toList());
        return new OakAtom(leafSubstructure.getAllAtoms().get(0).getAtomIdentifier(),
                ElementProvider.UNKOWN,
                RepresentationSchemeType.SIDE_CHAIN_CENTROID.getAtomNameString(),
                Vectors3D.get3DCentroid(atomPositions));
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.SIDE_CHAIN_CENTROID;
    }
}
