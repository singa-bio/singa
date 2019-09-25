package bio.singa.simulation.model.sections;

import bio.singa.features.identifiers.GoTerm;
import bio.singa.mathematics.geometry.model.Polygon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static bio.singa.simulation.model.sections.CellTopology.*;

/**
 * @author cl
 */
public class CellRegion {

    private String identifier;
    private GoTerm goTerm;
    private Map<CellTopology, CellSubsection> cellSubSections;
    private Polygon areaRepresentation;

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

    public void addSubsection(CellTopology topology, CellSubsection subsection) {
        cellSubSections.put(topology, subsection);
    }

    public Polygon getAreaRepresentation() {
        return areaRepresentation;
    }

    public void setAreaRepresentation(Polygon areaRepresentation) {
        this.areaRepresentation = areaRepresentation;
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
        return cellSubSections.get(INNER);
    }

    public CellSubsection getOuterSubsection() {
        return cellSubSections.get(OUTER);
    }

    public CellSubsection getMembraneSubsection() {
        return cellSubSections.get(MEMBRANE);
    }

    public boolean has(CellTopology topology) {
        return cellSubSections.containsKey(topology);
    }

    public boolean hasMembrane() {
        return cellSubSections.containsKey(MEMBRANE);
    }

    public boolean hasInner() {
        return cellSubSections.containsKey(INNER);
    }

    public boolean hasOuter() {
        return cellSubSections.containsKey(OUTER);
    }

    @Override
    public String toString() {
        return identifier + (goTerm != null ? " (" + goTerm.getContent() + ")" : "");
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
