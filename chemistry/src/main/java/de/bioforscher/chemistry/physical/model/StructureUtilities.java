package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.mathematics.metrics.model.VectorMetricProvider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 22.06.2016.
 */
public class StructureUtilities {

    public static LabeledSymmetricMatrix<Atom> calculateDistanceMatrix(List<Atom> atoms) {
        LabeledSymmetricMatrix<Atom> labeledDistances = new LabeledSymmetricMatrix<>(
                VectorMetricProvider.EUCLIDEAN_METRIC.calculateDistancesPairwise(atoms.stream()
                        .map(Atom::getPosition)
                        .collect(Collectors.toList())).getElements());
        labeledDistances.setRowLabels(atoms);
        return labeledDistances;
    }

}
