package de.bioforscher.simulation.application.components;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.application.BioGraphConsumer;
import de.bioforscher.simulation.application.BioGraphProducer;
import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.application.Jobs;
import de.bioforscher.simulation.application.renderer.GraphRenderer;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioNode;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SimulationCanvas extends Canvas {

    private BioGraphSimulation owner;

    private ConcurrentLinkedQueue<AutomatonGraph> graphQueue = new ConcurrentLinkedQueue<>();
    private GraphRenderer renderer;

    private BioGraphContextMenu graphContextMenu;

    public SimulationCanvas(BioGraphSimulation owner) {
        super(SimulationSpace.getInstance().getWidth(), SimulationSpace.getInstance().getHeight());
        this.owner = owner;
        this.renderer = new GraphRenderer(this);
        this.graphContextMenu = new BioGraphContextMenu(this.owner.getSimulation(), this);
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleClick);
    }

    public void arrangeGraph(ActionEvent event) {
        Thread graphProducer = new Thread(new BioGraphProducer(this.graphQueue, this.owner.getGraph(), this.getWidth(),
                this.getHeight(), Jobs.ARRANGE, 100));
        graphProducer.start();
        BioGraphConsumer graphConsumer = new BioGraphConsumer(this.graphQueue, this.renderer);
        graphConsumer.start();
    }

    public void startSimulation() {
        Thread p = new Thread(new BioGraphProducer(this.graphQueue, this.owner.getGraph(), this.owner.getSimulation(),
                this.getWidth(), this.getHeight(), Jobs.SIMULATE));
        p.setDaemon(true);
        p.start();
        BioGraphConsumer graphConsumer = new BioGraphConsumer(this.graphQueue, this.renderer);
        graphConsumer.start();
    }

    public void handleClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.SECONDARY)) {
            handleRightClick(event);
        } else if (event.getButton().equals(MouseButton.PRIMARY)) {
            handleLeftClick(event);
        }
    }

    public void handleRightClick(MouseEvent event) {
        boolean isNode = false;
        for (BioNode node : this.owner.getGraph().getNodes()) {
            if (isClickedOnNode(event, node)) {
                BioNodeContextMenu bioNodeContextMenu = new BioNodeContextMenu(node, this.owner);
                bioNodeContextMenu.show(this.owner.getContextAnchor(), event.getScreenX(), event.getScreenY());
                isNode = true;
                break;
            }
        }
        if (!isNode) {
            this.graphContextMenu.show(this.owner.getContextAnchor(), event.getScreenX(), event.getScreenY());
        }
    }

    public void handleLeftClick(MouseEvent event) {
        for (BioNode node : this.owner.getGraph().getNodes()) {
            if (isClickedOnNode(event, node)) {
                ChemicalEntity species = this.renderer.getBioRenderingOptions().getNodeHighlightSpecies();
                node.setConcentration(species, this.owner.getConcentrationSlider().getValue());
                draw();
                break;
            }
        }
    }

    public boolean isClickedOnNode(MouseEvent event, BioNode node) {
        return node.getPosition().isNearVector(new Vector2D(event.getX(), event.getY()),
                this.renderer.getOptions().getStandardNodeDiameter() / 2);
    }

    public BioGraphContextMenu getGraphContextMenu() {
        return this.graphContextMenu;
    }

    public void setGraphContextMenu(BioGraphContextMenu graphContextMenu) {
        this.graphContextMenu = graphContextMenu;
    }

    public GraphRenderer getRenderer() {
        return this.renderer;
    }

    public void setRenderer(GraphRenderer renderer) {
        this.renderer = renderer;
    }

    public void draw() {
        double width = getWidth();
        double height = getHeight();
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        this.renderer.drawBio(this.owner.getGraph());
    }

    public void resetGraphContextMenu() {
        this.graphContextMenu = new BioGraphContextMenu(this.owner.getSimulation(), this);
    }

}
