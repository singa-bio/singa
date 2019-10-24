package bio.singa.javafx.renderer.layouts.force;

import bio.singa.javafx.renderer.graphs.GraphRenderer;
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
 * This class tries to arrange a graph using force directed placement. <br> The algorithm is based on Fruchterman,
 * Thomas MJ, and Edward M. Reingold. "Graph drawing by force-directed placement." Softw., Pract. Exper. 21.11 (1991):
 * 1129-1164.<br> With some modifications.
 *
 * @author cl
 */
public class ForceDirectedGraphLayout<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> implements LayoutRenderer<NodeType, EdgeType, IdentifierType, GraphType> {

    private static final double DEFAULT_FORCE_CONSTANT_FACTOR = 10.0;
    private final int totalIterations;
    private final DoubleProperty drawingWidth;
    private final DoubleProperty drawingHeight;
    private final double forceConstantFactor;
    private final IntegerProperty nodeNumber;
    private final Map<NodeType, Vector2D> velocities;
    private GraphType graph;
    private DoubleBinding forceConstant;
    private int iteration;
    private List<Force<NodeType, EdgeType, IdentifierType, GraphType>> forces;
    private Collection<IdentifierType> fixedNodes;

    public ForceDirectedGraphLayout(GraphType graph, GraphRenderer<NodeType, EdgeType, IdentifierType, GraphType> renderer, int totalIterations) {
        this(graph, renderer, totalIterations, DEFAULT_FORCE_CONSTANT_FACTOR);
    }

    public ForceDirectedGraphLayout(GraphType graph, GraphRenderer<NodeType, EdgeType, IdentifierType, GraphType> renderer, int totalIterations, double forceConstantFactor) {
        drawingWidth = renderer.drawingWidthProperty();
        drawingHeight = renderer.drawingHeightProperty();
        forces = new ArrayList<>();

        this.graph = graph;
        this.totalIterations = totalIterations;
        this.forceConstantFactor = forceConstantFactor;
        nodeNumber = new SimpleIntegerProperty(10);
        velocities = new HashMap<>();

        initializeForceConstant();

        fixedNodes = new ArrayList<>();
    }

    private void initializeForceConstant() {
        forceConstant = new DoubleBinding() {

            {
                super.bind(drawingWidth, drawingHeight, nodeNumber);
            }

            @Override
            protected double computeValue() {
                return Math.sqrt((drawingHeight.get() * drawingWidth.get()) / (nodeNumber.get() * forceConstantFactor));
            }
        };
    }

    public GraphType getGraph() {
        return graph;
    }

    public void setGraph(GraphType graph) {
        this.graph = graph;
        nodeNumber.setValue(graph.getNodes().size() * 2);
    }

    public double getDrawingWidth() {
        return drawingWidth.get();
    }

    public void setDrawingWidth(double drawingWidth) {
        this.drawingWidth.set(drawingWidth);
    }

    public DoubleProperty drawingWidthProperty() {
        return drawingWidth;
    }

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

    public List<Force<NodeType, EdgeType, IdentifierType, GraphType>> getForces() {
        return forces;
    }

    public void setForces(List<Force<NodeType, EdgeType, IdentifierType, GraphType>> forces) {
        this.forces = forces;
    }

    public void addForce(Force<NodeType, EdgeType, IdentifierType, GraphType> force) {
        forces.add(force);
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

    /**
     * Calculates one iteration of the optimization process and returns the resulting graph.
     *
     * @param i The current iteration.
     * @return The resulting graph
     */
    public GraphType arrangeGraph(int i) {

        // calculate the temperature
        double t = DecayFunctions.linear(i, totalIterations, drawingWidth.doubleValue() / 100);
        // apply forces
        velocities.clear();
        forces.forEach(nodeTypeForce -> nodeTypeForce.apply(graph));

        // placement depending on current velocity
        for (NodeType node : graph.getNodes()) {

            if (fixedNodes == null || fixedNodes.contains(node.getIdentifier())) {
                continue;
            }

            Vector2D currentLocation = node.getPosition();
            Vector2D currentVelocity = velocities.get(node);
            if (currentVelocity == null) {
                continue;
            }
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

}
