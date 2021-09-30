package bio.singa.structure.algorithms.superimposition.fit3d.representations;

import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.oak.OakAtom;
import bio.singa.structure.model.oak.StructuralEntityFilter;
import bio.singa.structure.model.oak.Structures;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.GLYCINE;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its last heavy sidechain atom (the atom most
 * far from the alpha carbon). This is only available for {@link AminoAcid}s with defined alpha carbons. For
 * glycine this defaults to {@link BetaCarbonRepresentationScheme}.
 *
 * @author fk
 */
public class LastHeavySidechainRepresentationScheme extends AbstractRepresentationScheme {

    @Override
    public Atom determineRepresentingAtom(LeafSubstructure leafSubstructure) {
        // immediately return atom if part of structure
        final Optional<Atom> optionalLH = leafSubstructure.getAtomByName("LH");
        if (optionalLH.isPresent()) {
            return optionalLH.get();
        }
        if (!(leafSubstructure instanceof AminoAcid) || leafSubstructure.getAllAtoms().stream()
                .noneMatch(StructuralEntityFilter.AtomFilter.isAlphaCarbon())) {
            return determineCentroid(leafSubstructure);
        }
        if (leafSubstructure.getFamily() == GLYCINE) {
            return new BetaCarbonRepresentationScheme().determineCentroid(leafSubstructure);
        }
        // fallback if no sidechain atoms exist or no alpha carbon is present
        if (leafSubstructure.getAllAtoms().stream().noneMatch(StructuralEntityFilter.AtomFilter.isSideChain()) || leafSubstructure.getAllAtoms().stream().noneMatch(StructuralEntityFilter.AtomFilter.isAlphaCarbon())) {
            return determineCentroid(leafSubstructure);
        }
        // FIXME :this takes squared distances
        LabeledSymmetricMatrix<Atom> atomDistanceMatrix = Structures.calculateAtomDistanceMatrix(leafSubstructure.getAllAtoms().stream()
                .filter(StructuralEntityFilter.AtomFilter.isSideChain().and(StructuralEntityFilter.AtomFilter.isHydrogen()
                        .negate()).or(StructuralEntityFilter.AtomFilter.isAlphaCarbon()))
                .collect(Collectors.toList()));
        if (atomDistanceMatrix.getRowDimension() == 1) {
            return atomDistanceMatrix.getColumnLabel(0);
        }
        int maximalElementIndex = Vectors.getIndexWithMaximalElement(atomDistanceMatrix.getRowByLabel(leafSubstructure.getAtomByName("CA").orElseThrow(NoSuchElementException::new)));
        Atom referenceAtom = atomDistanceMatrix.getColumnLabel(maximalElementIndex);
        return new OakAtom(leafSubstructure.getAllAtoms().get(leafSubstructure.getAllAtoms().size() - 1).getAtomIdentifier(),
                ElementProvider.UNKOWN,
                RepresentationSchemeType.LAST_HEAVY_SIDE_CHAIN.getAtomNameString(),
                referenceAtom.getPosition());
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.LAST_HEAVY_SIDE_CHAIN;
    }
}
