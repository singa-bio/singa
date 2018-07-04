package de.bioforscher.singa.simulation.model.modules.macroscopic;

import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.sections.CellRegion;

import java.util.*;

/**
 * @author cl
 */
public class MacroscopicMembrane {

    private String identifier;
    private CellRegion representativeRegion;
    private Map<AutomatonNode, List<LineSegment>> segments;

    public MacroscopicMembrane(String identifier, CellRegion representativeRegion) {
        this.identifier = identifier;
        this.representativeRegion = representativeRegion;
        segments = new LinkedHashMap<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public CellRegion getRepresentativeRegion() {
        return representativeRegion;
    }

    public void addSegment(AutomatonNode node, LineSegment segment) {
        if (!segments.containsKey(node)) {
            segments.put(node, new ArrayList<>());
        }
        segments.get(node).add(segment);
    }

    public Map<AutomatonNode, List<LineSegment>> getSegments() {
        return segments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MacroscopicMembrane that = (MacroscopicMembrane) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {

        return Objects.hash(identifier);
    }
}
