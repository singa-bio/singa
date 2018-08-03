package bio.singa.simulation.model.agents.membranes;

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
    private CellRegion representativeRegion;
    private List<MembraneSegment> segments;

    public Membrane(String identifier, CellRegion representativeRegion) {
        this.identifier = identifier;
        this.representativeRegion = representativeRegion;
        segments = new ArrayList<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public CellRegion getRepresentativeRegion() {
        return representativeRegion;
    }

    public void addSegment(AutomatonNode node, LineSegment segment) {
        MembraneSegment membraneSegment = new MembraneSegment(node, segment);
        segments.add(membraneSegment);
    }

    public List<MembraneSegment> getSegments() {
        return segments;
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
