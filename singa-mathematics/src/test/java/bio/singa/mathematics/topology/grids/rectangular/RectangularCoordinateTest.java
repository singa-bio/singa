package bio.singa.mathematics.topology.grids.rectangular;

import bio.singa.mathematics.graphs.model.AbstractNode;
import bio.singa.mathematics.graphs.model.Graphs;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class RectangularCoordinateTest {

    @Test
    void sortCoordinates() {

        List<RectangularCoordinate> coordinates = Graphs.buildGridGraph(10, 10).getNodes().stream()
                .map(AbstractNode::getIdentifier)
                .collect(Collectors.toList());
        Collections.shuffle(coordinates);
        coordinates.sort(RectangularCoordinate.COLUMN_FIRST);
        for (int i = 1; i < coordinates.size(); i++) {
            RectangularCoordinate predecessor = coordinates.get(i - 1);
            RectangularCoordinate current = coordinates.get(i);
            assertTrue(predecessor.getRow() <= current.getRow() || (predecessor.getRow() == 9 && current.getRow() == 0));
            assertTrue(predecessor.getColumn() <= current.getColumn() );
        }
    }
}