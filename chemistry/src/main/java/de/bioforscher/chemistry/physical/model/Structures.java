package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.mathematics.metrics.model.VectorMetricProvider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Methods to use for structures...
 */
public class Structures {

    /**
     * prevent instantiation
     */
    private Structures() {

    }

    /**
     * Returns the distance matrix of the given {@link StructuralMotif}.
     * <p>
     * TODO this method could be generified by using the method below. But how to do this?
     *
     * @param structuralMotif The {@link StructuralMotif} for which a distance matrix should be obtained.
     * @return The distance matrix of the {@link StructuralMotif}.
     */
    public static LabeledSymmetricMatrix<LeafSubstructure<?, ?>> calculateDistanceMatrix(StructuralMotif structuralMotif) {
        LabeledSymmetricMatrix<LeafSubstructure<?, ?>> labeledDistances = new LabeledSymmetricMatrix<>(
                VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(structuralMotif.getLeafSubstructures().stream()
                        .map(StructuralEntity::getPosition)
                        .collect(Collectors.toList())).getElements());
        labeledDistances.setRowLabels(structuralMotif.getLeafSubstructures());
        return labeledDistances;
    }

    /**
     * Returns the distance matrix of the given {@link StructuralEntity} object.
     * <p>
     * TODO This should be the only generic method to calculate distance matrices.
     *
     * @param structuralEntities The list of {@link StructuralEntity} objects for which a distance matrix should be obtained.
     * @return The distance matrix of the {@link StructuralEntity} objects.
     */
    public static <T extends StructuralEntity<T>> LabeledSymmetricMatrix<T> calculateDistanceMatrix(List<T> structuralEntities) {
        LabeledSymmetricMatrix<T> labeledDistances = new LabeledSymmetricMatrix<>(
                VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(structuralEntities.stream()
                        .map(StructuralEntity::getPosition)
                        .collect(Collectors.toList())).getElements());
        labeledDistances.setRowLabels(structuralEntities);
        return labeledDistances;
    }
//
//    public static LabeledSymmetricMatrix<Atom> calculateDistanceMatrix(List<Atom> atoms) {
//        LabeledSymmetricMatrix<Atom> labeledDistances = new LabeledSymmetricMatrix<>(
//                VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(atoms.stream()
//                        .map(StructuralEntity::getPosition)
//                        .collect(Collectors.toList())).getElements());
//        labeledDistances.setRowLabels(atoms);
//        return labeledDistances;
//    }
}
