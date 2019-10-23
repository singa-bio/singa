package bio.singa.javafx.renderer.graphs;

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

    public BooleanProperty editMode;
    public Vector2D dragStart;
    public Node<?, Vector2D, ?> draggedNode;

    public GraphCanvas() {
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
                for (Node node : GraphDisplayApplication.getGraph().getNodes()) {
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
                GraphDisplayApplication.renderer.arrangeGraph(GraphDisplayApplication.getGraph(), 30);
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
        for (Node node : GraphDisplayApplication.getGraph().getNodes()) {
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
        for (Node<?, Vector2D, ?> node : GraphDisplayApplication.getGraph().getNodes()) {
            if (isClickedOnNode(event, node)) {
                // do something in case of left click on node
                break;
            }
        }
    }

    private void handleArrangement() {
        if (GraphDisplayApplication.renderer.getRenderingMode().equals(GraphRenderer.RenderingMode.LLOYDS_RELAXATION.name())) {
            GraphDisplayApplication.renderer.relaxOnce(GraphDisplayApplication.getGraph());
        } else {
            GraphDisplayApplication.renderer.arrangeOnce(GraphDisplayApplication.getGraph());
        }
    }

    private boolean isClickedOnNode(MouseEvent event, Node<?, Vector2D, ?> node) {
        return node.getPosition().isNearVector(new Vector2D(event.getX() + GraphDisplayApplication.getRenderer().getRenderingOptions().getNodeDiameter() / 2,
                        event.getY() + GraphDisplayApplication.getRenderer().getRenderingOptions().getNodeDiameter() / 2),
                GraphDisplayApplication.getRenderer().getRenderingOptions().getNodeDiameter() / 2);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

}
