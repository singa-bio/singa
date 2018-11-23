package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;

import java.util.*;

/**
 * @author cl
 */
public class Membrane {

    private String identifier;
    private List<MembraneSegment> segments;
    private CellRegion innerRegion;
    private CellRegion membraneRegion;
    private Map<CellRegion, Set<Vector2D>> regionMap;

    public Membrane(String identifier) {
        this.identifier = identifier;
        segments = new ArrayList<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void addSegment(AutomatonNode node, LineSegment segment) {
        MembraneSegment membraneSegment = new MembraneSegment(node, segment);
        segments.add(membraneSegment);
    }

    public List<MembraneSegment> getSegments() {
        return segments;
    }

    public CellRegion getInnerRegion() {
        return innerRegion;
    }

    public void setInnerRegion(CellRegion innerRegion) {
        this.innerRegion = innerRegion;
    }

    public CellRegion getMembraneRegion() {
        return membraneRegion;
    }

    public void setMembraneRegion(CellRegion membraneRegion) {
        this.membraneRegion = membraneRegion;
    }

    public Map<CellRegion, Set<Vector2D>> getRegionMap() {
        return regionMap;
    }

    public void setRegionMap(Map<CellRegion, Set<Vector2D>> regionMap) {
        this.regionMap = regionMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Membrane that = (Membrane) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
