package bio.singa.mathematics.algorithms.clustering;

import bio.singa.mathematics.matrices.LabeledMatrix;
import bio.singa.mathematics.matrices.Matrices;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class AffinityPropagationSamplerTest {

    @Test
    @Ignore
    public void shouldSampleAffinityPropagation() throws IOException {

        LabeledMatrix<String> rmsdMatrix = Matrices.readLabeledMatrixFromCSV(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("clustering/rmsd_distances.csv"));

        AffinityPropagationSampler<String> affinityPropagationSampler = new AffinityPropagationSampler<>(rmsdMatrix.getRowLabels(), rmsdMatrix);
    }

}