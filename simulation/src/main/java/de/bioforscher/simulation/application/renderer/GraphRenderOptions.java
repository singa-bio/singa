package de.bioforscher.simulation.application.renderer;

import javafx.scene.paint.Color;

public class GraphRenderOptions {

    private double standardNodeDiameter;
    private Color standardNodeColor;
    private boolean renderNodes;

    private Color standardEdgeColor;
    private double standerdEdgeWidth;
    private boolean renderEdges;

    private double voronoiEdgeWidth;
    private Color voronoiEdgeColor;
    private boolean renderVoronoi;

    private Color backgroundColor;

    public GraphRenderOptions() {

        this.renderNodes = true;
        this.standardNodeColor = Color.GREEN;
        this.standardNodeDiameter = 10.0;

        this.renderEdges = true;
        this.standardEdgeColor = Color.BLACK;
        this.standerdEdgeWidth = 3.0;

        this.renderVoronoi = true;
        this.voronoiEdgeColor = Color.LIGHTGREEN;
        this.voronoiEdgeWidth = 2.0;

        this.backgroundColor = Color.WHITE;

    }

    public double getStandardNodeDiameter() {
        return standardNodeDiameter;
    }

    public void setStandardNodeDiameter(double standardNodeDiameter) {
        this.standardNodeDiameter = standardNodeDiameter;
    }

    public Color getStandardNodeColor() {
        return standardNodeColor;
    }

    public void setStandardNodeColor(Color standardNodeColor) {
        this.standardNodeColor = standardNodeColor;
    }

    public boolean isRenderNodes() {
        return renderNodes;
    }

    public void setRenderNodes(boolean renderNodes) {
        this.renderNodes = renderNodes;
    }

    public Color getStandardEdgeColor() {
        return standardEdgeColor;
    }

    public void setStandardEdgeColor(Color standardEdgeColor) {
        this.standardEdgeColor = standardEdgeColor;
    }

    public double getStanderdEdgeWidth() {
        return standerdEdgeWidth;
    }

    public void setStanderdEdgeWidth(double standerdEdgeWidth) {
        this.standerdEdgeWidth = standerdEdgeWidth;
    }

    public boolean isRenderEdges() {
        return renderEdges;
    }

    public void setRenderEdges(boolean renderEdges) {
        this.renderEdges = renderEdges;
    }

    public double getVoronoiEdgeWidth() {
        return voronoiEdgeWidth;
    }

    public void setVoronoiEdgeWidth(double voronoiEdgeWidth) {
        this.voronoiEdgeWidth = voronoiEdgeWidth;
    }

    public Color getVoronoiEdgeColor() {
        return voronoiEdgeColor;
    }

    public void setVoronoiEdgeColor(Color voronoiEdgeColor) {
        this.voronoiEdgeColor = voronoiEdgeColor;
    }

    public boolean isRenderVoronoi() {
        return renderVoronoi;
    }

    public void setRenderVoronoi(boolean renderVoronoi) {
        this.renderVoronoi = renderVoronoi;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

}
