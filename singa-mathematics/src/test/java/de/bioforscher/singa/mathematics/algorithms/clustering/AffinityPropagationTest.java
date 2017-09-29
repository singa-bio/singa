package de.bioforscher.singa.mathematics.algorithms.clustering;

import de.bioforscher.singa.mathematics.matrices.LabeledMatrix;
import de.bioforscher.singa.mathematics.matrices.Matrices;
import org.junit.Test;

import java.io.IOException;

public class AffinityPropagationTest {

    @Test
    public void shouldRunClustering() throws IOException {
        LabeledMatrix<String> rmsdMatrix = Matrices.readLabeledMatrixFromCSV(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("clustering/rmsd_distances.csv"));
        AffinityPropagation<String> affinityPropagation = new AffinityPropagation<>(rmsdMatrix.getRowLabels(),
                rmsdMatrix, true);
        affinityPropagation.run();
    }
}