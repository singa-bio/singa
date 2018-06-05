package de.bioforscher.singa.simulation.renderer;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.javafx.renderer.colors.ColorScale;
import de.bioforscher.singa.simulation.model.graphs.AutomatonEdge;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import javafx.scene.paint.Color;

public class BioGraphRenderOptions {

    private ChemicalEntity nodeHighlightEntity;
    private ChemicalEntity edgeHighlightEntity;

    private RenderingMode renderingMode;
    private ColorScale nodeColorScale;

    public BioGraphRenderOptions() {
        // todo render depending on maximal value in any node
        nodeColorScale = new ColorScale.Builder(5.0E-15, 10.0E-15).build();
        renderingMode = RenderingMode.STATE_BASED;
    }

    public RenderingMode getRenderingMode() {
        return renderingMode;
    }

    public void setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
    }

    public ColorScale getNodeColorScale() {
        return nodeColorScale;
    }

    public void setNodeColorScale(ColorScale nodeColorScale) {
        this.nodeColorScale = nodeColorScale;
    }

    public ChemicalEntity getNodeHighlightEntity() {
        return nodeHighlightEntity;
    }

    public void setNodeHighlightEntity(ChemicalEntity species) {
        nodeHighlightEntity = species;
    }

    public ChemicalEntity getEdgeHighlightEntity() {
        return edgeHighlightEntity;
    }

    public void setEdgeHighlightEntity(ChemicalEntity edgeHighlightEntity) {
        this.edgeHighlightEntity = edgeHighlightEntity;
    }

    public Color getColorForUpdatable(Updatable node) {
        CellSubsection firstSubsection = node.getCellRegion().getSubsections().iterator().next();
        if (nodeHighlightEntity != null) {
            double concentration = node.getConcentration(firstSubsection, nodeHighlightEntity).getValue().doubleValue();
            return nodeColorScale.getColor(concentration);
        } else {
            return Color.GRAY;
        }
    }

    public Color getEdgeColor(AutomatonEdge edge) {
        return Color.LIGHTGREY;
    }

}
