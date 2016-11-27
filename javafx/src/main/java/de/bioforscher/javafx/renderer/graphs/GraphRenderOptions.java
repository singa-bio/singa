package de.bioforscher.javafx.renderer.graphs;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * The default rendering options for any graph. Supported options are:
 * <ul>
 * <li> the node diameter (default = 15.0)
 * <li> the node color (default = {@link Color#SEAGREEN})
 * <li> displaying nodes (default = true)
 * <li> the edge thickness (default = 3.0)
 * <li> the edge color (default = {@link Color#DIMGRAY})
 * <li> displaying edges (default = true)
 * <li> identifer text color (default = {@link Color#SLATEGRAY})
 * <li> identifer text font (default = system default)
 * <li> displaying identifer text (default = false)
 * <li> background color (default = {@link Color#WHITE})
 * </ul>
 *
 */
public class GraphRenderOptions {

    private double nodeDiameter = 15;
    private Color nodeColor = Color.SEAGREEN;
    private boolean displayingNodes = true;

    private Color edgeColor = Color.DIMGRAY;
    private double edgeThickness = 3;
    private boolean displayingEdges = true;

    private Color identifierTextColor = Color.WHITESMOKE;
    // private Font identifierFont;
    private boolean displayingIdentifierText = false;

    private Color backgroundColor = Color.WHITE;

    public double getNodeDiameter() {
        return this.nodeDiameter;
    }

    public void setNodeDiameter(double nodeDiameter) {
        this.nodeDiameter = nodeDiameter;
    }

    public Color getNodeColor() {
        return this.nodeColor;
    }

    public void setNodeColor(Color nodeColor) {
        this.nodeColor = nodeColor;
    }

    public boolean isDisplayingNodes() {
        return this.displayingNodes;
    }

    public void setDisplayingNodes(boolean displayingNodes) {
        this.displayingNodes = displayingNodes;
    }

    public Color getEdgeColor() {
        return this.edgeColor;
    }

    public void setEdgeColor(Color edgeColor) {
        this.edgeColor = edgeColor;
    }

    public double getEdgeThickness() {
        return this.edgeThickness;
    }

    public void setEdgeThickness(double edgeThickness) {
        this.edgeThickness = edgeThickness;
    }

    public boolean isDisplayingEdges() {
        return this.displayingEdges;
    }

    public void setDisplayingEdges(boolean displayingEdges) {
        this.displayingEdges = displayingEdges;
    }

    public Color getIdentifierTextColor() {
        return this.identifierTextColor;
    }

    public void setIdentifierTextColor(Color identifierTextColor) {
        this.identifierTextColor = identifierTextColor;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isDisplayingIdentifierText() {
        return this.displayingIdentifierText;
    }

    public void setDisplayingIdentifierText(boolean displayingIdentifierText) {
        this.displayingIdentifierText = displayingIdentifierText;
    }

}
