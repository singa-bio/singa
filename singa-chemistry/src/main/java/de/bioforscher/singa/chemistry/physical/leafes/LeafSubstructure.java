package de.bioforscher.singa.chemistry.physical.leafes;

import de.bioforscher.singa.chemistry.parser.pdb.structures.tokens.AtomToken;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.AtomName;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationScheme;
import de.bioforscher.singa.chemistry.physical.model.*;
import de.bioforscher.singa.core.utility.Nameable;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.mathematics.vectors.Vectors3D;

import java.util.*;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter;

/**
 * The leaf substructure class represents a atom containing grouping entry in the three dimensional physical
 * representation any macro molecular structure. This abstract class is used to handle the atoms contained in this
 * structure and the bonds connecting them in a graph-like fashion. Implementations comprise {@link AminoAcid},
 * {@link AtomContainer},
 *
 * @param <LeafSubstructureType> A self reference to remember valid neighbours.
 * @param <FamilyType>           The possible representations of this leaf substructure implementation.
 * @author cl
 */
public abstract class LeafSubstructure<LeafSubstructureType extends LeafSubstructure<LeafSubstructureType, FamilyType>,
        FamilyType extends StructuralFamily>
        implements Substructure<LeafSubstructureType>, Exchangeable<FamilyType>, Nameable {

    /**
     * The unique leaf identifer;
     */
    private final LeafIdentifier leafIdentifier;

    /**
     * The structural family of this entity
     */
    private final FamilyType family;

    /**
     * The families to which the {@link LeafSubstructure} can be exchanged.
     */
    private Set<FamilyType> exchangeableFamilies;

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

    public LeafSubstructure(LeafIdentifier leafIdentifier, FamilyType family) {
        this.leafIdentifier = leafIdentifier;
        this.family = family;
        this.neighbours = new ArrayList<>();
        this.atoms = new TreeMap<>();
        this.bonds = new HashMap<>();
        this.exchangeableFamilies = new HashSet<>();
    }

    /**
     * This is a copy constructor. Creates a new leaf with the same attributes as the given leaf. This
     * also recursively creates copies of all the underlying substructures and atoms. The neighbours of this
     * substructure are NOT copied. Due to the nature of this operation it would be bad to keep a part of the relations
     * to the lifecycle of the substructure to copy. If you want to keep the neighbouring substructures, copy the
     * superordinate substructure that contains this substructure and it will also traverse and copy the neighbouring
     * substructures.
     *
     * @param leafSubstructure The leaf to copy.
     */
    public LeafSubstructure(LeafSubstructure<LeafSubstructureType, FamilyType> leafSubstructure) {
        // initialize variables
        this(leafSubstructure.leafIdentifier, leafSubstructure.family);
        // copy and add all atoms
        for (Atom atom : leafSubstructure.atoms.values()) {
            this.atoms.put(atom.getIdentifier(), atom.getCopy());
        }
        // copy and add all bonds
        for (Bond bond : leafSubstructure.bonds.values()) {
            Bond edgeCopy = bond.getCopy();
            Atom sourceCopy = this.atoms.get(bond.getSource().getIdentifier());
            Atom targetCopy = this.atoms.get(bond.getTarget().getIdentifier());
            addEdgeBetween(edgeCopy, sourceCopy, targetCopy);
        }
        // add exchangeable types
        this.exchangeableFamilies.addAll(leafSubstructure.getExchangeableFamilies());
    }

    /**
     * Returns the integer part of the leaf pdbIdentifier.
     *
     * @return The integer part of the leaf pdbIdentifier.
     */
    @Override
    public int getIdentifier() {
        return this.leafIdentifier.getIdentifier();
    }

    /**
     * Returnt the complete leaf pdbIdentifier.
     *
     * @return The complete pdbIdentifier.
     */
    public LeafIdentifier getLeafIdentifier() {
        return this.leafIdentifier;
    }

    /**
     * Returns the next free node identifer. This method returns the value of a counting variable that is used to add
     * all internal nodes. Every time this method is called a new identifer is given. You should only need to use this
     * method if you really need to add a atom form outside and cannot use predefined pdbIdentifiers.
     *
     * @return The next free node identifer.
     */
    @Override
    public int nextNodeIdentifier() {
        return this.nextNodeIdentifier++;
    }

    /**
     * Returns all atoms of this leaf substructure. The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.
     *
     * @return All atoms of this leaf substructure.
     */
    @Override
    public Collection<Atom> getNodes() {
        return this.atoms.values();
    }

    /**
     * Returns the atom with the specified identifer or {@code null} if this pdbIdentifier is not assigned.
     *
     * @param identifier The identifer.
     * @return The atom with the specified identifer.
     */
    @Override
    public Atom getNode(int identifier) {
        return this.atoms.get(identifier);
    }

    /**
     * Adds a atom to this substructure. The identifer of the atom is used to address this atom in this substructure.
     *
     * @param atom The atom to add.
     * @return The identifer of the atom.
     */
    @Override
    public int addNode(Atom atom) {
        this.atoms.put(atom.getIdentifier(), atom);
        return atom.getIdentifier();
    }

    /**
     * Removes a atom with the given identifer.
     *
     * @param identifier The identifer of the atom to be removed.
     */
    @Override
    public void removeNode(int identifier) {
        this.atoms.remove(identifier);
        this.bonds.entrySet().removeIf(bond -> bond.getValue().containsNode(identifier));
    }

    /**
     * Returns true, if a mapping with the given node is present in this leaf substructure.
     *
     * @param node The node to check.
     * @return True, if a mapping with the given node is present in this leaf substructure.
     */
    @Override
    public boolean containsNode(Object node) {
        return this.atoms.containsValue(node);
    }

    /**
     * Returns true, if any atom with the given name is present in this leaf substructure.
     *
     * @param atomName The atom name to check.
     * @return True, if any atom with the given name is present in this leaf substructure.
     */
    public boolean containsAtomWithName(AtomName atomName) {
        return this.atoms.values().stream().map(Atom::getAtomNameString).anyMatch(name -> name.equals(atomName.getName()));
    }

    /**
     * Returns the next free edge identifer. This method returns the value of a counting variable that is used to add
     * all internal edges. Every time this method is called a new identifer is given. You should only need to use this
     * method if you really need to add a bond form outside.
     *
     * @return The next free bond identifer.
     */
    @Override
    public int nextEdgeIdentifier() {
        return this.nextEdgeIdentifier++;
    }

    /**
     * Returns all bonds of this leaf substructure.
     *
     * @return All bonds of this leaf substructure.
     */
    @Override
    public Set<Bond> getEdges() {
        return new HashSet<>(this.bonds.values());
    }

    /**
     * Returns the bonds with the specified identifer or {@code null} if this pdbIdentifier is not assigned.
     *
     * @param identifier The identifer.
     * @return The bonds with the specified identifer.
     */
    @Override
    public Bond getEdge(int identifier) {
        return this.bonds.get(identifier);
    }

    /**
     * Adds a {@link BondType#SINGLE_BOND covalent} bond connecting the the given atoms. The order of the given atoms
     * does not matter, but is retained.
     *
     * @param identifier The identifer of the new bond.
     * @param source     The source atom.
     * @param target     The target atom.
     * @return The identifer of the added edge.
     */
    @Override
    public int addEdgeBetween(int identifier, Atom source, Atom target) {
        return addEdgeBetween(new Bond(identifier), source, target);
    }

    /**
     * Adds a bond connecting the the given atoms. The order of the given atoms does not matter, but is retained. The
     * bond type can be specified beforehand and the pdbIdentifier of the edge is used as the identifer in the leaf.
     *
     * @param edge   The edge to be added.
     * @param source The source atom.
     * @param target The target atom.
     * @return The identifer of the added edge.
     */
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

    /**
     * Adds a {@link BondType#SINGLE_BOND covalent} bond connecting the the given atoms. The order of the given atoms
     * does not matter, but is retained.
     *
     * @param source The source atom.
     * @param target The target atom.
     * @return The identifer to retrieve this edge.
     */
    @Override
    public int addEdgeBetween(Atom source, Atom target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }


    public int addEdgeBetween(Atom source, Atom target, BondType type) {
        return addEdgeBetween(new Bond(nextEdgeIdentifier(), type), source, target);
    }

    /**
     * Returns true, if a mapping with the given edge is present in this leaf substructure.
     *
     * @param edge The node to check.
     * @return True, if a mapping with the given edge is present in this leaf substructure.
     */
    @Override
    public boolean containsEdge(Object edge) {
        return this.bonds.containsValue(edge);
    }

    /**
     * Returns the structural family.
     *
     * @return The structural family.
     */
    @Override
    public FamilyType getFamily() {
        return this.family;
    }

    /**
     * Returns the centroid of <b>ALL</b> atoms of the {@link LeafSubstructure}. This may be not intended, because
     * hydrogens are considered as well. You can use an implementation of {@link RepresentationScheme} that does not
     * consider hydrogens at all.
     */
    @Override
    public Vector3D getPosition() {
        return Vectors3D.getCentroid(this.atoms.values().stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()));
    }

    /**
     * Adds a neighbour to this leaf substructure. This should only be used if you add an edge as well. But adding
     * an edge with the given methods always takes care of the neighbours.
     *
     * @param node The neighbour to add.
     */
    @Override
    public void addNeighbour(LeafSubstructureType node) {
        this.neighbours.add(node);
    }

    /**
     * Returns all neighbours of this leaf substructure.
     *
     * @return All neighbours of this leaf substructure.
     */
    @Override
    public List<LeafSubstructureType> getNeighbours() {
        return this.neighbours;
    }

    /**
     * Returns The number of neighbours of this leaf substructure.
     *
     * @return The number of neighbours of this leaf substructure.
     */
    @Override
    public int getDegree() {
        return this.neighbours.size();
    }

    /**
     * Returns all atoms in this leaf substructure. This collection is <b>NOT</b> backed by the map, so changes to the
     * list are <b>NOT</b> reflected in the collection, but changes to the atoms <b>are</b>.
     *
     * @return All atoms of the leaf substructure.
     */
    @Override
    public List<Atom> getAllAtoms() {
        return new ArrayList<>(this.atoms.values());
    }

    /**
     * Gets the atom with this name, if possible.
     *
     * @param atomName The name of the atom.
     * @return The Atom associated to this name.
     * @throws NoSuchElementException If there is no atom with this name.
     */
    public Atom getAtomByName(AtomName atomName) {
        return getNodes().stream()
                .filter(AtomFilter.hasAtomName(atomName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("could not parse atom name: " + atomName));
    }

    /**
     * Returns the chain pdbIdentifier this leaf belongs to.
     *
     * @return The chain pdbIdentifier this leaf belongs to.
     */
    public String getChainIdentifier() {
        return this.leafIdentifier.getChainIdentifer();
    }

    /**
     * Returns the original PDB-ID this leaf belongs to.
     *
     * @return The original PDB-ID this leaf belongs to.
     */
    public String getPdbIdentifier() {
        return this.leafIdentifier.getPdbIdentifier();
    }

    /**
     * Assembles a list of strings, where each sting represents a atom of this leaf in PDBFormat.
     *
     * @return A list of strings, where each sting represents a atom of this leaf in PDBFormat.
     */
    public List<String> getPdbLines() {
        return AtomToken.assemblePDBLine(this);
    }

    @Override
    public Set<FamilyType> getExchangeableFamilies() {
        return this.exchangeableFamilies;
    }

    @Override
    public void addExchangeableFamily(FamilyType exchangeableType) {
        this.exchangeableFamilies.add(exchangeableType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeafSubstructure<?, ?> that = (LeafSubstructure<?, ?>) o;

        if (!this.family.equals(that.family)) return false;
        return this.leafIdentifier.equals(that.leafIdentifier);
    }

    @Override
    public int hashCode() {
        int result = this.family.hashCode();
        result = 31 * result + this.leafIdentifier.hashCode();
        return result;
    }
}
