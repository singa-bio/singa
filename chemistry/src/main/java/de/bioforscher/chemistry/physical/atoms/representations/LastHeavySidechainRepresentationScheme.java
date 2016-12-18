package de.bioforscher.chemistry.physical.atoms.representations;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.model.Structures;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.mathematics.vectors.Vectors;

import java.util.stream.Collectors;

/**
 * An implementation to represent a given {@link LeafSubstructure} by its last heavy sidechain atom (the atom most far
 * from the alpha carbon). This is only available for {@link Residue}s with defined alpha carbons. For glycine this
 * defaults to {@link BetaCarbonRepresentationScheme}.
 *
 * @author fk
 */
public class LastHeavySidechainRepresentationScheme extends AbstractRepresentationScheme {

    @Override
    public Atom determineRepresentingAtom(LeafSubstructure<?, ?> leafSubstructure) {
        if (!(leafSubstructure instanceof Residue) || leafSubstructure.getAllAtoms().stream().noneMatch(AtomFilter.isAlphaCarbon())) {
            logger.warn("fallback for ", leafSubstructure);
            return determineCentroid(leafSubstructure);
        }
        if (leafSubstructure.getFamily() == ResidueFamily.GLYCINE) {
            return new BetaCarbonRepresentationScheme().determineCentroid(leafSubstructure);
        }
        LabeledSymmetricMatrix<Atom> atomDistanceMatrix = Structures.calculateDistanceMatrix(leafSubstructure.getAllAtoms().stream()
                .filter(AtomFilter.isSidechain().and(AtomFilter.isHydrogen().negate()).or(AtomFilter.isAlphaCarbon()))
                .collect(Collectors.toList()));
        if (atomDistanceMatrix.getRowDimension() == 1) {
            return atomDistanceMatrix.getColumnLabel(0);
        }
        int maximalElementIndex = Vectors.getIndexWithMaximalElement(atomDistanceMatrix.getRowByLabel(((Residue) leafSubstructure).getAlphaCarbon()));
        return atomDistanceMatrix.getColumnLabel(maximalElementIndex);
    }
}
