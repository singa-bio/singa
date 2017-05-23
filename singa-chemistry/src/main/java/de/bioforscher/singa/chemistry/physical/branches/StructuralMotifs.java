package de.bioforscher.singa.chemistry.physical.branches;

import de.bioforscher.singa.chemistry.algorithms.superimposition.SubStructureSuperimposer;
import de.bioforscher.singa.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.families.MatcherFamily;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility methods that deal with {@link StructuralMotif}s.
 *
 * @author fk
 */
public class StructuralMotifs {

    /**
     * prevent instantiation
     */
    private StructuralMotifs() {

    }

    public static void assignExchanges(StructuralMotif structuralMotif, EnumSet<MatcherFamily> familyGroup) {
        for (LeafSubstructure<?, ?> leafSubstructure : structuralMotif.getLeafSubstructures()) {
            familyGroup.stream()
                    .filter(family -> family.getMembers().contains(leafSubstructure.getFamily()))
                    .forEach(familyMember -> structuralMotif.addExchangeableFamily(leafSubstructure.getLeafIdentifier(),
                            familyMember));
        }
    }

    /**
     * Performs a superimposition of given {@link StructuralMotif}s using arbitrary atoms and returns the RMSD distance
     * matrix between all elements.
     *
     * @param structuralMotifs     The input structural motifs.
     * @param idealSuperimposition If ideal superimposition should be performed.
     * @return A {@link LabeledSymmetricMatrix} that contains all-against-all RMSD values.
     */
    public static LabeledSymmetricMatrix<StructuralMotif> calculateRmsdMatrix(List<StructuralMotif> structuralMotifs,
                                                                              boolean idealSuperimposition) {
        return calculateRmsdMatrix(structuralMotifs, AtomFilter.isArbitrary(), idealSuperimposition);
    }

    /**
     * Performs a superimposition of given {@link StructuralMotif}s using the specified {@link AtomFilter}
     * and returns the RMSD distance matrix between all elements.
     *
     * @param structuralMotifs     The input structural motifs.
     * @param atomFilter           The {@link AtomFilter} used for the superimposition.
     * @param idealSuperimposition If ideal superimposition should be performed.
     * @return A {@link LabeledSymmetricMatrix} that contains all-against-all RMSD values.
     */
    public static LabeledSymmetricMatrix<StructuralMotif> calculateRmsdMatrix(List<StructuralMotif> structuralMotifs,
                                                                              Predicate<Atom> atomFilter,
                                                                              boolean idealSuperimposition) {

        // check for correct input of same size
        if (structuralMotifs.stream()
                .map(StructuralMotif::size)
                .distinct()
                .count() != 1) {
            throw new IllegalArgumentException("RMSD matrix can only be calculated for structural motifs of same size");
        }

        double[][] temporaryDistanceMatrix = new double[structuralMotifs.size()][structuralMotifs.size()];
        List<StructuralMotif> matrixLabels = new ArrayList<>();
        // initially append first label
        matrixLabels.add(structuralMotifs.get(0));

        for (int i = 0; i < structuralMotifs.size() - 1; i++) {

            for (int j = i + 1; j < structuralMotifs.size(); j++) {

                StructuralMotif reference = structuralMotifs.get(i);
                StructuralMotif candidate = structuralMotifs.get(j);

                // calculate superimposition
                SubstructureSuperimposition superimposition = idealSuperimposition ?
                        SubStructureSuperimposer.calculateIdealSubstructureSuperimposition(reference, candidate, atomFilter) :
                        SubStructureSuperimposer.calculateSubstructureSuperimposition(reference, candidate, atomFilter);

                // store distance matrix
                temporaryDistanceMatrix[i][j] = superimposition.getRmsd();
                temporaryDistanceMatrix[j][i] = superimposition.getRmsd();

            }
            // store label
            matrixLabels.add(structuralMotifs.get(i + 1));
        }
        LabeledSymmetricMatrix<StructuralMotif> rmsdMatrix = new LabeledSymmetricMatrix<>(temporaryDistanceMatrix);
        rmsdMatrix.setColumnLabels(matrixLabels);
        return rmsdMatrix;
    }
}
