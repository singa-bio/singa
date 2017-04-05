package de.bioforscher.chemistry.physical.atoms.representations;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.Structures;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.mathematics.vectors.Vectors;

import java.util.stream.Collectors;

import static de.bioforscher.chemistry.physical.model.StructuralEntityFilter.AtomFilter;

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
        if (!(leafSubstructure instanceof AminoAcid) || leafSubstructure.getAllAtoms().stream().noneMatch(
                AtomFilter.isAlphaCarbon())) {
            logger.warn("fallback for ", leafSubstructure);
            return determineCentroid(leafSubstructure);
        }
        if (leafSubstructure.getFamily() == AminoAcidFamily.GLYCINE) {
            return new BetaCarbonRepresentationScheme().determineCentroid(leafSubstructure);
        }
        // fallback if no sidechain atoms exist or no alpha carbon is present
        if (leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isSidechain())
                .count() == 0 || leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isAlphaCarbon())
                .count() == 0) {
            return determineCentroid(leafSubstructure);
        }
        LabeledSymmetricMatrix<Atom> atomDistanceMatrix = Structures.calculateDistanceMatrix(leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isSidechain().and(AtomFilter.isHydrogen()
                        .negate()).or(AtomFilter.isAlphaCarbon()))
                .collect(Collectors.toList()));
        if (atomDistanceMatrix.getRowDimension() == 1) {
            return atomDistanceMatrix.getColumnLabel(0);
        }
        int maximalElementIndex = Vectors.getIndexWithMaximalElement(atomDistanceMatrix.getRowByLabel(((AminoAcid) leafSubstructure).getAlphaCarbon()));
        return atomDistanceMatrix.getColumnLabel(maximalElementIndex);
    }

    @Override
    public RepresentationSchemeType getType() {
        return RepresentationSchemeType.LAST_HEAVY_SIDECHAIN;
    }
}
