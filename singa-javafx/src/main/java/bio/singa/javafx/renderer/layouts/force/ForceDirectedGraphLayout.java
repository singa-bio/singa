package bio.singa.javafx.renderer.layouts.force;

import bio.singa.javafx.renderer.layouts.LayoutRenderer;
import bio.singa.mathematics.functions.DecayFunctions;
import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.*;

/**
 * This class tries to arrange a graph using force directed placement. <br>
 * The algorithm is based on Fruchterman, Thomas MJ, and Edward M. Reingold.
 * "Graph drawing by force-directed placement." Softw., Pract. Exper. 21.11
 * (1991): 1129-1164.<br>
 * With some modifications.
 *
 * @author cl
 */
public class ForceDirectedGraphLayout<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> implements LayoutRenderer<NodeType, EdgeType, IdentifierType, GraphType> {

    private final int totalIterations;
    private final DoubleProperty drawingWidth;
    private final DoubleProperty drawingHeight;
    private final IntegerProperty nodeNumber;
    private final GraphType graph;
    private final Map<NodeType, Vector2D> velocities;
    private DoubleBinding forceConstant;
    private int iteration;
    private List<BinaryForce<NodeType>> forces;
    private Collection<IdentifierType> fixedNodes;
    private UnaryForce<NodeType> centralPull;


    /**
     * Creates a new GraphDrawingTool.
     *
     * @param totalIterations Number of total iterations
     * @param graph The graph to arrange
     * @param drawingHeight The height property.
     * @param drawingWidth The width property.
     */
    public ForceDirectedGraphLayout(GraphType graph, DoubleProperty drawingWidth, DoubleProperty drawingHeight, int totalIterations) {
        this.drawingWidth = drawingWidth;
        this.drawingHeight = drawingHeight;
        this.totalIterations = totalIterations;
        this.graph = graph;
        nodeNumber = new SimpleIntegerProperty(graph.getNodes().size() * 2);
        velocities = new HashMap<>();

        initializeForceConstant();

        BinaryForce<NodeType> generalAttraction = new BinaryAttractiveForce<>(this);
        BinaryForce<NodeType> generalRepulsion = new BinaryRepulsiveForce<>(this);
//        centralPull = new UnaryAttractiveForce<>(this, drawingWidth.divide(2.0), drawingHeight.divide(2.0));

        forces = new ArrayList<>();
        forces.add(generalAttraction);
        forces.add(generalRepulsion);
//        forces.add(centralPull);

        fixedNodes = new ArrayList<>();
    }

    private void initializeForceConstant() {
        forceConstant = new DoubleBinding() {

            {
                super.bind(drawingWidth, drawingHeight, nodeNumber);
            }

            @Override
            protected double computeValue() {
                return Math.sqrt((drawingHeight.get() * drawingWidth.get()) / (nodeNumber.get()));
            }
        };
    }

    @Override
    public double getDrawingWidth() {
        return drawingWidth.get();
    }

    public void setDrawingWidth(double drawingWidth) {
        this.drawingWidth.set(drawingWidth);
    }

    public DoubleProperty drawingWidthProperty() {
        return drawingWidth;
    }

    @Override
    public double getDrawingHeight() {
        return drawingHeight.get();
    }

    public void setDrawingHeight(double drawingHeight) {
        this.drawingHeight.set(drawingHeight);
    }

    public DoubleProperty drawingHeightProperty() {
        return drawingHeight;
    }

    public Number getForceConstant() {
        return forceConstant.get();
    }

    public DoubleBinding forceConstantProperty() {
        return forceConstant;
    }

    public Map<NodeType, Vector2D> getVelocities() {
        return velocities;
    }

    public void fixNodes(Collection<IdentifierType> identifiers) {
        fixedNodes = identifiers;
    }

    @Override
    public GraphType optimizeLayout() {
        iteration++;
        return arrangeGraph(iteration);
    }

    /**
     * Calculates one iteration of the optimization process and returns the
     * resulting graph.
     *
     * @param i The current iteration.
     * @return The resulting graph
     */
    public GraphType arrangeGraph(int i) {

        // calculate the temperature
        double t = DecayFunctions.linear(i, totalIterations, drawingWidth.doubleValue() / 50);
        // apply forces
        forces.forEach(nodeTypeForce -> nodeTypeForce.apply(graph));

        // placement depending on current velocity
        for (NodeType node : graph.getNodes()) {

            if (fixedNodes == null || fixedNodes.contains(node.getIdentifier())) {
                continue;
            }

            Vector2D currentLocation = node.getPosition();
            Vector2D currentVelocity = velocities.get(node);
            double magnitude = currentVelocity.getMagnitude();

            // calculate new position v = v.pos + v^ * min(|v|,temp)
            Vector2D nextLocation = currentLocation.add(currentVelocity.normalize().multiply(Math.min(magnitude, t)));

            // taking care, that the vertices aren't placed outside of the canvas
            // TODO: could be better
            double nextX;
            if (nextLocation.getX() < drawingWidth.doubleValue() && nextLocation.getX() > 0.0) {
                nextX = nextLocation.getX();
            } else {
                nextX = drawingWidth.doubleValue() / 2 + Math.random() - 0.5;
            }

            double nextY;
            if (nextLocation.getY() < drawingHeight.doubleValue() && nextLocation.getY() > 0.0) {
                nextY = nextLocation.getY();
            } else {
                nextY = drawingHeight.doubleValue() / 2 + Math.random() - 0.5;
            }

            // place node
            node.setPosition(new Vector2D(nextX, nextY));

        }

        // returns the optimized graph
        return graph;

    }

    private void applyForce() {
        List<NodeType> nodes = new ArrayList<>(graph.getNodes());
        velocities.clear();
        Vector2D[][] compactValues = new Vector2D[nodes.size()][];
        for (int rowIndex = 0; rowIndex < nodes.size(); rowIndex++) {
            compactValues[rowIndex] = new Vector2D[rowIndex + 1];
        }
        // compute forces
        for (int rowIndex = 0; rowIndex < compactValues.length; rowIndex++) {
            NodeType source = nodes.get(rowIndex);
//            centralPull.determineDisplacement(source);
            for (int columnIndex = 0; columnIndex < compactValues[rowIndex].length - 1; columnIndex++) {
                NodeType target = nodes.get(columnIndex);
                if (source == target) {
                    continue;
                }
                for (BinaryForce<NodeType> force : forces) {
                    // calculate accelerations
                    force.determineDisplacement(source, target);
                }
            }
        }
    }

}
