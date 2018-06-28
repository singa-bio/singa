package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.graphs.grid.AbstractGridGraph;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

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
     * Initializes the concentration of the given chemical entity of every node in this graph to to the given
     * concentration in mol/l.
     *
     * @param entity The chemical entity.
     * @param concentration The concentration in mol/l.
     */
    public void initializeSpeciesWithConcentration(ChemicalEntity entity, double concentration) {
        initializeSpeciesWithConcentration(entity, Quantities.getQuantity(concentration, MOLE_PER_LITRE).to(Environment.getConcentrationUnit()));
    }

    /**
     * Initializes the concentration of the given chemical entity of every node in this graph.
     *
     * @param entity The chemical entity.
     * @param concentration The concentration.
     */
    public void initializeSpeciesWithConcentration(ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        getNodes().forEach(node -> node.getConcentrationContainer().set(node.getCellRegion().getInnerSubsection(), entity, concentration));
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
