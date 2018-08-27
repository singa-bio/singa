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
            "pc_nuclear_membrane"),

    CELL("cell", new GoTerm("GO:0005623"),
            CellSubsections.CYTOPLASM,
            CellSubsections.CELL_OUTER_MEMBRANE,
            CellSubsections.EXTRACELLULAR_REGION,
            "pc_cell_membrane");

    private CellRegion innerRegion;
    private CellRegion membraneRegion;
    private String templateLocation;

    OrganelleTypes(String name, GoTerm goTerm, CellSubsection innerSubsection, CellSubsection membraneSubsection, CellSubsection outerSubsection, String templateName) {
        // region for inner nodes
        innerRegion = new CellRegion(name, goTerm);
        innerRegion.addSubSection(CellTopology.INNER, innerSubsection);
        // region for membrane associated nodes
        membraneRegion = new CellRegion(membraneSubsection.getIdentifier(), membraneSubsection.getGoTerm());
        membraneRegion.addSubSection(CellTopology.INNER, innerSubsection);
        membraneRegion.addSubSection(CellTopology.MEMBRANE, membraneSubsection);
        membraneRegion.addSubSection(CellTopology.OUTER, outerSubsection);
        // location for organelle images
        templateLocation = "organelle_templates/" + templateName + ".png";
    }

    public CellRegion getInnerRegion() {
        return innerRegion;
    }

    public CellRegion getMembraneRegion() {
        return membraneRegion;
    }

    public OrganelleTemplate create() {
        OrganelleTemplate template = OrganelleImageParser.getOrganelleTemplate(templateLocation);
        // set scaling
        template.mapToSystemExtend();
        // resize to a handleable number of edges
        template.reduce();
        // if only one group is specified set the enum specified region
        if (template.getGroups().size() == 1) {
            template.initializeGroup(membraneRegion);
        }
        // set the default regions
        template.setInnerRegion(innerRegion);
        template.setMembraneRegion(membraneRegion);
        // return template
        return template;
    }

}
