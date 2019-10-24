package bio.singa.javafx.renderer.graphs;

import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * @author cl
 */
public class GraphCanvas extends Canvas {

    private final GraphRenderer renderer;
    private final Graph<? extends Node<?, Vector2D, ?>, ?, ?> graph;
    public BooleanProperty editMode;
    public Vector2D dragStart;
    public Node<?, Vector2D, ?> draggedNode;

    public GraphCanvas(GraphRenderer renderer, Graph<? extends Node<?, Vector2D, ?>, ?, ?> graph) {
        this.renderer = renderer;
        this.graph = graph;
        editMode = new SimpleBooleanProperty(true);
        // handle events
        addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClick);
        addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleDrag);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleDrag);
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleDrag);
    }

    public void handleDrag(MouseEvent event) {
        // drag moves node
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                dragStart = new Vector2D(event.getX(), event.getY());
                for (Node node : graph.getNodes()) {
                    if (isClickedOnNode(event, node)) {
                        draggedNode = node;
                        break;
                    }
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                if (draggedNode != null) {
                    draggedNode.setPosition(new Vector2D(event.getX(), event.getY()));
                    handleArrangement();
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                draggedNode = null;
                renderer.arrangeGraph(graph, 30);
            }
        }
    }

    private void handleClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.SECONDARY)) {
            handleRightClick(event);
        } else if (event.getButton().equals(MouseButton.PRIMARY)) {
            handleLeftClick(event);
        }
    }

    private void handleRightClick(MouseEvent event) {
        boolean isNode = false;
        for (Node node : graph.getNodes()) {
            if (isClickedOnNode(event, node)) {
                // do something in case of right click on node
                isNode = true;
                break;
            }
        }
        if (!isNode) {
            // do something in case of right click not on node
        }
    }

    private void handleLeftClick(MouseEvent event) {
        for (Node<?, Vector2D, ?> node : graph.getNodes()) {
            if (isClickedOnNode(event, node)) {
                // do something in case of left click on node
                break;
            }
        }
    }

    private void handleArrangement() {
        renderer.arrangeOnce(graph);
    }

    private boolean isClickedOnNode(MouseEvent event, Node<?, Vector2D, ?> node) {
        return node.getPosition().isNearVector(new Vector2D(event.getX() + renderer.getRenderingOptions().getNodeDiameter() / 2,
                        event.getY() + renderer.getRenderingOptions().getNodeDiameter() / 2),
                renderer.getRenderingOptions().getNodeDiameter() / 2);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

}
