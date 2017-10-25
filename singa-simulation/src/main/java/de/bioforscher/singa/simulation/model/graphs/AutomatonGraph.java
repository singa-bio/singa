package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * The Automaton graph class is the underlying graph of cellular graph automaton {@link Simulation}s. Each {@link
 * AutomatonNode} is placed in a two dimensional simulation space, neighbourhoods are defined by {@link AutomatonEdge}s.
 * Nodes can be assigned to groups using {@link CellSection}s that emulate compartments, extracellular space or
 * membranes.
 *
 * @author cl
 */
public class AutomatonGraph extends AbstractGraph<AutomatonNode, AutomatonEdge, Vector2D, Integer> {

    /**
     * The identifier for the next node that is to be added.
     */
    private int nextNodeIdentifier;

    /**
     * The cell sections referenced in this graph.
     */
    private Map<String, CellSection> cellSections;

    /**
     * Creates a new empty graph.
     */
    public AutomatonGraph() {
        this.cellSections = new HashMap<>();
    }

    /**
     * Creates a new empty graph, initialized with node and edge capacity.
     *
     * @param nodeCapacity The node capacity.
     * @param edgeCapacity The edge capacity.
     */
    public AutomatonGraph(int nodeCapacity, int edgeCapacity) {
        super(nodeCapacity, edgeCapacity);
        this.cellSections = new HashMap<>();
    }

    @Override
    public int addEdgeBetween(int identifier, AutomatonNode source, AutomatonNode target) {
        return addEdgeBetween(new AutomatonEdge(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(AutomatonNode source, AutomatonNode target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    @Override
    public Integer nextNodeIdentifier() {
        return this.nextNodeIdentifier++;
    }

    /**
     * Initializes the concentration of the given chemical entity of every node in this graph to to the given
     * concentration in mol/l.
     *
     * @param entity The chemical entity.
     * @param concentration The concentration in mol/l.
     */
    public void initializeSpeciesWithConcentration(ChemicalEntity entity, double concentration) {
        initializeSpeciesWithConcentration(entity, Quantities.getQuantity(concentration, MOLE_PER_LITRE));
    }

    /**
     * Initializes the concentration of the given chemical entity of every node in this graph.
     *
     * @param entity The chemical entity.
     * @param concentration The concentration.
     */
    public void initializeSpeciesWithConcentration(ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        this.getNodes().forEach(node -> node.setConcentration(entity, concentration));
    }

    /**
     * Returns all {@link CellSection}s referenced in this graph.
     *
     * @return The cell sections.
     */
    public Set<CellSection> getCellSections() {
        return new HashSet<>(this.cellSections.values());
    }

    /**
     * Return the cell section with the given identifier.
     *
     * @param identifier The identifier.
     * @return The cell section.
     */
    public CellSection getCellSection(String identifier) {
        return this.cellSections.get(identifier);
    }

    /**
     * Adds a cell section to this graph but does not associate any node to it.
     *
     * @param cellSection The cell section.
     */
    public void addCellSection(CellSection cellSection) {
        this.cellSections.put(cellSection.getIdentifier(), cellSection);
    }

    /**
     * Adds nodes in a rectangular region to the cell section. Each node in the graph that lies inside of the rectangle
     * is associated to the compartment. Additionally the outermost nodes are assigned as membrane nodes of this
     * compartment.
     *
     * @param enclosedCompartment The enclosed compartment.
     * @param rectangle The rectangle.
     */
    public void addNodesToCompartment(EnclosedCompartment enclosedCompartment, Rectangle rectangle) {
        Set<AutomatonNode> compartmentContent = new HashSet<>();
        Rectangle r = new Rectangle(rectangle.getTopRightVertex(), rectangle.getBottomLeftVertex());
        this.getNodes().forEach(node -> {
            if (node.getPosition().canBePlacedIn(r)) {
                compartmentContent.add(node);
                node.setCellSection(enclosedCompartment);
            }
        });
        if (!compartmentContent.isEmpty()) {
            this.cellSections.get(enclosedCompartment.getIdentifier()).getContent().addAll(compartmentContent);
            Membrane membrane = enclosedCompartment.generateMembrane();
            enclosedCompartment.getEnclosingMembrane().initializeNodes(this);
            this.cellSections.put(membrane.getIdentifier(), membrane);
        }
    }

}
