package bio.singa.simulation.model.sections;

/**
 * @author cl
 */
public class CellRegions {

    public  static final CellRegion EXTRACELLULAR_REGION = new CellRegion(CellSubsections.EXTRACELLULAR_REGION.getIdentifier(), CellSubsections.EXTRACELLULAR_REGION.getGoTerm());

    static {
        EXTRACELLULAR_REGION.addSubSection(CellTopology.INNER, CellSubsections.EXTRACELLULAR_REGION);
    }

}
