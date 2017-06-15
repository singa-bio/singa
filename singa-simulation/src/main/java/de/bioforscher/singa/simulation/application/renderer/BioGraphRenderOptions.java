package de.bioforscher.singa.simulation.application.renderer;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.javafx.renderer.colors.ColorScale;
import de.bioforscher.singa.simulation.model.graphs.BioEdge;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import javafx.scene.paint.Color;

public class BioGraphRenderOptions {

    private ChemicalEntity nodeHighlightEntity;
    private ChemicalEntity edgeHighlightEntity;

    private RenderingMode renderingMode;
    private ColorScale nodeColorScale;

    public BioGraphRenderOptions() {
        // todo render depending on maximal value in any node
        this.nodeColorScale = new ColorScale.Builder(0, 1).build();
        this.renderingMode = RenderingMode.STATE_BASED;
    }

    public RenderingMode getRenderingMode() {
        return this.renderingMode;
    }

    public void setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
    }

    public ColorScale getNodeColorScale() {
        return this.nodeColorScale;
    }

    public void setNodeColorScale(ColorScale nodeColorScale) {
        this.nodeColorScale = nodeColorScale;
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

    public Color getNodeColor(BioNode node) {
        if (this.nodeHighlightEntity != null) {
            double concentration = node.getConcentration(this.nodeHighlightEntity).getValue().doubleValue();
            return this.nodeColorScale.getColor(concentration);
        } else {
            return Color.GRAY;
        }
    }

    public Color getEdgeColor(BioEdge edge) {
            return Color.LIGHTGREY;
    }

}
