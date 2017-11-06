package de.bioforscher.singa.mathematics.algorithms.optimization;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.matrices.LabeledMatrix;
import de.bioforscher.singa.mathematics.matrices.LabeledRegularMatrix;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KuhnMunkresTest {

    @Test
    public void shouldCalculateKuhnMunkres() {
        LabeledMatrix<String> costMatrix = new LabeledRegularMatrix<>(new double[][]{{2.0, 3.0, 3.0}, {3.0, 2.0, 3.0}, {3.0, 3.0, 2.0}});
        List<String> rowLabels = Stream.of("Armond", "Francine", "Herbert").collect(Collectors.toList());
        List<String> columnLabels = Stream.of("Clean bathroom", "Sweep floors", "Wash windows").collect(Collectors.toList());
        costMatrix.setRowLabels(rowLabels);
        costMatrix.setColumnLabels(columnLabels);
        KuhnMunkres<String> kuhnMunkres = new KuhnMunkres<>(costMatrix);
        List<Pair<String>> assignedPairs = kuhnMunkres.getAssignedPairs();
        Assert.assertTrue(assignedPairs.get(0).getFirst().equals("Armond"));
        Assert.assertTrue(assignedPairs.get(0).getSecond().equals("Clean bathroom"));
        Assert.assertTrue(assignedPairs.get(1).getFirst().equals("Francine"));
        Assert.assertTrue(assignedPairs.get(1).getSecond().equals("Sweep floors"));
        Assert.assertTrue(assignedPairs.get(2).getFirst().equals("Herbert"));
        Assert.assertTrue(assignedPairs.get(2).getSecond().equals("Wash windows"));
    }
}