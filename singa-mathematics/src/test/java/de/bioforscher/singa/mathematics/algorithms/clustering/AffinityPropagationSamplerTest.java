package de.bioforscher.singa.mathematics.algorithms.clustering;

import de.bioforscher.singa.mathematics.matrices.LabeledMatrix;
import de.bioforscher.singa.mathematics.matrices.Matrices;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class AffinityPropagationSamplerTest {

    @Test
    public void shouldSampleAffinityPropagation() throws IOException {

        LabeledMatrix<String> rmsdMatrix = Matrices.readLabeledMatrixFromCSV(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("clustering/rmsd_distances.csv"));

        AffinityPropagationSampler<String> affinityPropagationSampler = new AffinityPropagationSampler<>(rmsdMatrix.getRowLabels(), rmsdMatrix);
    }

}