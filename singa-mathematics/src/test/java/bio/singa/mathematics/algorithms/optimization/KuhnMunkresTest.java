package bio.singa.mathematics.algorithms.optimization;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.matrices.LabeledMatrix;
import bio.singa.mathematics.matrices.LabeledRegularMatrix;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KuhnMunkresTest {

    @Test
    void shouldCalculateKuhnMunkres() {
        LabeledMatrix<String> costMatrix = new LabeledRegularMatrix<>(new double[][]{{2.0, 3.0, 3.0}, {3.0, 2.0, 3.0}, {3.0, 3.0, 2.0}});
        List<String> rowLabels = Stream.of("Armond", "Francine", "Herbert").collect(Collectors.toList());
        List<String> columnLabels = Stream.of("Clean bathroom", "Sweep floors", "Wash windows").collect(Collectors.toList());
        costMatrix.setRowLabels(rowLabels);
        costMatrix.setColumnLabels(columnLabels);
        KuhnMunkres<String> kuhnMunkres = new KuhnMunkres<>(costMatrix);
        List<Pair<String>> assignedPairs = kuhnMunkres.getAssignedPairs();
        assertEquals("Armond", assignedPairs.get(0).getFirst());
        assertEquals("Clean bathroom", assignedPairs.get(0).getSecond());
        assertEquals("Francine", assignedPairs.get(1).getFirst());
        assertEquals("Sweep floors", assignedPairs.get(1).getSecond());
        assertEquals("Herbert", assignedPairs.get(2).getFirst());
        assertEquals("Wash windows", assignedPairs.get(2).getSecond());
    }
}