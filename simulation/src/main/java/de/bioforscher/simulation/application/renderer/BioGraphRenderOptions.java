package de.bioforscher.simulation.application.renderer;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import javafx.scene.paint.Color;

public class BioGraphRenderOptions {

    private Species nodeHighlightSpecies;
    private Species edgeHighlightSpecies;

    private ColorScale nodeColorScale;

    public BioGraphRenderOptions() {
        // this.nodeColorScale = new MultiColorScale(0, 1, Color.RED,
        // Color.YELLOW, Color.GREEN);
        this.nodeColorScale = new ColorScale.Builder(0, 1).build();

    }

    public Species getNodeHighlightSpecies() {
        return this.nodeHighlightSpecies;
    }

    public void setNodeHighlightSpecies(Species species) {
        this.nodeHighlightSpecies = species;
    }

    public Species getEdgeHighlightSpecies() {
        return this.edgeHighlightSpecies;
    }

    public void setEdgeHighlightSpecies(Species edgeHighlightSpecies) {
        this.edgeHighlightSpecies = edgeHighlightSpecies;
    }

    public Color getNodeColor(BioNode node) {
        if (this.nodeHighlightSpecies != null) {
            double concentration = node.getConcentration(this.nodeHighlightSpecies).getValue().doubleValue();
            return this.nodeColorScale.getColor(concentration);
        } else {
            return Color.GRAY;
        }

    }

    public Color getEdgeColor(BioEdge edge) {
        if (this.edgeHighlightSpecies != null) {
            double opacity = edge.getPermeability(this.edgeHighlightSpecies);
            ;
            return new Color(0, 0, 0, opacity);
        } else {
            return Color.LIGHTGREY;
        }

    }

}
