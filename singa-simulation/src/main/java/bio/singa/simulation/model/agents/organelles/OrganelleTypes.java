package bio.singa.simulation.model.agents.organelles;

import bio.singa.features.identifiers.GoTerm;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellSubsections;
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
            CellSubsections.EARLY_ENDOSOME_LUMEN,
            CellSubsections.EARLY_ENDOSOME_MEMBRANE,
            CellSubsections.CYTOPLASM,
            "early_endosome"),

    NUCLEUS("nucleus", new GoTerm("GO:0005634"),
            CellSubsections.NUCLEOPLASM,
            CellSubsections.NUCLEAR_MEMBRANE,
            CellSubsections.CYTOPLASM,
            "nuclear_envelope"),

    CELL("cell", new GoTerm("GO:0005623"),
            CellSubsections.CYTOPLASM,
            CellSubsections.CELL_OUTER_MEMBRANE,
            CellSubsections.EXTRACELLULAR_REGION,
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

}
