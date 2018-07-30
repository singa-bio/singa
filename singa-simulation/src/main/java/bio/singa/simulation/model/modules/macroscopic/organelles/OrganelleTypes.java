package bio.singa.simulation.model.modules.macroscopic.organelles;

import bio.singa.features.identifiers.GoTerm;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.parser.organelles.OrganelleImageParser;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Map;

/**
 * @author cl
 */
public enum OrganelleTypes {

    EARLY_ENDOSOME("early endosome", new GoTerm("GO:0005769"),
            Constants.earlyEndosomeLumen,
            Constants.earlyEndosomeMembrane,
            Constants.cytoplasm,
            "early_endosome"),

    NUCLEUS("nucleus", new GoTerm("GO:0005634"),
            Constants.nucleoplasm,
            Constants.nuclearMembrane,
            Constants.cytoplasm,
            "nuclear_envelope"),

    CELL("cell", new GoTerm("GO:0005623"),
            Constants.cytoplasm,
            Constants.cellOuterMembrane,
            Constants.extracellularRegion,
            "cell_membrane");

    private CellRegion internalRegion;
    private CellRegion membraneRegion;
    private String templateLocation;
    private Polygon polygon;
    private Quantity<Length> scale;

    OrganelleTypes(String name, GoTerm goTerm, CellSubsection innerSubsection, CellSubsection membraneSubsection, CellSubsection outerSubsection, String templateName) {
        // region for inner nodes
        internalRegion = new CellRegion(name, goTerm);
        internalRegion.addSubSection(CellTopology.INNER, innerSubsection);
        // region for membrane associated nodes
        membraneRegion = new CellRegion(membraneSubsection.getIdentifier(), membraneSubsection.getGoTerm());
        membraneRegion.addSubSection(CellTopology.INNER, innerSubsection);
        membraneRegion.addSubSection(CellTopology.MEMBRANE, membraneSubsection);
        membraneRegion.addSubSection(CellTopology.OUTER, outerSubsection);
        // location for organelle images
        templateLocation = "organelle_templates/" + templateName + ".png";
    }

    public CellRegion getInternalRegion() {
        return internalRegion;
    }

    public CellRegion getMembraneRegion() {
        return membraneRegion;
    }

    public Organelle create() {
        if (polygon == null) {
            Map.Entry<Polygon, Quantity<Length>> entry = OrganelleImageParser.getPolygonTemplate(templateLocation);
            polygon = entry.getKey();
            // resize to a handable number of edges
            while (polygon.getVertices().size() > 200) {
                polygon.reduce(1);
            }
            scale = entry.getValue();
        }
        return new Organelle(internalRegion, membraneRegion, polygon, scale);
    }

    // TODO extract to its own enum
    public static class Constants {
        private static final CellSubsection cytoplasm = new CellSubsection("cytoplasm", new GoTerm("GO:0005737"));
        private static final CellSubsection cellOuterMembrane = new CellSubsection("cell outer membrane", new GoTerm("GO:0009279"));
        private static final CellSubsection nucleoplasm = new CellSubsection("nucleoplasm", new GoTerm("GO:0005654"));
        private static final CellSubsection nuclearMembrane = new CellSubsection("nuclear membrane", new GoTerm("GO:0031965"));
        private static final CellSubsection earlyEndosomeLumen = new CellSubsection("early endosome lumen", new GoTerm("GO:0031905"));
        private static final CellSubsection earlyEndosomeMembrane = new CellSubsection("early endosome membrane", new GoTerm("GO:0031901"));
        private static final CellSubsection extracellularRegion = new CellSubsection("extracellular region", new GoTerm("GO:0005576"));
        public  static final CellRegion extracellularRegionRegion = new CellRegion(extracellularRegion.getIdentifier(), extracellularRegion.getGoTerm());
        static {
            extracellularRegionRegion.addSubSection(CellTopology.INNER, extracellularRegion);
        }
    }
}
