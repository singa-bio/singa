package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.bonds.Bond;
import de.bioforscher.chemistry.physical.proteins.Chain;
import de.bioforscher.chemistry.physical.proteins.Residue;
import de.bioforscher.mathematics.graphs.model.Graph;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.VectorUtilities;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The SubStructure is the central component in the three dimensional structure representation of macro molecules.
 * A SubStructure can contain other substructures and/or atoms. Further implementations are used to infer more
 * information. <br/>
 * <p>
 * Each SubStructure is both, a graph-like structure that connects atoms with bonds and a node of a graph.
 * As a graph a Substructure contains Elements that are themselves SubStructures or plain Atoms. Edges in a SubStructure
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
public class SubStructure implements Graph<Atom, Bond>, StructuralEntity<SubStructure> {

    /*
     * ENTITY VARIABLES
     */

    /**
     * The identifier of this entity.
     */
    public int identifier;

    /**
     * The neighboring substructures.
     */
    private List<SubStructure> neighbours;

    /*
     * GRAPH VARIABLES
     */

    /**
     * The substructures of this substructure.
     */
    private Map<Integer, SubStructure> substructures;

    /**
     * The actual nodes
     */
    private Map<Integer, Atom> nodes;

    /**
     * The edges of the graph.
     */
    private Map<Integer, Bond> edges;

    /**
     * Creates a new SubStructure. The identifier is considered in the superordinate SubStructure.
     *
     * @param identifier The identifier of this SubStructure.
     */
    public SubStructure(int identifier) {
        this.identifier = identifier;
        this.neighbours = new ArrayList<>();
        this.substructures = new HashMap<>();
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
    }

    /*
     * ENTITY METHODS
     */

    /**
     * Returns the identifier of this SubStructure in the superordinate SubStructure.
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
     * @see VectorUtilities#getCentroid(Collection)
     */
    @Override
    public Vector3D getPosition() {
        return VectorUtilities.getCentroid(this.getAllAtoms().stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()))
                .as(Vector3D.class);
    }

    /**
     * Adds a SubStructure that is considered as a neighbour of this SubStructure. This method should only be used when
     * also adding a edge between this SubStructure and the SubStructure ths is to be added.
     *
     * @param subStructure The SubStructure to add.
     */
    @Override
    public void addNeighbour(SubStructure subStructure) {
        this.neighbours.add(subStructure);
    }

    /**
     * Returns all neighbouring SubStructures of this SubStructure. Usually those SubStructures are coneccted via edges
     * in the superordinate SubStructure.
     *
     * @return The neighbouring SubStructures.
     */
    @Override
    public List<SubStructure> getNeighbours() {
        return this.neighbours;
    }

    /**
     * Returns the degree of this Substructure, defined as the number of neighbours of this structure.
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
     * Returns all atoms that are contained in this substructure. This does not imply all atoms from the contained
     * SubStructures. For example a chain could not contain any atoms, but only residues, that themselves contain the
     * actual atoms. To get all atoms use the {@link SubStructure#getAllAtoms()} method.
     *
     * @return
     */
    @Override
    public Set<Atom> getNodes() {
        return this.nodes.values().stream()
                .collect(Collectors.toSet());
    }

    /**
     * Returns a specific atom from this SubStructure identified by its identifier.
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
    public void addNode(Atom atom) {
        this.nodes.put(atom.getIdentifier(), atom);
    }

    /**
     * Adds all nodes in the collection to this SubStructure.
     *
     * @param atoms The Atoms to add.
     */
    public void addAllNodes(Collection<Atom> atoms) {
        atoms.forEach(this::addNode);
    }

    /**
     * Considering the SubStructures that are contained in this SubStructure, this method returns the next larger and
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
     * Adds a SubStructure to this SubStructure.
     *
     * @param subStructure The SubStructure to add.
     */
    public void addSubstructure(SubStructure subStructure) {
        this.substructures.put(subStructure.getIdentifier(), subStructure);
    }

    /**
     * Adds all Substructures in order of their appearance in the list.
     *
     * @param subStructures The SubStructures to add.
     */
    public void addAllSubstructures(List<SubStructure> subStructures) {
        subStructures.forEach(subStructure -> this.substructures.put(subStructure.getIdentifier(), subStructure));
    }

    public SubStructure getSubStructure(int identifier) {
        return this.substructures.get(identifier);
    }

    /**
     * Returns all SubStructures referenced in this SubStructure.
     *
     * @return All SubStructures.
     */
    public List<SubStructure> getSubstructures() {
        return this.substructures.values().stream()
                .collect(Collectors.toList());
    }

    /**
     * Removes the atom with the given identifier from this SubStructure. Also disbands all edges associated to this
     * node.
     *
     * @param identifier The identifier of the atom to remove.
     */
    @Override
    public void removeNode(int identifier) {
        // remove atoms and connecting edges from substructures
        this.nodes.entrySet().removeIf(node -> node.getValue().getIdentifier() == identifier);
        // remove connecting edges from  in between substructures
        this.edges.entrySet().removeIf(edge -> edge.getValue().containsNode(identifier));
    }

    /**
     * Gets all bonds that are present in this Substructure. These can also span different across Substructures.
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

    /**
     * Adds a predefined bond with identifier.
     *
     * @param identifier The identifier.
     * @param bond       The bond.
     */
    public void addEdge(int identifier, Bond bond) {
        this.edges.put(identifier, bond);
    }

    /**
     * Creates a new Bond with the given parameters.
     *
     * @param identifier The identifier.
     * @param source     The source atom (order is irrelevant).
     * @param target     The target atom (order is irrelevant).
     * @param edgeClass  The class of the desired edge.
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

    /**
     * Adds the actual edge and adds the neighbours to the Atoms.
     *
     * @param identifier The identifier.
     * @param source     The source atom (order is irrelevant).
     * @param target     The target atom (order is irrelevant).
     * @param edge       The predefined edge.
     */
    private void connectWithEdge(int identifier, Atom source, Atom target, Bond edge) {
        this.edges.put(identifier, edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
    }

    /**
     * Returns all atoms from this SubStructure and all SubStructures that are contained in this SubStructure.
     *
     * @return All atoms.
     */
    public List<Atom> getAllAtoms() {
        List<Atom> atoms = new ArrayList<>();
        for (SubStructure subStructure : this.substructures.values()) {
            if (subStructure.substructures.values().isEmpty()) {
                atoms.addAll(subStructure.getNodes());
            } else {
                atoms.addAll(subStructure.getAllAtoms());
            }
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
        List<Residue> residues = new ArrayList<>();
        for (SubStructure subStructure : this.substructures.values()) {
            if (subStructure instanceof Residue) {
                residues.add((Residue) subStructure);
            } else {
                residues.addAll(subStructure.getResidues());
            }
        }
        return residues;
    }

    /**
     * Checks if a specific Atom is contained in this substructure.
     *
     * @param node The node.
     * @return True if the specific Atom is contained in this substructure.
     */
    @Override
    public boolean containsNode(Object node) {
        // TODO we should be able to tweak this by using parallel streams (feedback demanded)
        return this.nodes.values().stream()
                .anyMatch(atom -> atom.equals(node));
    }

    /**
     * Checks if a specific Edge (or bond) is contained in this substructure.
     *
     * @param edge The node.
     * @return True if the specific Edge is contained in this substructure.
     */
    @Override
    public boolean containsEdge(Object edge) {
        return this.edges.containsValue(edge);
    }

    /**
     * Returns all atom-containing substructures for a substructure, i.e. all substructures with non-empty
     * {@link Atom} lists, which can be for instance {@link Residue}s, {@link Nucleotide}s or {@link Ligand}.
     * This method returns a list containing the element itself if this is already a {@link SubStructure} with atoms
     *
     * @param substructure                The substructure for which all atom-containing substructures are wanted.
     * @param atomContainingSubStructures
     * @return The list of atom containing substructures.
     */
    private List<SubStructure> findAtomContainingSubStructures(List<SubStructure> atomContainingSubstructures, SubStructure substructure) {
        // substructure contains atoms
        if (substructure != null && !substructure.getNodes().isEmpty()) {
            atomContainingSubstructures.add(substructure);
        } else {
            Iterator<SubStructure> substructureIterator = getSubstructures().iterator();
            while (substructureIterator.hasNext()) {
                findAtomContainingSubStructures(atomContainingSubstructures, substructureIterator.next());
            }
        }
        return atomContainingSubstructures;
    }

    public List<SubStructure> getAtomContainingSubstructures() {
        List<SubStructure> atomContainingSubstructrues = new ArrayList<>();
        for (SubStructure subStructure : this.substructures.values()) {
            if (subStructure.substructures.values().isEmpty()) {
                atomContainingSubstructrues.add(subStructure);
            } else {
                atomContainingSubstructrues.addAll(subStructure.getAtomContainingSubstructures());
            }
        }
        if(!getNodes().isEmpty())
            atomContainingSubstructrues.add(this);
        return atomContainingSubstructrues;
    }
}
