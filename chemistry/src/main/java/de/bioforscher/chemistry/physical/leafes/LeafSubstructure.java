package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.model.Bond;
import de.bioforscher.chemistry.physical.model.Exchangeable;
import de.bioforscher.chemistry.physical.model.StructuralFamily;
import de.bioforscher.chemistry.physical.model.Substructure;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.Vectors;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public abstract class LeafSubstructure<LeafSubstructureType extends LeafSubstructure<LeafSubstructureType, FamilyType>,
        FamilyType extends StructuralFamily>
        implements Substructure<LeafSubstructureType>, Exchangeable<FamilyType> {

    /**
     * The identifier of this entity.
     */
    public int identifier;
    /**
     * A iterating variable to add a new node.
     */
    private int nextNodeIdentifier;
    /**
     * A iterating variable to add a new edge.
     */
    private int nextEdgeIdentifier;
    /**
     * The neighboring leaf substructures.
     */
    private List<LeafSubstructureType> neighbours;

    /**
     * The atoms representing the nodes of the graph.
     */
    private Map<Integer, Atom> atoms;

    /**
     * The bonds representing the edges of the graph.
     */
    private Map<Integer, Bond> bonds;

    /**
     * The families to which the {@link LeafSubstructure} can be exchanged.
     */
    protected Set<FamilyType> exchangeableTypes;

    public LeafSubstructure(int identifier) {
        this.identifier = identifier;
        this.neighbours = new ArrayList<>();
        this.atoms = new TreeMap<>();
        this.bonds = new HashMap<>();
        this.exchangeableTypes = new HashSet<>();
    }

    public LeafSubstructure(LeafSubstructure<?, ?> leafSubstructure) {
        this(leafSubstructure.identifier);
        for (Atom atom : leafSubstructure.atoms.values()) {
            this.atoms.put(atom.getIdentifier(), atom.getCopy());
        }
        for (Bond bond : leafSubstructure.bonds.values()) {
            Bond edgeCopy = bond.getCopy();
            Atom sourceCopy = this.atoms.get(bond.getSource().getIdentifier());
            Atom targetCopy = this.atoms.get(bond.getTarget().getIdentifier());
            addEdgeBetween(edgeCopy, sourceCopy, targetCopy);
        }
    }

    @Override
    public int nextNodeIdentifier() {
        return this.nextNodeIdentifier++;
    }

    @Override
    public Collection<Atom> getNodes() {
        return atoms.values();
    }

    @Override
    public Atom getNode(int identifier) {
        return null;
    }

    @Override
    public int addNode(Atom atom) {
        this.atoms.put(atom.getIdentifier(), atom);
        return atom.getIdentifier();
    }

    @Override
    public void removeNode(int identifier) {
        this.atoms.entrySet().removeIf(atom -> atom.getValue().getIdentifier() == identifier);
        this.bonds.entrySet().removeIf(bond -> bond.getValue().containsNode(identifier));
    }

    @Override
    public int nextEdgeIdentifier() {
        return this.nextEdgeIdentifier++;
    }

    @Override
    public Set<Bond> getEdges() {
        return new HashSet<>(this.bonds.values());
    }

    @Override
    public Bond getEdge(int identifier) {
        return this.bonds.get(identifier);
    }

    @Override
    public int addEdgeBetween(int identifier, Atom source, Atom target) {
        return addEdgeBetween(new Bond(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(Bond edge, Atom source, Atom target) {
        if (source == null || target == null) {
            return 0;
        }
        edge.setSource(source);
        edge.setTarget(target);
        this.bonds.put(edge.getIdentifier(), edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
        return edge.getIdentifier();
    }

    @Override
    public int addEdgeBetween(Atom source, Atom target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    @Override
    public boolean containsNode(Object node) {
        return this.atoms.containsValue(node);
    }

    @Override
    public boolean containsEdge(Object edge) {
        return this.bonds.containsValue(edge);
    }

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public Vector3D getPosition() {
        return Vectors.getCentroid(this.atoms.values().stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()))
                .as(Vector3D.class);
    }

    @Override
    public void addNeighbour(LeafSubstructureType node) {
        this.neighbours.add(node);
    }

    @Override
    public List<LeafSubstructureType> getNeighbours() {
        return this.neighbours;
    }

    @Override
    public int getDegree() {
        return this.neighbours.size();
    }

    @Override
    public List<Atom> getAllAtoms() {
        return new ArrayList<>(this.atoms.values());
    }

    @Override
    public Set<FamilyType> getExchangeableTypes() {
        return exchangeableTypes;
    }

    @Override
    public void addExchangeableType(FamilyType exchangeableType) {
        this.exchangeableTypes.add(exchangeableType);
    }
}
