package bio.singa.javafx.viewer;

import bio.singa.javafx.renderer.graphs.GraphRenderOptions;
import bio.singa.javafx.renderer.molecules.MoleculeRendererOptions;
import bio.singa.javafx.renderer.molecules.SwingMoleculeGraphRenderer;
import bio.singa.structure.model.molecules.MoleculeGraph;
import bio.singa.structure.parser.mol.MolParser;
import javafx.scene.paint.Color;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MolSvgWriter {

    public static final Color CARBON_COLOR = Color.rgb(0, 86, 114);
    public static final Color OXYGEN_COLOR = Color.rgb(0, 151, 198);
    public static final Color PHOSPHOROUS_COLOR = Color.rgb(18, 193, 251);
    public static final Color EDGE_COLOR = Color.rgb(51, 51, 51);
    public static final Color OUTLINE_COLOR = Color.rgb(51, 51, 51);
    public static final double EDGE_THICKNESS = 5.0;
    public static final int NODE_DIAMETER = 20;

    private static final Logger logger = LoggerFactory.getLogger(MolSvgWriter.class);

    public static void main(String[] args) throws IOException {

        Path molFilePath = Paths.get(args[0]);

        // parse structures one by one
        MolParser molParser = new MolParser(molFilePath, true);
        MoleculeGraph first = molParser.parseNextMoleculeGraph();

        MoleculeRendererOptions moleculeOptions = new MoleculeRendererOptions();
        moleculeOptions.setCarbonColor(CARBON_COLOR);
        moleculeOptions.setOxygenColor(OXYGEN_COLOR);
        moleculeOptions.setPhosphorusColor(PHOSPHOROUS_COLOR);
        GraphRenderOptions options = new GraphRenderOptions();
        options.setEdgeColor(EDGE_COLOR);
        options.setNodeOutlineColor(OUTLINE_COLOR);
        options.setEdgeThickness(EDGE_THICKNESS);
        options.setNodeDiameter(NODE_DIAMETER);

        // display
        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(500, 500);
        SwingMoleculeGraphRenderer swingMoleculeGraphRenderer = new SwingMoleculeGraphRenderer(svgGraphics2D, 500, 500);
        swingMoleculeGraphRenderer.setMoleculeOptions(moleculeOptions);
        swingMoleculeGraphRenderer.setRenderingOptions(options);
        swingMoleculeGraphRenderer.render(first);
        String svgElement = svgGraphics2D.getSVGElement();
        try {
            Path svgPath = Paths.get(args[1]);
            Files.createDirectories(svgPath.getParent());
            SVGUtils.writeToSVG(svgPath.toFile(), svgElement);
        } catch (IOException e) {
            logger.error("failed to write SVG for {}", moleculeOptions, e);
        }
    }
}
