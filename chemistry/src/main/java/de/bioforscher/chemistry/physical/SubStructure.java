package de.bioforscher.chemistry.physical;

import de.bioforscher.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.mathematics.graphs.model.Graph;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.VectorUtilities;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Substructure is formally a collection of atoms that are represented together as a single entity.
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

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public Vector3D getPosition() {
        return VectorUtilities.getCentroid(this.getAllAtoms().stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()))
                .as(Vector3D.class);
    }

    @Override
    public void addNeighbour(SubStructure node) {
        this.neighbours.add(node);
    }

    @Override
    public List<SubStructure> getNeighbours() {
        return this.neighbours;
    }

    @Override
    public int getDegree() {
        return this.neighbours.size();
    }

    public void connectByDistance() {
        // calculate pairwise distances
        LabeledSymmetricMatrix<Atom> distances = StructureUtilities.calculateDistanceMatrix(getAllAtoms().stream()
                .collect(Collectors.toList()));
        // connect nodes that are below a certain distance
        for (int rowIndex = 0; rowIndex < distances.getElements().length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < distances.getElements()[rowIndex].length; columnIndex++) {
                if (rowIndex != columnIndex && distances.getElement(rowIndex, columnIndex) < 1.8) {
                    connectWithEdge(nextEdgeIdentifier(),
                            getNode(distances.getRowLabel(rowIndex).getIdentifier()),
                            getNode(distances.getColumnLabel(columnIndex).getIdentifier()),
                            new Bond(BondType.COVALENT_BOND));
                }
            }
        }
    }

    /*
     * GRAPH METHODS
     */

    public int getNextSubstructureIdentifier() {
        if (this.substructures.keySet().isEmpty()) {
            return 0;
        }
        return Collections.max(this.substructures.keySet()) + 1;
    }

    @Override
    public Set<Atom> getNodes() {
        return this.nodes.values().stream()
                .collect(Collectors.toSet());
    }

    @Override
    public Atom getNode(int identifier) {
        return this.nodes.values().stream()
                .filter(atom -> atom.getIdentifier() == identifier)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public List<Atom> getAllAtoms() {
        List<Atom> atoms = new ArrayList<>();
        for (SubStructure subStructure : this.substructures.values()) {
            if (subStructure.substructures.values().isEmpty()) {
                atoms.addAll(subStructure.getNodes());
            } else {
                atoms.addAll(subStructure.getAllAtoms());
            }
        }
        return atoms;
    }

    @Override
    public void addNode(Atom node) {
        this.nodes.put(node.getIdentifier(), node);
    }

    public void addAllNodes(Collection<Atom> nodes) {
        nodes.forEach(this::addNode);
    }

    public void addSubstructure(SubStructure subStructure) {
        this.substructures.put(subStructure.getIdentifier(), subStructure);
    }

    public void addAllSubstructures(List<SubStructure> subStructures) {
        subStructures.forEach( subStructure -> this.substructures.put(subStructure.getIdentifier(), subStructure));
    }

    public Set<SubStructure> getSubstructures() {
        return this.substructures.values().stream()
                .collect(Collectors.toSet());
    }

    @Override
    public void removeNode(int identifier) {
        // remove atoms and connecting edges from substructures
        this.nodes.entrySet().removeIf(node -> node.getValue().getIdentifier() == identifier);
        // remove connecting edges from  in between substructures
        this.edges.entrySet().removeIf(edge -> edge.getValue().containsNode(identifier));
    }

    @Override
    public Set<Bond> getEdges() {
        return new HashSet<>(this.edges.values());
    }

    @Override
    public Bond getEdge(int identifier) {
        return this.edges.get(identifier);
    }

    public void addEdge(int identifier, Bond edge) {
        this.edges.put(identifier, edge);
    }

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

    public List<Residue> getResidues() {
        List<Residue> residues = new ArrayList<>();
        for (SubStructure subStructure : this.substructures.values()) {
            if (subStructure instanceof Chain) {
                residues.addAll(subStructure.getResidues());
            }
            if (subStructure instanceof Residue) {
                residues.add((Residue) subStructure);
            }
        }
        return residues;
    }

    @Override
    public boolean containsNode(Object node) {
        return this.nodes.values().stream()
                .anyMatch(atom -> atom.equals(node));
    }

    @Override
    public boolean containsEdge(Object edge) {
        return this.edges.containsValue(edge);
    }

}
