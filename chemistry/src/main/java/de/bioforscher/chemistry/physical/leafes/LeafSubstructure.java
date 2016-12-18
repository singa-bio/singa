package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.parser.pdb.tokens.AtomToken;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.model.*;
import de.bioforscher.core.utility.Nameable;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.Vectors;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public abstract class LeafSubstructure<LeafSubstructureType extends LeafSubstructure<LeafSubstructureType, FamilyType>,
        FamilyType extends StructuralFamily>
        implements Substructure<LeafSubstructureType>, Exchangeable<FamilyType>, Nameable {

    /**
     * The identifier of this entity.
     */
    public int identifier;
    /**
     * The families to which the {@link LeafSubstructure} can be exchanged.
     */
    protected Set<FamilyType> exchangeableTypes;
    /**
     * Maps each of the atoms contained in this LeafSubstructure to it's unique identifier.
     */
    private Map<Atom, UniqueAtomIdentifer> identiferMap;
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

    public LeafSubstructure(int identifier) {
        this.identifier = identifier;
        this.neighbours = new ArrayList<>();
        this.atoms = new TreeMap<>();
        this.bonds = new HashMap<>();
        this.exchangeableTypes = new HashSet<>();
        this.identiferMap = new HashMap<>();
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

    public List<String> getPDBLines() {
        return AtomToken.assemblePDBLine(this);
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
    /**
     * Returns the centroid of <b>ALL</b> atoms of the {@link LeafSubstructure}. This may be not intended, because
     * hydrogens are considered as well. You can use an implementation of
     * {@link de.bioforscher.chemistry.physical.atoms.representations.RepresentationScheme}
     * that does not consider hydrogens at all.
     */
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

    /**
     * Gets the atom with this name, if possible.
     *
     * @param atomName The name of the atom.
     * @return The Atom associated to this name.
     * @throws NoSuchElementException if there is no atom with this name.
     */
    public Atom getAtomByName(AtomName atomName) {
        return getNodes().stream()
                .filter(atom -> atom.getAtomName() == atomName)
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Set<FamilyType> getExchangeableTypes() {
        return exchangeableTypes;
    }

    @Override
    public void addExchangeableType(FamilyType exchangeableType) {
        this.exchangeableTypes.add(exchangeableType);
    }

    public Map<Atom, UniqueAtomIdentifer> getIdentiferMap() {
        return this.identiferMap;
    }

    public void setIdentiferMap(Map<Atom, UniqueAtomIdentifer> identiferMap) {
        this.identiferMap = identiferMap;
    }

    public String getChain() {
        return this.identiferMap.values().stream().map(UniqueAtomIdentifer::getChainIdentifer).findFirst().orElse("X");
    }

}
