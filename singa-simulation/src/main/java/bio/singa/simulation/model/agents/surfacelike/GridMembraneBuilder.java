package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.core.utility.Pair;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.topology.grids.rectangular.NeumannRectangularDirection;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.mathematics.topology.grids.rectangular.RectangularGrid;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellSubsection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class GridMembraneBuilder {

    private Map<Integer, CellRegion> subsectionMap;
    private Map<Pair<Integer>, CellRegion> membraneMap;

    private AutomatonGraph graph;
    private Map<CellRegion, Membrane> membranes;
    private int[][] sectionArray;

    public GridMembraneBuilder(int[][] sectionArray) {
        this.sectionArray = sectionArray;
        subsectionMap = new HashMap<>();
        subsectionMap.put(0, CellRegions.CYTOPLASM_REGION);
        subsectionMap.put(1, CellRegions.EXTRACELLULAR_REGION);
        membraneMap = new HashMap<>();
        membraneMap.put(new Pair<>(0, 1), CellRegions.CELL_OUTER_MEMBRANE_REGION);
    }

    public Map<Integer, CellRegion> getSubsectionMap() {
        return subsectionMap;
    }

    public void setSubsectionMap(Map<Integer, CellRegion> subsectionMap) {
        this.subsectionMap = subsectionMap;
    }

    public Map<Pair<Integer>, CellRegion> getMembraneMap() {
        return membraneMap;
    }

    public void setMembraneMap(Map<Pair<Integer>, CellRegion> membraneMap) {
        this.membraneMap = membraneMap;
    }

    public AutomatonGraph getGraph() {
        return graph;
    }

    public void setGraph(AutomatonGraph graph) {
        this.graph = graph;
    }

    public Map<CellRegion, Membrane> getMembranes() {
        return membranes;
    }

    public void setMembranes(Map<CellRegion, Membrane> membranes) {
        this.membranes = membranes;
    }

    public void createTopology() {
        int cols = sectionArray.length;
        int rows = sectionArray[0].length;

        graph = AutomatonGraphs.createRectangularAutomatonGraph(cols, rows);
        // fill grid
        RectangularGrid<Integer> originalGrid = new RectangularGrid<>(cols, rows);
        int currentRow = 0;
        for (int[] row : sectionArray) {
            int currentColumn = 0;
            for (int cell : row) {
                originalGrid.setValue(currentRow, currentColumn, cell);
                currentColumn++;
            }
            currentRow++;
        }
        System.out.println(originalGrid);

        // create membrane grid
        RectangularGrid<CellRegion> regionGrid = new RectangularGrid<>(cols, rows);
        for (int column = 0; column < cols; column++) {
            for (int row = 0; row < rows; row++) {
                RectangularCoordinate coordinate = new RectangularCoordinate(column, row);
                int center = originalGrid.getValue(coordinate);
                List<Integer> neighbours = originalGrid.getNeighboursOf(coordinate);
                boolean border = false;
                int other = center;
                for (Integer neighbour : neighbours) {
                    other = neighbour;
                    // is border
                    if (neighbour != center) {
                        border = true;
                        break;
                    }
                }
                // define region
                CellRegion region;
                if (border) {
                    Pair<Integer> pair = new Pair<>(center, other);
                    region = membraneMap.get(pair);
                } else {
                    region = subsectionMap.get(center);
                }
                regionGrid.setValue(column, row, region);


            }
        }
        System.out.println(regionGrid);

        // associate regions and subsections
        for (int column = 0; column < cols; column++) {
            for (int row = 0; row < rows; row++) {
                AutomatonNode node = graph.getNode(column, row);
                node.setCellRegion(regionGrid.getValue(column, row));
            }
        }

        // setup node representations
        for (AutomatonNode node : graph.getNodes()) {
            // create rectangles centered on the nodes with side length of node distance
            Vector2D position = node.getPosition();
            double offset = Environment.convertSystemToSimulationScale(UnitRegistry.getSpace()) * 0.5;
            Vector2D topLeft = new Vector2D(position.getX() - offset, position.getY() - offset);
            Vector2D bottomRight = new Vector2D(position.getX() + offset, position.getY() + offset);
            node.setSpatialRepresentation(new Rectangle(topLeft, bottomRight));
        }

        // setup membrane and subsections
        membranes = new HashMap<>();
        for (int column = 0; column < cols; column++) {
            for (int row = 0; row < rows; row++) {
                Integer centerValue = originalGrid.getValue(column, row);
                RectangularCoordinate coordinate = new RectangularCoordinate(column, row);
                Map<NeumannRectangularDirection, Integer> valueMap = originalGrid.getValueMap(coordinate);
                for (Map.Entry<NeumannRectangularDirection, Integer> entry : valueMap.entrySet()) {
                    NeumannRectangularDirection direction = entry.getKey();
                    int neighborValue = entry.getValue();
                    AutomatonNode node = graph.getNode(column, row);
                    // determine and add segment
                    if (centerValue != neighborValue) {
                        Rectangle representation = (Rectangle) node.getSpatialRepresentation();
                        LineSegment segment;
                        switch (direction) {
                            case NORTH:
                                segment = representation.getTopEdge();
                                break;
                            case SOUTH:
                                segment = representation.getBottomEdge();
                                break;
                            case EAST:
                                segment = representation.getRightEdge();
                                break;
                            default:
                                segment = representation.getLeftEdge();
                                break;
                        }
                        CellRegion membraneRegion = regionGrid.getValue(column, row);
                        Membrane membrane = membranes.computeIfAbsent(membraneRegion, k -> new Membrane(membraneRegion.getIdentifier()));
                        membrane.addSegment(node, segment);
                    }
                    // setup subsections
                    CellSubsection subsection = subsectionMap.get(centerValue).getInnerSubsection();
                    node.addSubsectionRepresentation(subsection, node.getSpatialRepresentation());
                }
            }
        }
    }

}
