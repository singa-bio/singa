package bio.singa.simulation.model.agents.organelles;

import bio.singa.features.identifiers.GoTerm;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellSubsections;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.parser.organelles.OrganelleImageParser;
import bio.singa.simulation.parser.organelles.OrganelleTemplate;

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
            "cell_membrane_grouped");

    private CellRegion internalRegion;
    private CellRegion membraneRegion;
    private String templateLocation;

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
        OrganelleTemplate template = OrganelleImageParser.getOrganelleTemplate(templateLocation);
        // resize to a handleable number of edges
        while (template.getPolygon().getVertices().size() > 200) {
            template.getPolygon().reduce(1);
        }
        return new Organelle(internalRegion, membraneRegion, template);
    }

}
