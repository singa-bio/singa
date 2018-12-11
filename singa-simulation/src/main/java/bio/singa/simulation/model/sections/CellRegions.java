package bio.singa.simulation.model.sections;

import bio.singa.features.identifiers.GoTerm;

/**
 * @author cl
 */
public class CellRegions {

    public static final CellRegion EXTRACELLULAR_REGION = new CellRegion(CellSubsections.EXTRACELLULAR_REGION.getIdentifier(), CellSubsections.EXTRACELLULAR_REGION.getGoTerm());
    public static final CellRegion CELL_OUTER_MEMBRANE_REGION = new CellRegion(CellSubsections.CELL_OUTER_MEMBRANE.getIdentifier(), CellSubsections.CELL_OUTER_MEMBRANE.getGoTerm());
    public static final CellRegion EARLY_ENDOSOME_VESICLE_REGION = new CellRegion("Early Endosome", new GoTerm("GO:0005768"));

    static {
        EXTRACELLULAR_REGION.addSubSection(CellTopology.INNER, CellSubsections.EXTRACELLULAR_REGION);

        CELL_OUTER_MEMBRANE_REGION.addSubSection(CellTopology.INNER, CellSubsections.CYTOPLASM);
        CELL_OUTER_MEMBRANE_REGION.addSubSection(CellTopology.MEMBRANE, CellSubsections.CELL_OUTER_MEMBRANE);
        CELL_OUTER_MEMBRANE_REGION.addSubSection(CellTopology.OUTER, CellSubsections.EXTRACELLULAR_REGION);

        EARLY_ENDOSOME_VESICLE_REGION.addSubSection(CellTopology.MEMBRANE, CellSubsections.EARLY_ENDOSOME_MEMBRANE);
        EARLY_ENDOSOME_VESICLE_REGION.addSubSection(CellTopology.OUTER, CellSubsections.EARLY_ENDOSOME_LUMEN);

    }

}
