package bio.singa.javafx.renderer.layouts.force;

import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.beans.binding.DoubleBinding;

/**
 *
 * @author cl
 */
public abstract class Force<NodeType extends Node<NodeType, Vector2D, ?>> {

    private final ForceDirectedGraphLayout<NodeType, ?, ?, ?> parentLayout;
    private DoubleBinding forceConstant;

    public Force(ForceDirectedGraphLayout<NodeType, ?, ?, ?> parentLayout) {
        this.parentLayout = parentLayout;
        forceConstant = parentLayout.forceConstantProperty();
    }

    public ForceDirectedGraphLayout<NodeType, ?, ?, ?> getParentLayout() {
        return parentLayout;
    }

    public Number getForceConstant() {
        return forceConstant.get();
    }

    public DoubleBinding forceConstantProperty() {
        return forceConstant;
    }

    public abstract void apply(Graph<NodeType, ?, ?> graph);

}
