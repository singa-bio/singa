package bio.singa.simulation.renderer;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.javafx.renderer.colors.ColorScale;
import bio.singa.simulation.model.graphs.AutomatonEdge;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Updatable;
import javafx.scene.paint.Color;

public class BioGraphRenderOptions {

    private static final double DEFAULT_MINIMAL_CONCENTRATION = 0.0;
    private static final double DEFAULT_MAXIMAL_CONCENTRATION = 1.0;

    private ChemicalEntity nodeHighlightEntity;
    private RenderingMode renderingMode;
    private ColorScale nodeColorScale;

    public BioGraphRenderOptions() {
        nodeColorScale = new ColorScale.Builder(DEFAULT_MINIMAL_CONCENTRATION, DEFAULT_MAXIMAL_CONCENTRATION).build();
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

    public Color getColorForUpdatable(Updatable updatable) {
        CellSubsection firstSubsection = updatable.getCellRegion().getSubsections().iterator().next();
        if (nodeHighlightEntity != null) {
            double concentration = updatable.getConcentrationContainer().get(firstSubsection, nodeHighlightEntity).getValue().doubleValue();
            return nodeColorScale.getColor(concentration);
        } else {
            return Color.GRAY;
        }
    }

    public Color getEdgeColor(AutomatonEdge edge) {
        return Color.LIGHTGREY;
    }

}
