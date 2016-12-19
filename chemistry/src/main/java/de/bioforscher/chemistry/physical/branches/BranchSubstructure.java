package de.bioforscher.chemistry.physical.branches;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Nucleotide;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.model.Bond;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.model.StructureFilter;
import de.bioforscher.chemistry.physical.model.Substructure;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.mathematics.matrices.SymmetricMatrix;
import de.bioforscher.mathematics.metrics.model.VectorMetricProvider;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.Vectors;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The BranchSubstructure is the central component in the three dimensional structure representation of macro molecules.
 * A BranchSubstructure can contain other substructures and/or atoms. Further implementations are used to infer more
 * information. <br/>
 * <p>
 * Each BranchSubstructure is both, a graph-like structure that connects atoms with bonds and a node of a graph.
 * As a graph a BranchSubstructure contains Elements that are themselves SubStructures or plain Atoms. Edges in a BranchSubstructure
 * are only able to connect Atoms, but this can be done across different substructures. For example, this makes it
 * possible to connect Residues in a chain with the peptide backbone ({@link Chain#connectChainBackbone()}).<br/>
 * <p>
 * SubStructures are also able to be structuring elements of a Structure such as Motifs or Domains.<br/>
 *
 * @author cl
 * @see Chain
 * @see Residue
 * @see Atom
 */
public abstract class BranchSubstructure<SubstructureType extends Substructure<SubstructureType>>
        implements Substructure<SubstructureType> {

    /*
     * ENTITY VARIABLES
     */

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
     * The neighboring substructures.
     */
    private List<SubstructureType> neighbours;
    /**
     * The substructures of this substructure.
     */
    private Map<Integer, Substructure<?>> substructures;

    /*
     * GRAPH VARIABLES
     */
    /**
     * The actual nodes
     */
    private Map<Integer, Atom> nodes;
    /**
     * The edges of the graph.
     */
    private Map<Integer, Bond> edges;

    /**
     * Creates a new BranchSubstructure. The identifier is considered in the superordinate BranchSubstructure.
     *
     * @param identifier The identifier of this BranchSubstructure.
     */
    public BranchSubstructure(int identifier) {
        this.identifier = identifier;
        this.neighbours = new ArrayList<>();
        this.substructures = new TreeMap<>();
        this.nodes = new TreeMap<>();
        this.edges = new HashMap<>();
    }

    /**
     * This is a copy constructor. Creates a new branchSubstructure with the same attributes as the given branchSubstructure. This
     * also recursively creates copies of all the underlying substructures and atoms. The neighbours of this
     * branchSubstructure are NOT copied. Due to the nature of this operation it would be bad to keep a part of the relations
     * to the lifecycle of the branchSubstructure to copy. If you want to keep the neighbouring substructures, copy the
     * superordinate branchSubstructure that contains this branchSubstructure and it will also traverse and copy the neighbouring
     * substructures.
     *
     * @param branchSubstructure The branchSubstructure to copy
     */
    public BranchSubstructure(BranchSubstructure<SubstructureType> branchSubstructure) {
        this.identifier = branchSubstructure.getIdentifier();
        this.substructures = new HashMap<>();
        for (Substructure structure : branchSubstructure.substructures.values()) {
            this.substructures.put(structure.getIdentifier(), structure.getCopy());
        }
        this.nodes = new TreeMap<>();
        for (Atom atom : branchSubstructure.nodes.values()) {
            this.nodes.put(atom.getIdentifier(), atom.getCopy());
        }
        this.edges = new HashMap<>();
        for (Bond bond : branchSubstructure.edges.values()) {
            Bond edgeCopy = bond.getCopy();
            Atom sourceCopy = this.nodes.get(bond.getSource().getIdentifier());
            Atom targetCopy = this.nodes.get(bond.getTarget().getIdentifier());
            addEdgeBetween(edgeCopy, sourceCopy, targetCopy);
        }
        this.neighbours = new ArrayList<>();
    }

    /**
     * Returns the distance matrix of all {@link LeafSubstructure}s contained in this {@link BranchSubstructure}.
     *
     * @return distance matrix of all {@link LeafSubstructure}s
     */
    public LabeledSymmetricMatrix<LeafSubstructure<?, ?>> getDistanceMatrix() {
        List<LeafSubstructure<?, ?>> leafSubstructures = getLeafSubstructures();
        SymmetricMatrix distanceMatrix = VectorMetricProvider.EUCLIDEAN_METRIC
                .calculateDistancesPairwise(leafSubstructures.stream()
                        .map(LeafSubstructure::getPosition)
                        .collect(Collectors.toList()));
        LabeledSymmetricMatrix<LeafSubstructure<?, ?>> labeledDistanceMatrix =
                new LabeledSymmetricMatrix<>(distanceMatrix.getElements());
        labeledDistanceMatrix.setRowLabels(leafSubstructures);
        return labeledDistanceMatrix;
    }

    /**
     * Returns the squared-distance matrix of all {@link LeafSubstructure}s contained in this {@link BranchSubstructure}.
     *
     * @return distance matrix of all {@link LeafSubstructure}s
     */
    public LabeledSymmetricMatrix<LeafSubstructure<?, ?>> getSquaredDistanceMatrix() {
        List<LeafSubstructure<?, ?>> leafSubstructures = getLeafSubstructures();
        SymmetricMatrix distanceMatrix = VectorMetricProvider.SQUARED_EUCLIDEAN_METRIC
                .calculateDistancesPairwise(leafSubstructures.stream()
                        .map(LeafSubstructure::getPosition)
                        .collect(Collectors.toList()));
        LabeledSymmetricMatrix<LeafSubstructure<?, ?>> labeledDistanceMatrix =
                new LabeledSymmetricMatrix<>(distanceMatrix.getElements());
        labeledDistanceMatrix.setRowLabels(leafSubstructures);
        return labeledDistanceMatrix;
    }

    /*
     * ENTITY METHODS
     */

    /**
     * Returns the identifier of this BranchSubstructure in the superordinate BranchSubstructure.
     *
     * @return The identifier.
     */
    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    /**
     * Returns the centroid of this substructure.
     *
     * @return The centroid.
     * @see Vectors#getCentroid(Collection)
     */
    @Override
    public Vector3D getPosition() {
        return Vectors.getCentroid(this.getAllAtoms().stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()))
                .as(Vector3D.class);
    }

    @Override
    public int nextNodeIdentifier() {
        return this.nextNodeIdentifier++;
    }

    /**
     * Adds a BranchSubstructure that is considered as a neighbour of this BranchSubstructure. This method should only be used when
     * also adding a edge between this BranchSubstructure and the BranchSubstructure ths is to be added.
     *
     * @param substructure The BranchSubstructure to add.
     */
    @Override
    public void addNeighbour(SubstructureType substructure) {
        this.neighbours.add(substructure);
    }

    /**
     * Returns all neighbouring SubStructures of this BranchSubstructure. Usually those SubStructures are coneccted via edges
     * in the superordinate BranchSubstructure.
     *
     * @return The neighbouring SubStructures.
     */
    @Override
    public List<SubstructureType> getNeighbours() {
        return this.neighbours;
    }

    /**
     * Returns the degree of this BranchSubstructure, defined as the number of neighbours of this structure.
     *
     * @return The degree.
     */
    @Override
    public int getDegree() {
        return this.neighbours.size();
    }

    /*
     * GRAPH METHODS
     */

    /**
     * Returns all atoms that are contained in this substructure. This does not imply all atoms of the contained
     * SubStructures. For example a chain could not contain any atoms, but only residues, that themselves contain the
     * actual atoms. To get all atoms use the {@link BranchSubstructure#getAllAtoms()} method.
     *
     * @return
     */
    @Override
    public List<Atom> getNodes() {
        return new ArrayList<>(this.nodes.values());
    }

    /**
     * Returns a specific atom of this BranchSubstructure identified by its identifier.
     *
     * @param identifier The identifier
     * @return The atom associated with the identifier.
     * @throws IllegalArgumentException if the identifier is not assigned in this substructure.
     */
    @Override
    public Atom getNode(int identifier) {
        return this.nodes.values().stream()
                .filter(atom -> atom.getIdentifier() == identifier)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * Adds a Atom to this substructure.
     *
     * @param atom The Atom to add.
     */
    @Override
    public int addNode(Atom atom) {
        this.nodes.put(atom.getIdentifier(), atom);
        return atom.getIdentifier();
    }

    /**
     * Adds all nodes in the collection to this BranchSubstructure.
     *
     * @param atoms The Atoms to add.
     */
    public void addAllNodes(Collection<Atom> atoms) {
        atoms.forEach(this::addNode);
    }

    /**
     * Considering the SubStructures that are contained in this BranchSubstructure, this method returns the next larger and
     * unused identifier.
     *
     * @return The next free identifier.
     */
    public int getNextSubstructureIdentifier() {
        if (this.substructures.keySet().isEmpty()) {
            return 0;
        }
        return Collections.max(this.substructures.keySet()) + 1;
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
     * Removes the substructure with the given identifier from this {@link Substructure}. This removes all {@link Atom}s
     * and {@link Bond}s as well
     *
     * @param identifier The identifier of the atom to remove.
     */
    public void removeSubstructure(int identifier) {
        List<Integer> atomsToBeRemoved = this.substructures.get(identifier).getAllAtoms().stream()
                .map(Atom::getIdentifier)
                .collect(Collectors.toList());
        atomsToBeRemoved.forEach(this::removeNode);
        this.substructures.entrySet().removeIf(substructure -> substructure.getValue().getIdentifier() == identifier);
    }

    /**
     * Adds all {@link Substructure}s in order of their appearance in the list.
     *
     * @param substructures The Substructures to add.
     */
    public void addAllSubstructures(List<Substructure> substructures) {
        substructures.forEach(ss -> this.substructures.put(ss.getIdentifier(), ss));
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
     * Returns all SubStructures referenced in this BranchSubstructure.
     *
     * @return All SubStructures.
     */
    public List<Substructure> getSubstructures() {
        return new ArrayList<>(this.substructures.values());
    }

    /**
     * Removes the atom with the given identifier from this BranchSubstructure. Also disbands all edges associated to this
     * node.
     *
     * @param identifier The identifier of the atom to remove.
     */
    @Override
    public void removeNode(int identifier) {
        // remove atoms and connecting edges from substructures
        this.nodes.entrySet().removeIf(node -> node.getValue().getIdentifier() == identifier);
        // remove connecting edges from in between substructures
        this.edges.entrySet().removeIf(edge -> edge.getValue().containsNode(identifier));
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
     * Returns all atoms of this BranchSubstructure and all SubStructures that are contained in this BranchSubstructure.
     *
     * @return All atoms.
     */
    public List<Atom> getAllAtoms() {
        List<Atom> atoms = new ArrayList<>();
        for (Substructure<?> substructure : this.substructures.values()) {
            atoms.addAll(substructure.getAllAtoms());
        }
        atoms.addAll(this.getNodes());
        return atoms;
    }

    /**
     * Returns all Residues that are present in this or subordinate SubStructures.
     *
     * @return All residues.
     */
    public List<Residue> getResidues() {
        return getLeafSubstructures().stream()
                .filter(StructureFilter.isResidue())
                .map(Residue.class::cast)
                .collect(Collectors.toList());
    }

    public List<Nucleotide> getNucleotides() {
        return this.getLeafSubstructures().stream()
                .filter(StructureFilter.isNucleotide())
                .map(Nucleotide.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a specific Atom is contained in this substructure.
     *
     * @param node The node.
     * @return True if the specific Atom is contained in this substructure.
     */
    @Override
    public boolean containsNode(Object node) {
        return this.nodes.containsValue(node);
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

    public List<BranchSubstructure<?>> getBranchSubstructures() {
        List<BranchSubstructure<?>> branchSubStructures = new ArrayList<>();
        for (Substructure substructure : this.substructures.values()) {
            if (substructure instanceof BranchSubstructure) {
                BranchSubstructure<?> branchSubstructure = (BranchSubstructure<?>) substructure;
                branchSubStructures.add(branchSubstructure);
                branchSubStructures.addAll(branchSubstructure.getBranchSubstructures());
            }
        }
        return branchSubStructures;
    }

    /**
     * Returns all atom-containing substructures (@{@link LeafSubstructure}s) of this {@link BranchSubstructure}.
     *
     * @return list of atom-containing substructures
     */
    public List<LeafSubstructure<?, ?>> getLeafSubstructures() {
        List<LeafSubstructure<?, ?>> leafSubstructures = new ArrayList<>();
        for (Substructure substructure : this.substructures.values()) {
            if (substructure instanceof LeafSubstructure) {
                leafSubstructures.add((LeafSubstructure) substructure);
            } else if (substructure instanceof BranchSubstructure) {
                leafSubstructures.addAll(((BranchSubstructure<?>) substructure).getLeafSubstructures());
            }
        }
        return leafSubstructures;
    }

    public Structure toStructure() {
        Structure structure = new Structure();
        structure.addSubstructure(this);
        return structure;
    }

    public abstract SubstructureType getCopy();
}
