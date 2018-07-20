package bio.singa.mathematics.algorithms.clustering;

import bio.singa.mathematics.matrices.LabeledMatrix;
import bio.singa.mathematics.matrices.Matrices;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AffinityPropagationTest {

    @Test
    public void shouldRunClustering() throws IOException {
        LabeledMatrix<String> rmsdMatrix = Matrices.readLabeledMatrixFromCSV(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("clustering/rmsd_distances.csv"));
        AffinityPropagation<String> affinityPropagation = AffinityPropagation.<String>create()
                .dataPoints(rmsdMatrix.getRowLabels())
                .matrix(rmsdMatrix)
                .isDistance(true)
                .selfSimilarity(0.3)
                .maximalEpochs(100)
                .run();
        affinityPropagation.getSilhouetteCoefficient();
        assertEquals(2, affinityPropagation.getClusters().size());
    }

    @Test
    public void shouldRunClusteringSelfSimilarityByMedian() throws IOException {
        LabeledMatrix<String> rmsdMatrix = Matrices.readLabeledMatrixFromCSV(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("clustering/rmsd_distances.csv"));
        AffinityPropagation<String> affinityPropagation = AffinityPropagation.<String>create()
                .dataPoints(rmsdMatrix.getRowLabels())
                .matrix(rmsdMatrix)
                .isDistance(true)
                .selfSimilarityByMedian()
                .maximalEpochs(100)
                .run();
        affinityPropagation.getSilhouetteCoefficient();
        assertEquals(3, affinityPropagation.getClusters().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithWrongInput() throws IOException {
        LabeledMatrix<String> rmsdMatrix = Matrices.readLabeledMatrixFromCSV(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("clustering/rmsd_distances.csv"));
        rmsdMatrix.getRowLabels().remove(0);
        List<String> data = rmsdMatrix.getRowLabels();
        data.remove(0);
        AffinityPropagation.<String>create()
                .dataPoints(data)
                .matrix(rmsdMatrix)
                .isDistance(true)
                .selfSimilarity(0.3)
                .maximalEpochs(100)
                .run();
    }
}