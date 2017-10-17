package de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d.statistics;

import de.bioforscher.singa.chemistry.algorithms.superimposition.SubstructureSuperimposition;

import java.io.IOException;
import java.util.TreeMap;

/**
 * @author fk
 */
public interface StatisticalModel {

    void calculatePvalues(TreeMap<Double, SubstructureSuperimposition> matches) throws IOException, InterruptedException;
}
