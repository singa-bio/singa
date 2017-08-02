package de.bioforscher.singa.chemistry.physical.branches;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.interactions.Bond;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.Nucleotide;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.Substructure;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.mathematics.vectors.Vectors3D;

import java.util.*;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.BranchFilter.isChain;
import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.LeafFilter.isAminoAcid;
import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.LeafFilter.isNucleotide;

/**
 * The BranchSubstructure is the central component in the three dimensional structure representation of macro molecules.
 * A BranchSubstructure can contain other substructures and/or atoms. Further implementations are used to infer more
 * information. <br> <p> Each BranchSubstructure is both, a graph-like structure that connects atoms with bonds and a
 * node of a graph. As a graph a BranchSubstructure contains Elements that are themselves SubStructures or plain
 * AtomFilter. Edges in a BranchSubstructure are only able to connect AtomFilter, but this can be done across different
 * substructures. For example, this makes it possible to connect AminoAcids in a chainIdentifier with the peptide
 * backbone ({@link Chain#connectChainBackbone()}).<br> <p> SubStructures are also able to be structuring elements of a
 * Structure such as Motifs or Domains.<br>
 *
 * @author cl
 * @see Chain
 * @see AminoAcid
 * @see Atom
 */
public abstract class BranchSubstructure<SubstructureType extends Substructure<SubstructureType, IdentifierType>, IdentifierType>
        implements Substructure<SubstructureType, IdentifierType> {

    /**
     * The identifier of this entity.
     */
    protected IdentifierType identifier;

    /**
     * The substructures of this substructure.
     */
    protected Map<Object, Substructure<?, ?>> substructures;

    private LeafIdentifier nextLeafIdentifier;

    /**
     * A iterating variable to add a new edge.
     */
    private int nextEdgeIdentifier;

    /**
     * The neighboring substructures.
     */
    private List<SubstructureType> neighbours;

    /**
     * The edges of the graph.
     */
    private Map<Integer, Bond> edges;

    /**
     * Creates a new BranchSubstructure. The identifier is considered in the superordinate BranchSubstructure.
     *
     * @param identifier The identifier of this BranchSubstructure.
     */
    public BranchSubstructure(IdentifierType identifier) {
        this.identifier = identifier;
        this.neighbours = new ArrayList<>();
        this.substructures = new TreeMap<>();
        this.edges = new HashMap<>();
    }

    /**
     * This is a copy constructor. Creates a new branchSubstructure with the same attributes as the given
     * branchSubstructure. This also recursively creates copies of all the underlying substructures and atoms. The
     * neighbours of this branchSubstructure are NOT copied. Due to the nature of this operation it would be bad to keep
     * a part of the relations to the lifecycle of the branchSubstructure to copy. If you want to keep the neighbouring
     * substructures, copy the superordinate branchSubstructure that contains this branchSubstructure and it will also
     * traverse and copy the neighbouring substructures.
     *
     * @param branchSubstructure The BranchSubstructure to copy
     */
    public BranchSubstructure(BranchSubstructure<SubstructureType, IdentifierType> branchSubstructure) {
        this.identifier = branchSubstructure.getIdentifier();
        this.substructures = new TreeMap<>();
        for (Substructure structure : branchSubstructure.substructures.values()) {
            this.substructures.put(structure.getIdentifier(), structure.getCopy());
        }
        this.edges = new HashMap<>();
        for (Bond bond : branchSubstructure.edges.values()) {
            Bond edgeCopy = bond.getCopy();
            Atom sourceCopy = branchSubstructure.getNode(bond.getSource().getIdentifier());
            Atom targetCopy = branchSubstructure.getNode(bond.getTarget().getIdentifier());
            addEdgeBetween(edgeCopy, sourceCopy, targetCopy);
        }
        this.neighbours = new ArrayList<>();
    }

    /**
     * Returns the identifier of this BranchSubstructure in the superordinate BranchSubstructure.
     *
     * @return The identifier.
     */
    @Override
    public IdentifierType getIdentifier() {
        return this.identifier;
    }

    /**
     * Returns the centroid of this substructure.
     *
     * @return The centroid.
     * @see Vectors3D#getCentroid(Collection)
     */
    @Override
    public Vector3D getPosition() {
        return Vectors3D.getCentroid(this.getAllAtoms().stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()));
    }

    /**
     * Moves all atoms in this BranchSubstructure, such that the centroid of this branch is at the specified position.
     *
     * @param position The new centroid position.
     */
    @Override
    public void setPosition(Vector3D position) {
        this.getAllAtoms().forEach(atom -> atom.setPosition(atom.getPosition().add(position)));
    }

    /**
     * Adds a BranchSubstructure that is considered as a neighbour of this BranchSubstructure. This method should only
     * be used when also adding a edge between this BranchSubstructure and the BranchSubstructure ths is to be added.
     *
     * @param substructure The BranchSubstructure to add.
     */
    @Override
    public void addNeighbour(SubstructureType substructure) {
        if (this.equals(substructure)) {
            throw new IllegalArgumentException("Can not establish self reference between two identical substructures.");
        }
        this.neighbours.add(substructure);
    }

    /**
     * Returns all neighbouring SubStructures of this BranchSubstructure. Usually those SubStructures are connected via
     * edges in the superordinate BranchSubstructure.
     *
     * @return The neighbouring SubStructures.
     */
    @Override
    public List<SubstructureType> getNeighbours() {
        return this.neighbours;
    }

    /**
     * Returns the degree of this BranchSubstructure, defined as the number of neighbours of this BranchSubstructure.
     *
     * @return The degree.
     */
    @Override
    public int getDegree() {
        return this.neighbours.size();
    }

    /**
     * Returns all atoms that are contained in this substructure. This method has the same behaviour as the {@link
     * BranchSubstructure#getAllAtoms()}.
     *
     * @return All atoms that are contained in this substructure.
     */
    @Override
    public List<Atom> getNodes() {
        return getAllAtoms();
    }

    /**
     * Returns a specific atom of this BranchSubstructure identified by its identifier.
     *
     * @param identifier The identifier
     * @return The atom associated with the identifier.
     * @throws IllegalArgumentException if the identifier is not assigned in this substructure.
     */
    @Override
    public Atom getNode(Integer identifier) {
        return this.getBranchSubstructures().stream()
                .flatMap(branchSubstructure -> branchSubstructure.getAllAtoms().stream())
                .filter(atom -> atom.getIdentifier().equals(identifier))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not get atom with identifier " + identifier + "."));
    }

    /**
     * Adds a Atom to this substructure. This Method is not usable on substructures that do not contain atoms.
     *
     * @param atom The Atom to add.
     */
    @Override
    public Integer addNode(Atom atom) {
        throw new UnsupportedOperationException("BranchSubstructures only contains atoms in leaf substructures. If you " +
                "want to add a atom, add it to any of the leafs of this branch.");
    }

    /**
     * Adds all nodes in the collection to this BranchSubstructure. This Method is not usable on substructures that do
     * not contain atoms.
     *
     * @param atoms The AtomFilter to add.
     */
    public void addAllNodes(Collection<Atom> atoms) {
        throw new UnsupportedOperationException("BranchSubstructures only contains atoms in leaf substructures. If you " +
                "want to add a atom, add it to any of the leafs of this branch.");
    }

    /**
     * Adds a BranchSubstructure to this BranchSubstructure.
     *
     * @param substructure The BranchSubstructure to add.
     */
    public void addSubstructure(Substructure substructure) {
        this.substructures.put(substructure.getIdentifier(), substructure);
    }

    /**
     * Removes the substructure from this BranchSubstructure.
     *
     * @param substructure The substructure to remove.
     * @param <RemovableSubstructureType> The Type of the substructure.
     */
    public <RemovableSubstructureType extends Substructure> void removeSubstructure(RemovableSubstructureType substructure) {
        removeSubstructure(substructure.getIdentifier());
    }

    /**
     * Removes the substructure with the given identifier from this {@link Substructure}. This removes all {@link Atom}s
     * and {@link Bond}s as well <p>
     *
     * @param identifier The identifier of the atom to remove.
     */
    protected void removeSubstructure(Object identifier) {
        if (identifier instanceof LeafIdentifier && this instanceof StructuralModel) {
            // this is a model and we want to remove leaves
            LeafIdentifier leafIdentifier = (LeafIdentifier) identifier;
            Optional<Chain> chainWithLeaf = getBranchSubstructures().stream()
                    // find chain of this leaf
                    .filter(isChain())
                    .map(Chain.class::cast)
                    .filter(chain -> chain.getIdentifier().equals(leafIdentifier.getChainIdentifier()))
                    .findFirst();
            if (chainWithLeaf.isPresent()) {
                chainWithLeaf.get().removeSubstructure(leafIdentifier);
            } else {
                throw new IllegalArgumentException("Tried to remove substructure with identifier" +
                        identifier.toString() + ", but was unable to retrieve it.");
            }
        } else {
            // this is a chain or a structural motif
            List<Integer> atomsToBeRemoved = this.substructures.get(identifier).getAllAtoms().stream()
                    .map(Atom::getIdentifier)
                    .collect(Collectors.toList());
            atomsToBeRemoved.forEach(this::removeNode);
            this.substructures.remove(identifier);
        }
    }

    /**
     * Returns all Substructures referenced in this BranchSubstructure.
     *
     * @return All Substructures referenced in this BranchSubstructure.
     */
    public List<Substructure<?, ?>> getSubstructures() {
        return new ArrayList<>(this.substructures.values());
    }

    /**
     * Returns all BranchSubstructures referenced in this and all underlying BranchSubstructures.
     *
     * @return All BranchSubstructures referenced in this and all underlying BranchSubstructures.
     */
    public List<BranchSubstructure<?, ?>> getBranchSubstructures() {
        List<BranchSubstructure<?, ?>> branchSubStructures = new ArrayList<>();
        for (Substructure substructure : this.substructures.values()) {
            if (substructure instanceof BranchSubstructure) {
                BranchSubstructure<?, ?> branchSubstructure = (BranchSubstructure<?, ?>) substructure;
                branchSubStructures.add(branchSubstructure);
                branchSubStructures.addAll(branchSubstructure.getBranchSubstructures());
            }
        }
        return branchSubStructures;
    }

    /**
     * Returns all atom-containing {@link LeafSubstructure}s of this {@link BranchSubstructure}.
     *
     * @return list of atom-containing substructures
     */
    public List<LeafSubstructure<?, ?>> getLeafSubstructures() {
        List<LeafSubstructure<?, ?>> leafSubstructures = new ArrayList<>();
        for (Substructure substructure : this.substructures.values()) {
            if (substructure instanceof LeafSubstructure) {
                leafSubstructures.add((LeafSubstructure) substructure);
            } else if (substructure instanceof BranchSubstructure) {
                leafSubstructures.addAll(((BranchSubstructure<?, ?>) substructure).getLeafSubstructures());
            }
        }
        return leafSubstructures;
    }

    /**
     * Returns all AminoAcids that are present in this or subordinate Substructures.
     *
     * @return All residues.
     */
    public List<AminoAcid> getAminoAcids() {
        return getLeafSubstructures().stream()
                .filter(isAminoAcid())
                .map(AminoAcid.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Returns all Nucleotides that are present in this or subordinate Substructures.
     *
     * @return All Nucleotides.
     */
    public List<Nucleotide> getNucleotides() {
        return this.getLeafSubstructures().stream()
                .filter(isNucleotide())
                .map(Nucleotide.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Returns the {@link Substructure} with the given identifier or {@link Optional#empty()} if no such substructure
     * exists.
     *
     * @param identifier The identifier that should be returned.
     * @return The matching Substructure or {@link Optional#empty()}.
     */
    public Optional<Substructure> getSubstructure(int identifier) {
        return Optional.ofNullable(this.substructures.getOrDefault(identifier, null));
    }

    /**
     * Adds all {@link Substructure}s in order of their appearance in the list.
     *
     * @param substructures The Substructures to add.
     */
    public void addAllSubstructures(List<Substructure> substructures) {
        substructures.forEach(substructure -> this.substructures.put(substructure.getIdentifier(), substructure));
    }

    /**
     * Removes the atom with the given identifier from this BranchSubstructure. Also disbands all edges associated to
     * this node.
     *
     * @param atom The atom to remove.
     */
    @Override
    public Atom removeNode(Atom atom) {
        return removeNode(atom.getIdentifier());
    }

    /**
     * Removes the atom with the given identifier from this BranchSubstructure. Also disbands all edges associated to
     * this node.
     *
     * @param identifier The identifier of the atom to remove.
     */
    @Override
    public Atom removeNode(Integer identifier) {
        LeafSubstructure<?, ?> leafWithAtom = getLeafSubstructures().stream()
                .filter(leafSubstructure -> leafSubstructure.getNodes().stream()
                        .anyMatch(atom -> atom.getIdentifier().equals(identifier)))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not remove atom with identifier " + identifier + "," +
                        "No leaf found that contains any atom with this identifier."));
        return leafWithAtom.removeNode(identifier);
    }


    @Override
    public int nextEdgeIdentifier() {
        return this.nextEdgeIdentifier++;
    }

    /**
     * Gets all bonds that are present in this BranchSubstructure. These can also span different across Substructures.
     *
     * @return All bonds.
     */
    @Override
    public Set<Bond> getEdges() {
        return new HashSet<>(this.edges.values());
    }

    /**
     * Gets a specific bond using its identifier.
     *
     * @param identifier The identifier.
     * @return The Edge.
     */
    @Override
    public Bond getEdge(int identifier) {
        return this.edges.get(identifier);
    }


    @Override
    public int addEdgeBetween(int identifier, Atom source, Atom target) {
        return addEdgeBetween(new Bond(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(Bond edge, Atom source, Atom target) {
        edge.setSource(source);
        edge.setTarget(target);
        this.edges.put(edge.getIdentifier(), edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
        return edge.getIdentifier();
    }

    @Override
    public int addEdgeBetween(Atom source, Atom target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    /**
     * Returns all atoms of this BranchSubstructure and all SubStructures that are contained in this
     * BranchSubstructure.
     *
     * @return All atoms.
     */
    public List<Atom> getAllAtoms() {
        List<Atom> atoms = new ArrayList<>();
        for (Substructure<?, ?> substructure : this.substructures.values()) {
            atoms.addAll(substructure.getAllAtoms());
        }
        return atoms;
    }

    public LeafIdentifier getNextLeafIdentifier() {
        // lazily determine
        LeafIdentifier last;
        if (this.nextLeafIdentifier == null) {
            last = getLeafSubstructures().stream()
                    .map(LeafSubstructure::getIdentifier)
                    .max(LeafIdentifier::compareTo)
                    .orElseThrow(() -> new IllegalStateException("Could not find any largest leaf identifier, " +
                            "possibly this structure does not contain any elements."));

        } else {
            last = this.nextLeafIdentifier;
        }
        this.nextLeafIdentifier = new LeafIdentifier(last.getPdbIdentifier(), last.getModelIdentifier(),
                last.getChainIdentifier(), last.getSerial() + 1);
        return nextLeafIdentifier;
    }

    /**
     * Checks if a specific Atom is contained in this substructure.
     *
     * @param node The node.
     * @return True if the specific Atom is contained in this substructure.
     */
    @Override
    public boolean containsNode(Object node) {
        return getAllAtoms().contains(node);
    }

    /**
     * Checks if a specific Bond is contained in this substructure.
     *
     * @param edge The edge.
     * @return True if the specific Edge is contained in this substructure.
     */
    @Override
    public boolean containsEdge(Object edge) {
        return this.edges.containsValue(edge);
    }

    public abstract SubstructureType getCopy();

    @Override
    public Integer nextNodeIdentifier() {
        //FIXME not yet implemented
        throw new UnsupportedOperationException();
    }

    public String flatToString() {
        return getClass().getSimpleName() + " " + identifier + ": " + getSubstructures().stream()
                .map(branch -> branch.getClass().getSimpleName() + " " + branch.getIdentifier())
                .collect(Collectors.joining(", "));
    }

    public String deepToString() {
        return getClass().getSimpleName() + " " + identifier + " with Branches: {" + getSubstructures().stream()
                .map(Substructure::flatToString)
                .collect(Collectors.joining(", ")) + "}";
    }

    @Override
    public String toString() {
        return flatToString();
    }
}
