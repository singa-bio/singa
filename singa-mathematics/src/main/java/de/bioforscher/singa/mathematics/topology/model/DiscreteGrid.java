package de.bioforscher.singa.mathematics.topology.model;

/**
 * @author cl
 */
public interface DiscreteGrid<ValueType, CoordinateType extends DiscreteCoordinate<CoordinateType, ? extends DiscreteDirection>>  {

    void setValue(CoordinateType coordinate, ValueType value);

    ValueType getValue(CoordinateType coordinate);

}
