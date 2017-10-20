package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.structure.model.graph.model.LeafIdentifier;
import de.bioforscher.singa.structure.model.graph.model.StructuralFamily;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;

import java.util.*;

/**
 * @author cl
 */
public abstract class OakLeafSubstructure<FamilyType extends StructuralFamily> implements LeafSubstructure<FamilyType> {

    /**
     * The unique leaf identifer;
     */
    private final LeafIdentifier leafIdentifier;

    /**
     * The structural family of this entity
     */
    private final FamilyType family;

    private final String divergingThreeLetterCode;

    /**
     * A iterating variable to add a new node.
     */
    private int nextNodeIdentifier;

    /**
     * A iterating variable to add a new edge.
     */
    private int nextEdgeIdentifier = 0;

    /**
     * The atoms representing the nodes of the atom graph.
     */
    private Map<Integer, OakAtom> atoms;

    /**
     * s The bonds representing the edges of the atom graph.
     */
    private Map<Integer, OakBond> bonds;

    /**
     * Remembers if this Leaf was an HETATOM entry
     */
    private boolean annotatedAsHetAtom;

    /**
     * The families to which the {@link LeafSubstructure} can be exchanged.
     */
    private Set<FamilyType> exchangeableFamilies;

    public OakLeafSubstructure(LeafIdentifier leafIdentifier, FamilyType family) {
        this.leafIdentifier = leafIdentifier;
        this.divergingThreeLetterCode = "";
        this.family = family;
        this.atoms = new TreeMap<>();
        this.bonds = new HashMap<>();
        this.exchangeableFamilies = new HashSet<>();
    }

    public OakLeafSubstructure(LeafIdentifier identifer, FamilyType aminoAcidFamily, String threeLetterCode) {
        this.leafIdentifier = identifer;
        this.family = aminoAcidFamily;
        this.divergingThreeLetterCode = threeLetterCode;
        this.atoms = new TreeMap<>();
        this.bonds = new HashMap<>();
        this.exchangeableFamilies = new HashSet<>();
    }

    /**
     * This is a copy constructor. Creates a new leaf with the same attributes as the given leaf. This also recursively
     * creates copies of all the underlying substructures and atoms. The neighbours of this substructure are NOT copied.
     * Due to the nature of this operation it would be bad to keep a part of the relations to the lifecycle of the
     * substructure to copy. If you want to keep the neighbouring substructures, copy the superordinate substructure
     * that contains this substructure and it will also traverse and copy the neighbouring substructures.
     *
     * @param leafSubstructure The leaf to copy.
     */
    public OakLeafSubstructure(OakLeafSubstructure<FamilyType> leafSubstructure) {
        // initialize variables
        this(leafSubstructure.leafIdentifier, leafSubstructure.family);
        // copy and add all atoms
        for (OakAtom atom : leafSubstructure.atoms.values()) {
            this.atoms.put(atom.getIdentifier(), atom.getCopy());
        }
        // copy and add all bonds
        for (OakBond bond : leafSubstructure.bonds.values()) {
            OakBond edgeCopy = bond.getCopy();
            OakAtom sourceCopy = this.atoms.get(bond.getSource().getIdentifier());
            OakAtom targetCopy = this.atoms.get(bond.getTarget().getIdentifier());
            addBondBetween(edgeCopy, sourceCopy, targetCopy);
        }
        // add exchangeable types
        this.exchangeableFamilies.addAll(leafSubstructure.getExchangeableFamilies());
        this.annotatedAsHetAtom = leafSubstructure.annotatedAsHetAtom;
    }


    @Override
    public LeafIdentifier getIdentifier() {
        return this.leafIdentifier;
    }

    @Override
    public boolean containsAtomWithName(String atomName) {
        return getAtomByName(atomName).isPresent();
    }

    @Override
    public Optional<Atom> getAtomByName(String atomName) {
        for (OakAtom graphAtom : atoms.values()) {
            if (graphAtom.getAtomName().equals(atomName)) {
                return Optional.of(graphAtom);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isAnnotatedAsHeteroAtom() {
        return this.annotatedAsHetAtom;
    }

    public void setAnnotatedAsHetAtom(boolean annotatedAsHetAtom) {
        this.annotatedAsHetAtom = annotatedAsHetAtom;
    }

    @Override
    public String getThreeLetterCode() {
        return this.family.getThreeLetterCode();
    }

    @Override
    public FamilyType getFamily() {
        return this.family;
    }

    @Override
    public List<Atom> getAllAtoms() {
        return new ArrayList<>(this.atoms.values());
    }

    @Override
    public Optional<Atom> getAtom(Integer atomIdentifier) {
        if (this.atoms.containsKey(atomIdentifier)) {
            return Optional.of(this.atoms.get(atomIdentifier));
        }
        return Optional.empty();
    }

    public void addAtom(OakAtom atom) {
        this.atoms.put(atom.getIdentifier(), atom);
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        final OakAtom atom = this.atoms.get(atomIdentifier);
        if (atom != null) {
            for (OakAtom neighbor : atom.getNeighbours()) {
                neighbor.getNeighbours().remove(atom);
            }

            this.atoms.remove(atom.getIdentifier());
            this.bonds.entrySet().removeIf(edge -> edge.getValue().connectsAtom(atom));
        }
    }

    /**
     * Adds a bond connecting the the given atoms. The order of the given atoms does not matter, but is retained. The
     * bond type can be specified beforehand and the pdbIdentifier of the edge is used as the identifer in the leaf.
     *
     * @param edge The edge to be added.
     * @param source The source atom.
     * @param target The target atom.
     * @return The identifer of the added edge.
     */
    public int addBondBetween(OakBond edge, OakAtom source, OakAtom target) {
        if (source == null || target == null) {
            return -1;
        }
        edge.setSource(source);
        edge.setTarget(target);
        this.bonds.put(edge.getIdentifier(), edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
        return edge.getIdentifier();
    }

    public int addBondBetween(OakAtom source, OakAtom target) {
        if (source == null || target == null) {
            return -1;
        }
        OakBond bond = new OakBond(nextEdgeIdentifier++);
        bond.setSource(source);
        bond.setTarget(target);
        this.bonds.put(bond.getIdentifier(), bond);
        source.addNeighbour(target);
        target.addNeighbour(source);
        return bond.getIdentifier();
    }

}
