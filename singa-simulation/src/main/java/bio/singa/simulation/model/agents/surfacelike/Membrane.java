package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author cl
 */
public class Membrane {

    private String identifier;
    private List<MembraneSegment> segments;
    private CellRegion innerRegion;
    private CellRegion membraneRegion;

    public Membrane(String identifier) {
        this.identifier = identifier;
        segments = new ArrayList<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void addSegment(AutomatonNode node, LineSegment segment) {
        addSegment(new MembraneSegment(node, segment));
    }

    public void addSegment(MembraneSegment membraneSegment) {
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
