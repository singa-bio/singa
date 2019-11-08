package bio.singa.javafx.renderer.molecules;

import bio.singa.javafx.renderer.graphs.GraphRenderOptions;
import bio.singa.javafx.renderer.graphs.GraphRenderer;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.structure.model.molecules.MoleculeAtom;
import bio.singa.structure.model.molecules.MoleculeBond;
import bio.singa.structure.model.molecules.MoleculeGraph;

public class MoleculeGraphRenderer extends GraphRenderer<MoleculeAtom, MoleculeBond, Integer, MoleculeGraph> {

    private final MoleculeRendererOptions moleculeOptions = new MoleculeRendererOptions();

    public MoleculeGraphRenderer() {
        GraphRenderOptions options = new GraphRenderOptions();
        options.setNodeDiameter(22);
        setRenderingOptions(options);
    }

    @Override
    protected void drawNode(MoleculeAtom atom) {
        // draw node
        getGraphicsContext().setFill(moleculeOptions.getColorForElement(atom.getElement().getSymbol()));
        fillPoint(atom.getPosition(), getRenderingOptions().getNodeDiameter());
        // draw outline
        getGraphicsContext().setLineWidth(getRenderingOptions().getNodeOutlineThickness());
        getGraphicsContext().setStroke(getRenderingOptions().getNodeOutlineColor());
        strokeCircle(atom.getPosition(), getRenderingOptions().getNodeDiameter());
        // draw text
        getGraphicsContext().setFill(getRenderingOptions().getIdentifierTextColor());
        strokeTextCenteredOnPoint(atom.getElement().toString() + "." + atom.getIdentifier(), atom.getPosition());
    }

    @Override
    protected void drawEdge(MoleculeBond bond) {
        // set color and width
        getGraphicsContext().setLineWidth(getRenderingOptions().getEdgeThickness());
        getGraphicsContext().setStroke(getRenderingOptions().getEdgeColor());
        // draw bond
        drawBond(bond);
    }


    private void drawBond(MoleculeBond bond) {
        SimpleLineSegment connectingSegment = new SimpleLineSegment(bond.getSource().getPosition(), bond.getTarget().getPosition());
        switch (bond.getType()) {
            case DOUBLE_BOND: {
                // draw upper parallel
                SimpleLineSegment upperParallelSegment = connectingSegment.getParallelSegment((getRenderingOptions().getNodeDiameter() / 2.0) * 0.5);
                strokeLineSegment(upperParallelSegment);
                // draw lower parallel
                SimpleLineSegment lowerParallelSegment = connectingSegment.getParallelSegment((-getRenderingOptions().getNodeDiameter() / 2.0) * 0.5);
                strokeLineSegment(lowerParallelSegment);
                break;
            }
            case AROMATIC_BOND: {
                // draw upper parallel
                SimpleLineSegment upperParallelSegment = connectingSegment.getParallelSegment((getRenderingOptions().getNodeDiameter() / 2.0) * 0.5);
                strokeLineSegment(upperParallelSegment);
                // draw lower parallel
                SimpleLineSegment lowerParallelSegment = connectingSegment.getParallelSegment((-getRenderingOptions().getNodeDiameter() / 2.0) * 0.5);
                dashLineSegment(lowerParallelSegment, 2d, 4d);
                break;
            }
            default:
                // draw single bond
                strokeLineSegment(connectingSegment);
                break;
        }

    }

}
