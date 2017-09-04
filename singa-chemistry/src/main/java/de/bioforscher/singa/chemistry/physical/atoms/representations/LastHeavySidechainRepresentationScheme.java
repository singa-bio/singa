package de.bioforscher.singa.chemistry.physical.atoms.representations;

import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.AtomName;
import de.bioforscher.singa.chemistry.physical.atoms.RegularAtom;
import de.bioforscher.singa.chemistry.physical.atoms.UncertainAtom;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structures;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.vectors.Vectors;

import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its last heavy sidechain atom (the atom most far
 * from the alpha carbon). This is only available for {@link AminoAcid}s with defined alpha carbons. For glycine this
 * defaults to {@link BetaCarbonRepresentationScheme}.
 *
 * @author fk
 */
public class LastHeavySidechainRepresentationScheme extends AbstractRepresentationScheme {

    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?, ?> leafSubstructure) {
        // immediately return atom if part of structure
        if (leafSubstructure.containsAtomWithName(AtomName.LH)) {
            return leafSubstructure.getAtomByName(AtomName.LH);
        }
        if (!(leafSubstructure instanceof AminoAcid) || leafSubstructure.getAllAtoms().stream()
                .noneMatch(AtomFilter.isAlphaCarbon())) {
            return determineCentroid(leafSubstructure);
        }
        if (leafSubstructure.getFamily() == AminoAcidFamily.GLYCINE) {
            return new BetaCarbonRepresentationScheme().determineCentroid(leafSubstructure);
        }
        // fallback if no sidechain atoms exist or no alpha carbon is present
        if (leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isSideChain())
                .count() == 0 || leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isAlphaCarbon())
                .count() == 0) {
            return determineCentroid(leafSubstructure);
        }
        LabeledSymmetricMatrix<Atom> atomDistanceMatrix = Structures.calculateDistanceMatrix(leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isSideChain().and(AtomFilter.isHydrogen()
                        .negate()).or(AtomFilter.isAlphaCarbon()))
                .collect(Collectors.toList()));
        if (atomDistanceMatrix.getRowDimension() == 1) {
            return atomDistanceMatrix.getColumnLabel(0);
        }
        int maximalElementIndex = Vectors.getIndexWithMaximalElement(atomDistanceMatrix.getRowByLabel(((AminoAcid) leafSubstructure).getAlphaCarbon()));
        Atom referenceAtom = atomDistanceMatrix.getColumnLabel(maximalElementIndex);
        return new UncertainAtom(leafSubstructure.getAllAtoms().get(leafSubstructure.getAllAtoms().size()-1).getIdentifier(),
                ElementProvider.UNKOWN,
                RepresentationSchemeType.LAST_HEAVY_SIDE_CHAIN.getAtomNameString(),
                referenceAtom.getPosition());
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.LAST_HEAVY_SIDE_CHAIN;
    }
}
