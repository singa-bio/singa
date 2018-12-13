package bio.singa.simulation.model.graphs;

import bio.singa.mathematics.graphs.grid.AbstractGridGraph;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Automaton graph class is the underlying graph of cellular graph automaton {@link Simulation}s. Each {@link
 * AutomatonNode} is placed in a two dimensional simulation space, neighbourhoods are defined by {@link AutomatonEdge}s.
 * Nodes can be assigned to groups using {@link CellRegion}s that emulate compartments, extracellular space or
 * membranes.
 *
 * @author cl
 */
public class AutomatonGraph extends AbstractGridGraph<AutomatonNode, AutomatonEdge, Vector2D> {

    /**
     * The cell sections referenced in this graph.
     */
    private final Map<String, CellRegion> cellRegions;

    /**
     * Creates a new empty graph, initialized with node and edge capacity.
     *
     * @param columns The node capacity.
     * @param rows The edge capacity.
     */
    public AutomatonGraph(int columns, int rows) {
        super(columns, rows);
        cellRegions = new HashMap<>();
    }

    @Override
    public int addEdgeBetween(int identifier, AutomatonNode source, AutomatonNode target) {
        return addEdgeBetween(new AutomatonEdge(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(AutomatonNode source, AutomatonNode target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    /**
     * Returns all {@link CellSubsection}s referenced in this graph.
     *
     * @return The cell sections.
     */
    public Set<CellRegion> getCellSections() {
        return new HashSet<>(cellRegions.values());
    }

    /**
     * Return the cell section with the given identifier.
     *
     * @param identifier The identifier.
     * @return The cell section.
     */
    public CellRegion getCellRegion(String identifier) {
        return cellRegions.get(identifier);
    }

    /**
     * Adds a cell section to this graph but does not associate any node to it.
     *
     * @param cellSection The cell section.
     */
    public void addCellRegion(CellRegion cellSection) {
        cellRegions.put(cellSection.getIdentifier(), cellSection);
    }

}
