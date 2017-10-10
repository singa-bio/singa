package de.bioforscher.singa.mathematics.algorithms.clustering;

import de.bioforscher.singa.mathematics.matrices.LabeledMatrix;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public interface Clustering<DataType> {

    List<DataType> getDataPoints();

    LabeledMatrix<DataType> getDistanceMatrix();

    Map<DataType, List<DataType>> getClusters();

    default double getSilhouetteCoefficient() {
        double scGlobal = 0.0;
        for (List<DataType> cluster : getClusters().values()) {
            double sc = 0.0;
            if (cluster.size() != 1) {
                for (DataType dataPoint : cluster) {
                    double distA = 0.0;
                    for (DataType clusterMember : cluster) {
                        if (!dataPoint.equals(clusterMember)) {
                            distA += getDistanceMatrix().getValueForLabel(dataPoint, clusterMember);
                        }
                    }
                    distA /= cluster.size();

                    List<List<DataType>> otherClusters = getClusters().values().stream()
                            .filter(c -> !c.contains(dataPoint))
                            .collect(Collectors.toList());
                    double distB = Double.MAX_VALUE;
                    for (List<DataType> otherCluster : otherClusters) {
                        double distC = 0.0;
                        for (DataType clusterMember : otherCluster) {
                            if (!dataPoint.equals(clusterMember)) {
                                distC += getDistanceMatrix().getValueForLabel(dataPoint, clusterMember);
                            }
                        }
                        distC /= otherCluster.size();
                        if (distC < distB) {
                            distB = distC;
                        }
                    }
                    double s;
                    if (distA == distB) {
                        s = 0;
                    } else {
                        s = (distB - distA) / Math.max(distA, distB);
                    }
                    sc += s;
                }
                sc /= cluster.size();
            }
            scGlobal += sc;
        }
        return scGlobal / getClusters().size();
    }
}
