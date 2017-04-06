package de.bioforscher.singa.simulation.application.components.panes;

import javafx.scene.layout.AnchorPane;

/**
 * @author cl
 */
public class ResizablePane extends AnchorPane {

    private SimulationCanvas canvas;

    public ResizablePane(SimulationCanvas canvas) {
        this.canvas = canvas;
        getChildren().add(canvas);
        canvas.setManaged(false);
    }

    @Override
    public void resize(double width,double height) {
        super.resize(width, height);
        this.canvas.setWidth(width);
        this.canvas.setHeight(height);
    }

}
