package de.bioforscher.mathematics.topology.grids;

import de.bioforscher.mathematics.metrics.model.Metrizable;

public interface DiscreteCoordinate<Type> extends Metrizable<DiscreteCoordinate<Type>> {

    Type[] getNeighbours();

}
