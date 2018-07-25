package bio.singa.mathematics.topology.model;

/**
 * @author cl
 */
public interface DiscreteGrid<ValueType, DirectionType extends DiscreteDirection, CoordinateType extends DiscreteCoordinate<CoordinateType, DirectionType>>  {

    void setValue(CoordinateType coordinate, ValueType value);

    ValueType getValue(CoordinateType coordinate);

}
