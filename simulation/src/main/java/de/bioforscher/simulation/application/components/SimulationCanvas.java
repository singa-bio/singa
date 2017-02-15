package de.bioforscher.simulation.application.components;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.mathematics.geometry.edges.LineSegment;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.application.renderer.BioGraphRenderer;
import de.bioforscher.simulation.model.BioNode;
import javafx.event.Event;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class SimulationCanvas extends Canvas {

    private BioGraphSimulation owner;
    private BioGraphRenderer renderer;
    private BioGraphContextMenu graphContextMenu;

    private Vector2D dragStart;

    public SimulationCanvas(BioGraphSimulation owner) {
        this.owner = owner;
        this.renderer = new BioGraphRenderer();
        this.graphContextMenu = new BioGraphContextMenu(this.owner.getSimulation(), this);

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClick);

        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleDrag);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleDrag);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleDrag);

        this.widthProperty().addListener(observable -> draw());
        this.heightProperty().addListener(observable -> draw());

        this.renderer.drawingWidthProperty().bind(this.widthProperty());
        this.renderer.drawingHeightProperty().bind(this.heightProperty());
        this.renderer.setGraphicsContext(this.getGraphicsContext2D());
    }

    private void handleDrag(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            this.dragStart = new Vector2D(event.getX(), event.getY());
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            this.draw();
            this.renderer.getGraphicsContext().setFill(Color.DARKOLIVEGREEN.deriveColor(1,1,1,0.5));
            this.renderer.drawDraggedRectangle(this.dragStart, new Vector2D(event.getX(), event.getY()));
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            this.renderer.getGraphicsContext().setFill(Color.DARKOLIVEGREEN.deriveColor(1,1,1,0.5));
            Rectangle rectangle = this.renderer.drawDraggedRectangle(this.dragStart, new Vector2D(event.getX(), event.getY()));
            this.owner.getGraph().addCompartment(rectangle);
            this.draw();
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
        for (BioNode node : this.owner.getGraph().getNodes()) {
            if (isClickedOnNode(event, node)) {
                BioNodeContextMenu bioNodeContextMenu = new BioNodeContextMenu(node, this.owner);
                bioNodeContextMenu.show(this.owner.getPlotPane(), event.getScreenX(), event.getScreenY());
                isNode = true;
                break;
            }
        }
        if (!isNode) {
            this.graphContextMenu.show(this.owner.getPlotPane(), event.getScreenX(), event.getScreenY());
        }
    }

    private void handleLeftClick(MouseEvent event) {
        for (BioNode node : this.owner.getGraph().getNodes()) {
            if (isClickedOnNode(event, node)) {
                ChemicalEntity species = this.renderer.getBioRenderingOptions().getNodeHighlightEntity();
                node.setConcentration(species, this.owner.getConcentrationSlider().getValue());
                draw();
                break;
            }
        }
    }

    private boolean isClickedOnNode(MouseEvent event, BioNode node) {
        return node.getPosition().isNearVector(new Vector2D(event.getX() + this.renderer.getRenderingOptions().getNodeDiameter() / 2,
                        event.getY() + this.renderer.getRenderingOptions().getNodeDiameter() / 2),
                this.renderer.getRenderingOptions().getNodeDiameter() / 2);
    }

    public BioGraphRenderer getRenderer() {
        return this.renderer;
    }

    public void setRenderer(BioGraphRenderer renderer) {
        this.renderer = renderer;
    }

    public void draw() {
        this.renderer.render(this.owner.getGraph());
    }

    public void resetGraphContextMenu() {
        this.graphContextMenu = new BioGraphContextMenu(this.owner.getSimulation(), this);
    }


}
