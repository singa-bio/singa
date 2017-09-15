package de.bioforscher.singa.javafx.renderer.molecules;

import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeAtom;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeBond;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeBondType;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeGraph;
import de.bioforscher.singa.javafx.renderer.graphs.GraphRenderOptions;
import de.bioforscher.singa.javafx.renderer.graphs.GraphRenderer;
import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;

public class MoleculeGraphRenderer extends GraphRenderer<MoleculeAtom, MoleculeBond, Integer, MoleculeGraph> {

    private MoleculeRendererOptions moleculeOptions = new MoleculeRendererOptions();

    public MoleculeGraphRenderer() {
        GraphRenderOptions options = new GraphRenderOptions();
        options.setNodeDiameter(22);
        this.setRenderingOptions(options);
    }

    @Override
    protected void drawNode(MoleculeAtom atom) {
        // draw node
        getGraphicsContext().setFill(this.moleculeOptions.getColorForElement(atom.getElement().getSymbol()));
        drawPoint(atom.getPosition(), getRenderingOptions().getNodeDiameter());
        // draw outline
        getGraphicsContext().setStroke(getRenderingOptions().getEdgeColor());
        circlePoint(atom.getPosition(), getRenderingOptions().getNodeDiameter());
        // draw text
        getGraphicsContext().setFill(getRenderingOptions().getIdentifierTextColor());
        drawTextCenteredOnPoint(atom.getElement().toString() + "." + atom.getIdentifier(), atom.getPosition());
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
        LineSegment connectingSegment = new LineSegment(bond.getSource().getPosition(), bond.getTarget().getPosition());
        if (bond.getType() == MoleculeBondType.DOUBLE_BOND) {
            // draw upper parallel
            LineSegment upperParallelSegment = connectingSegment.getParallelSegment((getRenderingOptions().getNodeDiameter() / 2.0) * 0.5);
            drawLineSegment(upperParallelSegment);
            // draw lower parallel
            LineSegment lowerParallelSegment = connectingSegment.getParallelSegment((-getRenderingOptions().getNodeDiameter() / 2.0) * 0.5);
            drawLineSegment(lowerParallelSegment);
        } else if (bond.getType() == MoleculeBondType.AROMATIC_BOND) {
            // draw upper parallel
            LineSegment upperParallelSegment = connectingSegment.getParallelSegment((getRenderingOptions().getNodeDiameter() / 2.0) * 0.5);
            drawLineSegment(upperParallelSegment);
            // draw lower parallel
            LineSegment lowerParallelSegment = connectingSegment.getParallelSegment((-getRenderingOptions().getNodeDiameter() / 2.0) * 0.5);
            dashLineSegment(lowerParallelSegment, 2d, 4d);
        } else {
            // draw single bond
            drawLineSegment(connectingSegment);
        }


    }

}
