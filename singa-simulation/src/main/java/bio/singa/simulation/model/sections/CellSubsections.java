package bio.singa.simulation.model.sections;

import bio.singa.features.identifiers.GoTerm;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

import static bio.singa.features.identifiers.GoTerm.*;

/**
 * @author cl
 */
public class CellSubsections {

    // http://colorbrewer2.org/?type=diverging&scheme=PRGn&n=7#type=qualitative&scheme=Set3&n=7
    private static final Color lightBlue = color(141,211,199);
    private static final Color yellow = color(255,255,179);
    private static final Color violett = color(190,186,218);
    private static final Color red = color(251,128,114);
    private static final Color darkBlue = color(128,177,211);
    private static final Color orange = color(253,180,98);
    private static final Color green = color(179,222,105);

    private static final Map<GoTerm, Color> colorMap = new HashMap<>();

    public static final CellSubsection CYTOPLASM =  addSection(new CellSubsection("cytoplasm", new GoTerm("GO:0005737", GOA_DATABASE)), yellow);
    public static final CellSubsection CELL_OUTER_MEMBRANE = addSection(new CellSubsection("cell outer membrane", new GoTerm("GO:0009279", GOA_DATABASE)), yellow);
    public static final CellSubsection NUCLEOPLASM = addSection(new CellSubsection("nucleoplasm", new GoTerm("GO:0005654", GOA_DATABASE)), violett);
    public static final CellSubsection NUCLEAR_MEMBRANE = addSection(new CellSubsection("nuclear membrane", new GoTerm("GO:0031965", GOA_DATABASE)), violett);
    public static final CellSubsection EARLY_ENDOSOME_LUMEN = addSection(new CellSubsection("early endosome lumen", new GoTerm("GO:0031905", GOA_DATABASE)), orange);
    public static final CellSubsection EARLY_ENDOSOME_MEMBRANE = addSection(new CellSubsection("early endosome membrane", new GoTerm("GO:0031901", GOA_DATABASE)), orange);
    public static final CellSubsection EXTRACELLULAR_REGION = addSection(new CellSubsection("extracellular region", new GoTerm("GO:0005576", GOA_DATABASE)), lightBlue);

    private static CellSubsection addSection(CellSubsection subsection, Color color) {
        colorMap.put(subsection.getGoTerm(), color);
        return subsection;
    }

    public static Color getColor(GoTerm goTerm) {
        return colorMap.getOrDefault(goTerm, Color.LIGHTGRAY);
    }

    public static Color getColor(CellSubsection subsection) {
        return getColor(subsection.getGoTerm());
    }

    public static Color getColor(CellRegion cellRegion) {
        return getColor(cellRegion.getGoTerm());
    }

    private static Color color(double r, double g, double b) {
        return Color.color(r / 256.0, g / 256.0, b / 256.0);
    }

}
