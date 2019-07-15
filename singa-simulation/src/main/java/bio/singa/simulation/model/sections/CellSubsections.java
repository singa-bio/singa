package bio.singa.simulation.model.sections;

import bio.singa.features.identifiers.GoTerm;

import static bio.singa.features.identifiers.GoTerm.GOA_DATABASE;

/**
 * @author cl
 */
public class CellSubsections {

    public static final CellSubsection CYTOPLASM = new CellSubsection("cytoplasm", new GoTerm("GO:0005737","cytoplasm", GOA_DATABASE));
    public static final CellSubsection CELL_OUTER_MEMBRANE = new CellSubsection("cell outer membrane", new GoTerm("GO:0009279","cell outer membrane", GOA_DATABASE));
    public static final CellSubsection NUCLEOPLASM = new CellSubsection("nucleoplasm", new GoTerm("GO:0005654","nucleoplasm", GOA_DATABASE));
    public static final CellSubsection NUCLEAR_MEMBRANE = new CellSubsection("nuclear membrane", new GoTerm("GO:0031965", "nuclear membrane", GOA_DATABASE));
    public static final CellSubsection EARLY_ENDOSOME_LUMEN = new CellSubsection("early endosome lumen", new GoTerm("GO:0031905","early endosome lumen", GOA_DATABASE));
    public static final CellSubsection EARLY_ENDOSOME_MEMBRANE = new CellSubsection("early endosome membrane", new GoTerm("GO:0031901","early endosome membrane", GOA_DATABASE));
    public static final CellSubsection EXTRACELLULAR_REGION = new CellSubsection("extracellular region", new GoTerm("GO:0005576","extracellular region", GOA_DATABASE));

    public static final CellSubsection VESICLE_MEMBRANE = new CellSubsection("vesicle membrane", new GoTerm("GO:0012506", "vesicle membrane",GOA_DATABASE));
    public static final CellSubsection VESICLE_LUMEN = new CellSubsection("vesicle lumen", new GoTerm("GO:0031983", "vesicle lumen",GOA_DATABASE));

}
