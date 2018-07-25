package bio.singa.mathematics.algorithms.topology;

import bio.singa.mathematics.topology.model.DiscreteCoordinate;
import bio.singa.mathematics.topology.model.DiscreteDirection;
import bio.singa.mathematics.topology.model.DiscreteGrid;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author cl
 */
public class FloodFill {

    /**
     * Applies the FloodFill algorithm to the grid and modifies it.
     *
     * @param grid The grid to be modified.
     * @param pourPosition The initial position for the pour to start.
     * @param wallPredicate The predicate, returning true if the value shall be considered as a wall.
     * @param replacementConsumer The modification that should be made to valid values.
     * @param recurrencePredicate The predicate, returning true if the modifications for the replacement have already been applied.
     *
     * @param <ValueType> The kind of value.
     * @param <DirectionType> The kind of directions.
     * @param <CoordinateType> The kind of coordinates.
     */
    public static <ValueType, DirectionType extends DiscreteDirection, CoordinateType extends DiscreteCoordinate<CoordinateType, DirectionType>>
    void fill(DiscreteGrid<ValueType, DirectionType, CoordinateType> grid,
              CoordinateType pourPosition,
              Predicate<ValueType> wallPredicate,
              Consumer<CoordinateType> replacementConsumer,
              Predicate<ValueType> recurrencePredicate) {
        // sorry for the generics
        ValueType value = grid.getValue(pourPosition);
        // not already processed
        if (recurrencePredicate.test(value)) {
            return;
        }
        // not wall
        if (wallPredicate.test(value)) {
            return;
        }
        // apply replacement
        replacementConsumer.accept(pourPosition);
        // flow to neighbours
        for (CoordinateType coordinate : pourPosition.getListOfNeighbours()) {
            fill(grid, coordinate, wallPredicate, replacementConsumer, recurrencePredicate);
        }
    }

}
