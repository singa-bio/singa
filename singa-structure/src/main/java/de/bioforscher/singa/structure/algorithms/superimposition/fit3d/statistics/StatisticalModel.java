package de.bioforscher.singa.structure.algorithms.superimposition.fit3d.statistics;


import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.Fit3DMatch;

import java.io.IOException;
import java.util.List;

/**
 * @author fk
 */
public interface StatisticalModel {

    void calculatePvalues(List<Fit3DMatch> matches) throws IOException, InterruptedException;
}
