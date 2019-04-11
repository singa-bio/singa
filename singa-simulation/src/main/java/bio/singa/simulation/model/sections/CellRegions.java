package bio.singa.simulation.model.sections;

import bio.singa.features.identifiers.GoTerm;

import static bio.singa.features.identifiers.GoTerm.GOA_DATABASE;

/**
 * @author cl
 */
public class CellRegions {

    public static final CellRegion CYTOPLASM_REGION = new CellRegion(CellSubsections.CYTOPLASM.getIdentifier(), CellSubsections.CYTOPLASM.getGoTerm());
    public static final CellRegion EXTRACELLULAR_REGION = new CellRegion(CellSubsections.EXTRACELLULAR_REGION.getIdentifier(), CellSubsections.EXTRACELLULAR_REGION.getGoTerm());
    public static final CellRegion CELL_OUTER_MEMBRANE_REGION = new CellRegion(CellSubsections.CELL_OUTER_MEMBRANE.getIdentifier(), CellSubsections.CELL_OUTER_MEMBRANE.getGoTerm());

    public static final CellRegion CELL_CORTEX = new CellRegion("cell cortex", new GoTerm("GO:0005938", GOA_DATABASE));
    public static final CellRegion PERINUCLEAR_REGION = new CellRegion("perinuclear region", new GoTerm("GO:0048471", GOA_DATABASE));

    public static final CellRegion VESICLE_REGION = new CellRegion("vesicle", new GoTerm("GO:0031982", GOA_DATABASE));

    static {
        CYTOPLASM_REGION.addSubsection(CellTopology.INNER, CellSubsections.CYTOPLASM);
        EXTRACELLULAR_REGION.addSubsection(CellTopology.INNER, CellSubsections.EXTRACELLULAR_REGION);

        CELL_OUTER_MEMBRANE_REGION.addSubsection(CellTopology.INNER, CellSubsections.CYTOPLASM);
        CELL_OUTER_MEMBRANE_REGION.addSubsection(CellTopology.MEMBRANE, CellSubsections.CELL_OUTER_MEMBRANE);
        CELL_OUTER_MEMBRANE_REGION.addSubsection(CellTopology.OUTER, CellSubsections.EXTRACELLULAR_REGION);

        VESICLE_REGION.addSubsection(CellTopology.MEMBRANE, CellSubsections.VESICLE_MEMBRANE);
        VESICLE_REGION.addSubsection(CellTopology.OUTER, CellSubsections.VESICLE_LUMEN);
    }

}
