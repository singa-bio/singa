package bio.singa.structure.model.general;

import bio.singa.mathematics.matrices.LabeledSymmetricMatrix;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposer;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
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

    /**
     * Assigns the given {@link EnumSet} of exchanges to the specified {@link StructuralMotif}.
     *
     * @param structuralMotif The {@link StructuralMotif} to which the exchanges should be assigned.
     * @param familyGroup The group of {@link MatcherFamily} to be assigned.
     */
    public static void assignComplexExchanges(StructuralMotif structuralMotif, Set<StructuralFamily> familyGroup) {
        for (LeafSubstructure leafSubstructure : structuralMotif.getAllLeafSubstructures()) {
            familyGroup.stream()
                    .filter(family -> StructuralFamilies.Matchers.getMatcherEntities(family).contains(leafSubstructure.getFamily()))
                    .forEach(familyMember -> structuralMotif.addExchangeableFamily(leafSubstructure.getIdentifier(), familyMember));
        }
    }

    /**
     * Performs a superimposition of given {@link StructuralMotif}s using arbitrary atoms and returns the RMSD distance
     * matrix between all elements.
     *
     * @param structuralMotifs The input structural motifs.
     * @param idealSuperimposition If ideal superimposition should be performed.
     * @return A {@link LabeledSymmetricMatrix} that contains all-against-all RMSD values.
     */
    public static LabeledSymmetricMatrix<StructuralMotif> calculateRmsdMatrix(List<StructuralMotif> structuralMotifs,
                                                                              boolean idealSuperimposition) {
        return calculateRmsdMatrix(structuralMotifs, StructuralEntityFilter.AtomFilter.isArbitrary(), idealSuperimposition);
    }

    /**
     * Performs a superimposition of given {@link StructuralMotif}s using the specified {@link
     * StructuralEntityFilter.AtomFilter} and returns the RMSD distance matrix between all elements.
     *
     * @param structuralMotifs The input structural motifs.
     * @param atomFilter The {@link StructuralEntityFilter.AtomFilter} used for the superimposition.
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
                        SubstructureSuperimposer.calculateIdealSubstructureSuperimposition(reference, candidate, atomFilter) :
                        SubstructureSuperimposer.calculateSubstructureSuperimposition(reference, candidate, atomFilter);

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
