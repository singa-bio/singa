package bio.singa.mathematics.topology.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public interface DiscreteGrid<ValueType, DirectionType extends DiscreteDirection, CoordinateType extends DiscreteCoordinate<CoordinateType, DirectionType>> {

    void setValue(CoordinateType coordinate, ValueType value);

    ValueType getValue(CoordinateType coordinate);

    default List<ValueType> getNeighboursOf(CoordinateType coordinate) {
        List<ValueType> values = new ArrayList<>();
        for (CoordinateType neighbour : coordinate.getListOfNeighbours()) {
            if (isInRange(neighbour)) {
                ValueType value = getValue(neighbour);
                values.add(value);
            }
        }
        return values;
    }

    boolean isInRange(CoordinateType coordinate);

}
