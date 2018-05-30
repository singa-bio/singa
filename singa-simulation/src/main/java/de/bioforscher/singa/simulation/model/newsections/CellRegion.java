package de.bioforscher.singa.simulation.model.newsections;

import de.bioforscher.singa.features.parameters.Environment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static de.bioforscher.singa.simulation.model.newsections.CellTopology.INNER;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.OUTER;

/**
 * @author cl
 */
public class CellRegion {

    public static CellRegion CYTOSOL_A = new CellRegion("Cytoplasm");
    static {
        CYTOSOL_A.addSubSection(INNER, CellSubsection.SECTION_A);
    }

    public static CellRegion CYTOSOL_B = new CellRegion("Cytoplasm");
    static {
        CYTOSOL_B.addSubSection(INNER, CellSubsection.SECTION_B);
    }

    public static CellRegion MEMBRANE = new CellRegion("Membrane");
    static {
        MEMBRANE.addSubSection(INNER, CellSubsection.SECTION_A);
        MEMBRANE.addSubSection(CellTopology.MEMBRANE, CellSubsection.MEMBRANE);
        MEMBRANE.addSubSection(OUTER, CellSubsection.SECTION_B);
    }

    public static CellRegion forVesicle(String identifier, CellSubsection outerCompartment) {
        CellRegion region = new CellRegion("Vesicle-"+identifier);
        region.addSubSection(INNER, new CellSubsection("V"+identifier+"-Inner"));
        region.addSubSection(CellTopology.MEMBRANE, new CellSubsection("V"+identifier+"-Membrane"));
        region.addSubSection(OUTER, outerCompartment);
        return region;
    }

    private String identifier;

    private Map<CellTopology, CellSubsection> cellSubSections;

    public CellRegion(String identifier) {
        this.identifier = identifier;
        cellSubSections = new HashMap<>();
    }

    public String getIdentifier() {
        return identifier;
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
            entry.getValue().setPreferredConcentrationUnit(Environment.getTransformedMolarConcentration());
        }
        return container;
    }

    public CellSubsection getInnerSubsection() {
        return cellSubSections.get(INNER);
    }

    public CellSubsection getOuterSubsection() {
        return cellSubSections.get(OUTER);
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
}
