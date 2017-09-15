package de.bioforscher.singa.javafx.renderer.graphs;

import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.mathematics.graphs.model.GenericEdge;
import de.bioforscher.singa.mathematics.graphs.model.GenericGraph;
import de.bioforscher.singa.mathematics.graphs.model.GenericNode;

/**
 * @author cl
 */
public class LeafShellRenderer extends GraphRenderer<GenericNode<LeafSubstructure<?,?>>, GenericEdge<LeafSubstructure<?,?>>, Integer, GenericGraph<LeafSubstructure<?,?>>> {

    @Override
    protected void drawNode(GenericNode<LeafSubstructure<?, ?>> node) {
        // set color and diameter
        getGraphicsContext().setFill(getRenderingOptions().getNodeColor());
        drawPoint(node.getPosition(), getRenderingOptions().getNodeDiameter());
        // draw text
        getGraphicsContext().setFill(getRenderingOptions().getIdentifierTextColor());
        drawTextCenteredOnPoint(String.valueOf(node.getContent().getIdentifier().getSerial()), node.getPosition());
    }
}
