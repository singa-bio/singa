package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.structure.model.families.StructuralFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
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
     * The atoms representing the nodes of the atom graph.
     */
    private final Map<Integer, OakAtom> atoms;

    /**
     * The bonds representing the edges of the atom graph.
     */
    private final Map<Integer, OakBond> bonds;

    /**
     * The families to which the {@link LeafSubstructure} can be exchanged.
     */
    private final Set<FamilyType> exchangeableFamilies;

    /**
     * A iterating variable to add a new edge.
     */
    private int nextEdgeIdentifier = 0;

    /**
     * Remembers if this Leaf was an HETATOM entry
     */
    private boolean annotatedAsHetAtom;

    public OakLeafSubstructure(LeafIdentifier leafIdentifier, FamilyType family) {
        this.leafIdentifier = leafIdentifier;
        divergingThreeLetterCode = "";
        this.family = family;
        atoms = new TreeMap<>();
        bonds = new HashMap<>();
        exchangeableFamilies = new HashSet<>();
    }

    public OakLeafSubstructure(LeafIdentifier identifer, FamilyType aminoAcidFamily, String threeLetterCode) {
        leafIdentifier = identifer;
        family = aminoAcidFamily;
        divergingThreeLetterCode = threeLetterCode;
        atoms = new TreeMap<>();
        bonds = new HashMap<>();
        exchangeableFamilies = new HashSet<>();
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
            atoms.put(atom.getAtomIdentifier(), atom.getCopy());
        }
        // copy and add all bonds
        for (OakBond bond : leafSubstructure.bonds.values()) {
            OakBond edgeCopy = bond.getCopy();
            OakAtom sourceCopy = atoms.get(bond.getSource().getAtomIdentifier());
            OakAtom targetCopy = atoms.get(bond.getTarget().getAtomIdentifier());
            addBondBetween(edgeCopy, sourceCopy, targetCopy);
        }
        // add exchangeable types
        exchangeableFamilies.addAll(leafSubstructure.getExchangeableFamilies());
        annotatedAsHetAtom = leafSubstructure.annotatedAsHetAtom;
    }


    @Override
    public LeafIdentifier getIdentifier() {
        return leafIdentifier;
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
        return annotatedAsHetAtom;
    }

    public void setAnnotatedAsHetAtom(boolean annotatedAsHetAtom) {
        this.annotatedAsHetAtom = annotatedAsHetAtom;
    }

    @Override
    public String getThreeLetterCode() {
        if (divergingThreeLetterCode.isEmpty()) {
            return family.getThreeLetterCode();
        }
        return divergingThreeLetterCode;
    }

    @Override
    public FamilyType getFamily() {
        return family;
    }

    @Override
    public List<Atom> getAllAtoms() {
        return new ArrayList<>(atoms.values());
    }

    @Override
    public Optional<Atom> getAtom(Integer atomIdentifier) {
        if (atoms.containsKey(atomIdentifier)) {
            return Optional.of(atoms.get(atomIdentifier));
        }
        return Optional.empty();
    }

    public void addAtom(OakAtom atom) {
        atoms.put(atom.getAtomIdentifier(), atom);
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        final OakAtom atom = atoms.get(atomIdentifier);
        if (atom != null) {
            for (OakAtom neighbor : atom.getNeighbours()) {
                neighbor.getNeighbours().remove(atom);
            }

            atoms.remove(atom.getAtomIdentifier());
            bonds.entrySet().removeIf(edge -> edge.getValue().connectsAtom(atom));
        }
    }

    public Collection<OakBond> getBonds() {
        return bonds.values();
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
        bonds.put(edge.getIdentifier(), edge);
        source.addNeighbour(target);
        target.addNeighbour(source);
        return edge.getIdentifier();
    }

    public int addBondBetween(OakAtom source, OakAtom target) {
        return addBondBetween(source, target, BondType.SINGLE_BOND);
    }

    public int addBondBetween(OakAtom source, OakAtom target, BondType bondType) {
        if (source == null || target == null) {
            return -1;
        }
        OakBond bond = new OakBond(nextEdgeIdentifier++, bondType);
        bond.setSource(source);
        bond.setTarget(target);
        bonds.put(bond.getIdentifier(), bond);
        source.addNeighbour(target);
        target.addNeighbour(source);
        return bond.getIdentifier();
    }

    @Override
    public Set<FamilyType> getExchangeableFamilies() {
        return exchangeableFamilies;
    }

    @Override
    public void addExchangeableFamily(FamilyType exchangeableType) {
        exchangeableFamilies.add(exchangeableType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OakLeafSubstructure<?> that = (OakLeafSubstructure<?>) o;

        if (leafIdentifier != null ? !leafIdentifier.equals(that.leafIdentifier) : that.leafIdentifier != null)
            return false;
        return family != null ? family.equals(that.family) : that.family == null;
    }

    @Override
    public int hashCode() {
        int result = leafIdentifier != null ? leafIdentifier.hashCode() : 0;
        result = 31 * result + (family != null ? family.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return flatToString();
    }


    public int getNextEdgeIdentifier() {
        return nextEdgeIdentifier++;
    }

}
