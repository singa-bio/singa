package bio.singa.simulation.model.sections;

import bio.singa.features.identifiers.GoTerm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 *
 * @author cl
 */
public class CellRegion {

    public static CellRegion CYTOSOL_A = new CellRegion("Cytoplasm");
    static {
        CYTOSOL_A.addSubSection(CellTopology.INNER, CellSubsection.SECTION_A);
    }

    public static CellRegion CYTOSOL_B = new CellRegion("Cytoplasm");
    static {
        CYTOSOL_B.addSubSection(CellTopology.INNER, CellSubsection.SECTION_B);
    }

    public static CellRegion MEMBRANE = new CellRegion("Membrane");
    static {
        MEMBRANE.addSubSection(CellTopology.INNER, CellSubsection.SECTION_A);
        MEMBRANE.addSubSection(CellTopology.MEMBRANE, CellSubsection.MEMBRANE);
        MEMBRANE.addSubSection(CellTopology.OUTER, CellSubsection.SECTION_B);
    }

    public static CellRegion forVesicle(String identifier) {
        CellRegion region = new CellRegion(identifier+"-region");
        region.addSubSection(CellTopology.INNER, new CellSubsection(identifier+"-cargo"));
        region.addSubSection(CellTopology.MEMBRANE, new CellSubsection(identifier+"-coat"));
        return region;
    }

    private String identifier;
    private GoTerm goTerm;
    private Map<CellTopology, CellSubsection> cellSubSections;

    public CellRegion(String identifier) {
        this.identifier = identifier;
        cellSubSections = new HashMap<>();
    }

    public CellRegion(String identifier, GoTerm goTerm) {
        this(identifier);
        this.goTerm = goTerm;
    }

    public String getIdentifier() {
        return identifier;
    }

    public GoTerm getGoTerm() {
        return goTerm;
    }

    public void addSubSection(CellTopology topology, CellSubsection subsection) {
        cellSubSections.put(topology, subsection);
    }

    public Collection<CellSubsection> getSubsections() {
        return cellSubSections.values();
    }

    public ConcentrationContainer setUpConcentrationContainer() {
        ConcentrationContainer container = new ConcentrationContainer();
        for (Map.Entry<CellTopology, CellSubsection> entry : cellSubSections.entrySet()) {
            container.initializeSubsection(entry.getValue(), entry.getKey());
        }
        return container;
    }

    public CellSubsection getInnerSubsection() {
        return cellSubSections.get(CellTopology.INNER);
    }

    public CellSubsection getOuterSubsection() {
        return cellSubSections.get(CellTopology.OUTER);
    }

    public CellSubsection getMembraneSubsection() {
        return cellSubSections.get(CellTopology.MEMBRANE);
    }

    public boolean hasMembrane() {
        return cellSubSections.containsKey(CellTopology.MEMBRANE);
    }

    @Override
    public String toString() {
        return "Region "+identifier+" ("+getSubsections().stream().map(CellSubsection::getIdentifier).collect(Collectors.joining(","))+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellRegion that = (CellRegion) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {

        return Objects.hash(identifier);
    }
}
