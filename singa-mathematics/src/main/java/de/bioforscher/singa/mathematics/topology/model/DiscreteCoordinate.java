package de.bioforscher.singa.mathematics.topology.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DiscreteCoordinate<CoordinateType extends DiscreteCoordinate, DirectionType extends DiscreteDirection> {

    CoordinateType getNeighbour(DirectionType directionType);

    DirectionType[] getAllDirections();

    default List<CoordinateType> getListOfNeighbours() {
        List<CoordinateType> coordinates = new ArrayList<>();
        for (DirectionType direction : getAllDirections()) {
            coordinates.add(getNeighbour(direction));
        }
        return coordinates;
    }

    default Map<DirectionType, CoordinateType> getMapOfNeighbours() {
        Map<DirectionType, CoordinateType> coordinates = new HashMap<>();
        for (DirectionType direction : getAllDirections()) {
            coordinates.put(direction, getNeighbour(direction));
        }
        return coordinates;
    }



}
