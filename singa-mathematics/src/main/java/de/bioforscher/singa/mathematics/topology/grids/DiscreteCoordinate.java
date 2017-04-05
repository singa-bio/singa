package de.bioforscher.singa.mathematics.topology.grids;

import de.bioforscher.singa.mathematics.metrics.model.Metrizable;

public interface DiscreteCoordinate<Type> extends Metrizable<DiscreteCoordinate<Type>> {

    Type[] getNeighbours();

}
