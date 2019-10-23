package bio.singa.javafx.renderer.layouts.force;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.beans.binding.DoubleBinding;

/**
 *
 * @author cl
 */
public abstract class Force<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

    private final ForceDirectedGraphLayout<NodeType, EdgeType, IdentifierType, GraphType> parentLayout;
    private DoubleBinding forceConstant;
    private double forceMultiplier;

    public Force(ForceDirectedGraphLayout<NodeType, EdgeType, IdentifierType, GraphType> parentLayout) {
        this.parentLayout = parentLayout;
        forceConstant = parentLayout.forceConstantProperty();
        forceMultiplier = 1;
    }

    public ForceDirectedGraphLayout<NodeType, EdgeType, IdentifierType, GraphType> getParentLayout() {
        return parentLayout;
    }

    public Number getForceConstant() {
        return forceConstant.get();
    }

    public DoubleBinding setForceConstant(DoubleBinding forceConstant) {
        return this.forceConstant = forceConstant;
    }

    public DoubleBinding forceConstantProperty() {
        return forceConstant;
    }

    public double getForceMultiplier() {
        return forceMultiplier;
    }

    public void setForceMultiplier(double forceMultiplier) {
        this.forceMultiplier = forceMultiplier;
    }

    public abstract void apply(GraphType graph);

}
