package de.bioforscher.chemistry.physical;

import de.bioforscher.mathematics.graphs.model.Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Structure represents chemical objects in a three dimensional space. Substructures are used to partition a structure
 * into smaller substructures that can be connected with edges.
 */
public class Structure implements Graph<Atom, Bond> {

    /**
     * The nodes of the graph.
     */
    private Map<Integer, SubStructure> nodes;

    /**
     * The edges of the graph.
     */
    private Map<Integer, Bond> edges;

    public Structure() {
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
    }

    /**
     * Returns all atoms of this Structure.
     *
     * @return All atoms of this structure.
     */
    @Override
    public Set<Atom> getNodes() {
        return this.nodes.values().stream().flatMap(s -> s.getNodes().stream()).collect(Collectors.toSet());
    }

    /**
     * Returns a specific atom of this structure.
     */
    @Override
    public Atom getNode(int identifier) {
        return this.nodes.values().stream().flatMap(s -> s.getNodes().stream()).filter(atom -> atom.getIdentifier()
                == identifier).findAny().get();
    }

    /**
     * Adds a new atom in a new substructure, with the same identifier as the atom.
     *
     * @param node The atom to add
     */
    @Override
    public void addNode(Atom node) {
        this.nodes.put(node.getIdentifier(), new SubStructure(node));
    }

    /**
     * Adds a predefined Substructure to this Structure. This Substructure needs to have a unique identifier, with which
     * it can be addressed.
     *
     * @param subStructure
     */
    public void addSubstructure(SubStructure subStructure) {
        this.nodes.put(subStructure.getIdentifier(), subStructure);
    }

    /**
     * Removes the atom with the given identifier and all connecting edges.
     *
     * @param identifier The identifier of the atom, this is to be removed.
     */
    @Override
    public void removeNode(int identifier) {
        // remove atoms and connecting edges from substructures
        this.nodes.values().forEach(n -> n.removeNode(identifier));
        // remove connecting edges from  in between substructures
        this.edges.entrySet().removeIf(edge -> edge.getValue().containsNode(identifier));
    }

    /**
     * Returns all edges that connect the substructures, but not edges that connect substructures internally.
     *
     * @return All edges between substructures.
     */
    @Override
    public Set<Bond> getEdges() {
        return new HashSet<>(this.edges.values());
    }

    /**
     * Gets a edge with the given identifier.
     *
     * @param identifier the Identifier of the edge.
     * @return The desired edge and {@code null} if the edge could ot be retrieved.
     */
    @Override
    public Bond getEdge(int identifier) {
        return this.edges.get(identifier);
    }

    /**
     * Connects two atoms with an edge of the given class. This method tries to create an edge with an parameter less
     * constructor and afterwards connects both atoms with this edge. Use this Method only if you want to introduce some
     * new kind of edge that is no bond. Source and target are exchangeable and do not inherit a meaning of direction.
     *
     * @param identifier The identifier of the edge.
     * @param source The source atom.
     * @param target The target atom.
     * @param edgeClass The new class of edge that inherits from Bond.
     */
    @Override
    public void connect(int identifier, Atom source, Atom target, Class<Bond> edgeClass) {
        Bond bond = null;
        try {
            bond = edgeClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assert bond != null;
        bond.setIdentifier(identifier);
        bond.setSource(source);
        bond.setTarget(target);
        connectWithEdge(identifier, source, target, bond);
    }

    private void connectWithEdge(int identifier, Atom source, Atom target, Bond
            edge) {
        this.edges.put(identifier, edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
    }

    @Override
    public boolean containsNode(Object node) {
        return this.nodes.values().stream().flatMap(s -> s.getNodes().stream()).anyMatch(atom -> atom.equals(node));
    }

    @Override
    public boolean containsEdge(Object edge) {
        return this.edges.containsValue(edge);
    }

}
