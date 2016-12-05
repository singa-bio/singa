package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.mathematics.metrics.model.VectorMetricProvider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Methods to use for structures...
 */
public class StructureUtilities {

    public static LabeledSymmetricMatrix<LeafSubstructure<?, ?>> calculateDistanceMatrix(List<LeafSubstructure<?, ?>> leafSubstructures) {
        LabeledSymmetricMatrix<LeafSubstructure<?, ?>> labeledDistances = new LabeledSymmetricMatrix<>(
                VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(leafSubstructures.stream()
                        .map(StructuralEntity::getPosition)
                        .collect(Collectors.toList())).getElements());
        labeledDistances.setRowLabels(leafSubstructures);
        return labeledDistances;
    }

//    public static LabeledSymmetricMatrix<Substructure> calculateDistanceMatrix(List<Substructure> substructures) {
//        LabeledSymmetricMatrix<Substructure> labeledDistances = new LabeledSymmetricMatrix<>(
//                VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(substructures.stream()
//                        .map(Substructure::getPosition)
//                        .collect(Collectors.toList())).getElements());
//        labeledDistances.setRowLabels(substructures);
//        return labeledDistances;
//    }
}
