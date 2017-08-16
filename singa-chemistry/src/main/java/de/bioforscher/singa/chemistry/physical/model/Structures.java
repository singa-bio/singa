package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter;
import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider;

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
     * Returns the squared distance matrix of the given {@link StructuralMotif}.
     * <p>
     * TODO this method could be generified by using the method below. But how to do this?
     *
     * @param structuralMotif The {@link StructuralMotif} for which a distance matrix should be obtained.
     * @return The squared distance matrix of the {@link StructuralMotif}.
     */
    public static LabeledSymmetricMatrix<LeafSubstructure<?, ?>> calculateSquaredDistanceMatrix(StructuralMotif structuralMotif) {
        LabeledSymmetricMatrix<LeafSubstructure<?, ?>> labeledDistances = new LabeledSymmetricMatrix<>(
                VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC.calculateDistancesPairwise(structuralMotif.getLeafSubstructures().stream()
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
     * @param <EntityType> The Type of the structural entity.
     * @param structuralEntities The list of {@link StructuralEntity} objects for which a distance matrix should be
     * obtained.
     * @return The distance matrix of the {@link StructuralEntity} objects.
     */
    public static <EntityType extends StructuralEntity<EntityType, ?>> LabeledSymmetricMatrix<EntityType> calculateDistanceMatrix(List<EntityType> structuralEntities) {
        LabeledSymmetricMatrix<EntityType> labeledDistances = new LabeledSymmetricMatrix<>(
                VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(structuralEntities.stream()
                        .map(StructuralEntity::getPosition)
                        .collect(Collectors.toList())).getElements());
        labeledDistances.setRowLabels(structuralEntities);
        return labeledDistances;
    }

    /**
     * Returns the distance matrix of the given {@link StructuralEntity} object.
     * <p>
     * TODO This should be the only generic method to calculate distance matrices.
     *
     * @param <EntityType> The Type of the structural entity.
     * @param structuralEntities The list of {@link StructuralEntity} objects for which a distance matrix should be
     * obtained.
     * @return The squared distance matrix of the {@link StructuralEntity} objects.
     */
    public static <EntityType extends StructuralEntity<EntityType, ?>> LabeledSymmetricMatrix<EntityType> calculateSquaredDistanceMatrix(List<EntityType> structuralEntities) {
        LabeledSymmetricMatrix<EntityType> labeledDistances = new LabeledSymmetricMatrix<>(
                VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC.calculateDistancesPairwise(structuralEntities.stream()
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

    /**
     * Returns true iff the given {@link Structure} consists only of alpha carbon atoms
     * (<b>this may include hydrogen atoms</b>).
     *
     * @param structure The {@link Structure} to check.
     * @return True iff structure contains only alpha carbon atoms.
     */
    public static boolean isAlphaCarbonStructure(Structure structure) {
        return structure.getAllAtoms().stream()
                .noneMatch(AtomFilter.isAlphaCarbon().negate()
                        .and(AtomFilter.isHydrogen().negate()));
    }

    /**
     * Returns true iff the given {@link Structure} consists only of backbone atoms
     * (<b>this may include beta carbon and hydrogen atoms</b>).
     *
     * @param structure The {@link Structure} to check.
     * @return True iff structure contains only backbone and hydrogen atoms.
     */
    public static boolean isBackboneStructure(Structure structure) {
        return structure.getAllAtoms().stream()
                .noneMatch(AtomFilter.isBackbone().negate()
                        .and(AtomFilter.isHydrogen().negate())
                        .and(AtomFilter.isBetaCarbon().negate()));
    }
}
