package de.bioforscher.mathematics;

import de.bioforscher.mathematics.geometry.GeometryTests;
import de.bioforscher.mathematics.graphs.GraphTests;
import de.bioforscher.mathematics.matrices.MatrixTests;
import de.bioforscher.mathematics.metrics.MetricTests;
import de.bioforscher.mathematics.vectors.VectorTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({GraphTests.class, GeometryTests.class, MatrixTests.class, MetricTests.class, VectorTests.class})
public class AllTests {

}
