package bio.singa.javafx.renderer.molecules;

import bio.singa.chemistry.model.MoleculeAtom;
import bio.singa.chemistry.model.MoleculeBond;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.javafx.renderer.SwingRenderer;
import bio.singa.javafx.renderer.graphs.GraphRenderOptions;
import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.structure.io.plip.Interaction;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fk
 */
public class SwingMoleculeGraphRenderer implements SwingRenderer {

    private final Graphics2D context;
    private final int width;
    private final int height;

    private Map<Integer, List<Interaction>> interactionMap;

    private MoleculeRendererOptions moleculeOptions;
    private GraphRenderOptions renderingOptions;
    private MoleculeGraph currentGraph;

    public SwingMoleculeGraphRenderer(Graphics2D context, int width, int height) {
        this.context = context;
        this.width = width;
        this.height = height;
        moleculeOptions = new MoleculeRendererOptions();
        renderingOptions = new GraphRenderOptions();
        interactionMap = new HashMap<>();
    }

    public GraphRenderOptions getRenderingOptions() {
        return renderingOptions;
    }

    public void setRenderingOptions(GraphRenderOptions renderingOptions) {
        this.renderingOptions = renderingOptions;
    }

    public MoleculeRendererOptions getMoleculeOptions() {
        return moleculeOptions;
    }

    public void setMoleculeOptions(MoleculeRendererOptions moleculeOptions) {
        this.moleculeOptions = moleculeOptions;
    }

    public void addInteraction(int atom, Interaction interaction) {
        if (!interactionMap.containsKey(atom)) {
            interactionMap.put(atom, new ArrayList<>());
        }
        interactionMap.get(atom).add(interaction);
    }

    public void render(MoleculeGraph graph) {
        currentGraph = graph;
        // set line width of bonds
        for (MoleculeBond bond : currentGraph.getEdges()) {
            drawBond(bond);
        }
        for (MoleculeAtom atom : currentGraph.getNodes()) {
            drawAtom(atom);
        }
        for (Map.Entry<Integer, List<Interaction>> entry : interactionMap.entrySet()) {
            drawInteraction(entry.getKey(), entry.getValue());
        }
    }

    public void drawAtom(MoleculeAtom atom) {
        // draw node
        getGraphicsContext().setPaint(toAWTColor(moleculeOptions.getColorForElement(atom.getElement().getSymbol())));
        fillPoint(atom.getPosition(), renderingOptions.getNodeDiameter());
        // draw outline
        getGraphicsContext().setStroke(new BasicStroke((float) renderingOptions.getNodeOutlineThickness()));
        getGraphicsContext().setColor(toAWTColor(renderingOptions.getNodeOutlineColor()));
        strokeCircle(atom.getPosition(), renderingOptions.getNodeDiameter());
        // draw text
        getGraphicsContext().setPaint(toAWTColor(renderingOptions.getIdentifierTextColor()));
        strokeTextCenteredOnPoint(atom.getElement().toString() + "." + atom.getIdentifier(), atom.getPosition());
    }

    public void drawBond(MoleculeBond bond) {
        SimpleLineSegment connectingSegment = new SimpleLineSegment(bond.getSource().getPosition(), bond.getTarget().getPosition());
        getGraphicsContext().setStroke(new BasicStroke((float) renderingOptions.getEdgeThickness()));
        getGraphicsContext().setColor(toAWTColor(renderingOptions.getEdgeColor()));
        switch (bond.getType()) {
            case DOUBLE_BOND: {
                // draw upper parallel
                SimpleLineSegment upperParallelSegment = connectingSegment.getParallelSegment((renderingOptions.getNodeDiameter() / 2.0) * 0.5);
                strokeLineSegment(upperParallelSegment);
                // draw lower parallel
                SimpleLineSegment lowerParallelSegment = connectingSegment.getParallelSegment((-renderingOptions.getNodeDiameter() / 2.0) * 0.5);
                strokeLineSegment(lowerParallelSegment);
                break;
            }
            case AROMATIC_BOND: {
                // draw upper parallel
                SimpleLineSegment upperParallelSegment = connectingSegment.getParallelSegment((renderingOptions.getNodeDiameter() / 2.0) * 0.5);
                strokeLineSegment(upperParallelSegment);
                // draw lower parallel
                SimpleLineSegment lowerParallelSegment = connectingSegment.getParallelSegment((renderingOptions.getNodeDiameter() / 2.0) * 0.5);
                dashLineSegment(lowerParallelSegment, 2d, 4d);
                break;
            }
            default:
                // draw single bond
                strokeLineSegment(connectingSegment);
                break;
        }
    }

    public void drawInteraction(int atom, List<Interaction> interactions) {

        //determine minimal radius
        double minimalRadius = renderingOptions.getNodeDiameter() + 4.0;
        int minimalInteractionCount = interactionMap.values().stream()
                .mapToInt(List::size)
                .min().orElse(-1);
        int maximalInteractionCount = interactionMap.values().stream()
                .mapToInt(List::size)
                .max().orElse(-1);

        MoleculeAtom node = currentGraph.getNode(atom);
        switch (node.getNeighbours().size()) {
            case 1: {
                Vector2D first = node.getNeighbours().iterator().next().getPosition();
                Vector2D current = node.getPosition();
                Line line = new Line(first, current);
                double perpendicularSlope = line.getPerpendicularSlope();
                Line perpendicular = new Line(current, perpendicularSlope);
                strokeCircle(perpendicular.mirrorVector(first), 4);
                break;
            }
            case 2: {
                Vector2D first = node.getNeighbours().get(0).getPosition();
                Vector2D second = node.getNeighbours().get(1).getPosition();
                Vector2D current = node.getPosition();
                Vector2D midpoint = first.getMidpointTo(second);
                Line line = new Line(current, midpoint);
                double perpendicularSlope = line.getPerpendicularSlope();
                Line perpendicular = new Line(current, perpendicularSlope);
                strokeCircle(perpendicular.mirrorVector(midpoint), 4);
                break;
            }
            default:
                strokeCircle(node.getPosition(), renderingOptions.getNodeDiameter() + 3);
        }
    }

    @Override
    public Graphics2D getGraphicsContext() {
        return context;
    }

    @Override
    public double getDrawingWidth() {
        return width;
    }

    @Override
    public double getDrawingHeight() {
        return height;
    }
}
