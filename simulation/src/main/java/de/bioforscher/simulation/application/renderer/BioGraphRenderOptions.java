package de.bioforscher.simulation.application.renderer;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import javafx.scene.paint.Color;

public class BioGraphRenderOptions {

    private ChemicalEntity nodeHighlightSpecies;
    private ChemicalEntity edgeHighlightSpecies;

    private ColorScale nodeColorScale;

    public BioGraphRenderOptions() {
        // this.nodeColorScale = new MultiColorScale(0, 1, Color.RED,
        // Color.YELLOW, Color.GREEN);
        this.nodeColorScale = new ColorScale.Builder(0, 1).build();

    }

    public ChemicalEntity getNodeHighlightSpecies() {
        return this.nodeHighlightSpecies;
    }

    public void setNodeHighlightSpecies(ChemicalEntity species) {
        this.nodeHighlightSpecies = species;
    }

    public ChemicalEntity getEdgeHighlightSpecies() {
        return this.edgeHighlightSpecies;
    }

    public void setEdgeHighlightSpecies(ChemicalEntity edgeHighlightSpecies) {
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
        // TODO currently not implemented
        // if (this.edgeHighlightSpecies != null) {
        //     double opacity = edge.getPermeability(this.edgeHighlightSpecies);
        //     return new Color(0, 0, 0, opacity);
        // } else {
            return Color.LIGHTGREY;
        // }

    }

}
