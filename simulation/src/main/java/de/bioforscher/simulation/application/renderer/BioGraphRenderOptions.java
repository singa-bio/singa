package de.bioforscher.simulation.application.renderer;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.javafx.renderer.colors.ColorScale;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import javafx.scene.paint.Color;

public class BioGraphRenderOptions {

    private ChemicalEntity nodeHighlightEntity;
    private ChemicalEntity edgeHighlightEntity;

    private boolean coloringByEntity = true;
    private ColorScale nodeColorScale;

    public BioGraphRenderOptions() {
        this.nodeColorScale = new ColorScale.Builder(0, 1).build();
    }

    public ChemicalEntity getNodeHighlightEntity() {
        return this.nodeHighlightEntity;
    }

    public void setNodeHighlightEntity(ChemicalEntity species) {
        this.nodeHighlightEntity = species;
    }

    public ChemicalEntity getEdgeHighlightEntity() {
        return this.edgeHighlightEntity;
    }

    public void setEdgeHighlightEntity(ChemicalEntity edgeHighlightEntity) {
        this.edgeHighlightEntity = edgeHighlightEntity;
    }

    public boolean isColoringByEntity() {
        return this.coloringByEntity;
    }

    public void setColoringByEntity(boolean coloringByEntity) {
        this.coloringByEntity = coloringByEntity;
    }

    public Color getNodeColor(BioNode node) {
        if (this.nodeHighlightEntity != null) {
            double concentration = node.getConcentration(this.nodeHighlightEntity).getValue().doubleValue();
            return this.nodeColorScale.getColor(concentration);
        } else {
            return Color.GRAY;
        }

    }

    public Color getEdgeColor(BioEdge edge) {
        // TODO currently not implemented
        // if (this.edgeHighlightEntity != null) {
        //     double opacity = edge.getPermeability(this.edgeHighlightEntity);
        //     return new Color(0, 0, 0, opacity);
        // } else {
            return Color.LIGHTGREY;
        // }

    }


}
