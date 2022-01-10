package bio.singa.structure.model.pdb;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafIdentifier;
import bio.singa.structure.model.interfaces.LeafSubstructure;

import java.util.*;

/**
 * @author cl
 */
public abstract class PdbLeafSubstructure implements LeafSubstructure {

    /**
     * The unique leaf identifer;
     */
    private final PdbLeafIdentifier leafIdentifier;

    /**
     * The structural family of this entity
     */
    private final StructuralFamily family;

    private String divergingThreeLetterCode;

    /**
     * The atoms representing the nodes of the atom graph.
     */
    private final Map<Integer, PdbAtom> atoms;

    /**
     * The bonds representing the edges of the atom graph.
     */
    private final Map<Integer, PdbBond> bonds;

    /**
     * A iterating variable to add a new edge.
     */
    private int nextEdgeIdentifier = 0;

    /**
     * Remembers if this Leaf was an HETATOM entry
     */
    private boolean annotatedAsHetAtom;

    public PdbLeafSubstructure(PdbLeafIdentifier leafIdentifier, StructuralFamily family) {
        this.leafIdentifier = leafIdentifier;
        divergingThreeLetterCode = "";
        this.family = family;
        atoms = new TreeMap<>();
        bonds = new HashMap<>();
    }

    public PdbLeafSubstructure(PdbLeafIdentifier identifer, StructuralFamily aminoAcidFamily, String threeLetterCode) {
        leafIdentifier = identifer;
        family = aminoAcidFamily;
        divergingThreeLetterCode = threeLetterCode;
        atoms = new TreeMap<>();
        bonds = new HashMap<>();
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
    public PdbLeafSubstructure(PdbLeafSubstructure leafSubstructure) {
        this(leafSubstructure, leafSubstructure.leafIdentifier);
    }

    public PdbLeafSubstructure(PdbLeafSubstructure leafSubstructure, PdbLeafIdentifier leafIdentifier) {
        // initialize variables
        this(leafIdentifier, leafSubstructure.family, leafSubstructure.divergingThreeLetterCode);
        // copy and add all atoms
        for (PdbAtom atom : leafSubstructure.atoms.values()) {
            atoms.put(atom.getAtomIdentifier(), atom.getCopy());
        }
        // copy and add all bonds
        for (PdbBond bond : leafSubstructure.bonds.values()) {
            PdbBond edgeCopy = bond.getCopy();
            PdbAtom sourceCopy = atoms.get(bond.getSource().getAtomIdentifier());
            PdbAtom targetCopy = atoms.get(bond.getTarget().getAtomIdentifier());
            addBondBetween(edgeCopy, sourceCopy, targetCopy);
        }
        annotatedAsHetAtom = leafSubstructure.annotatedAsHetAtom;
    }

    public static PdbLeafSubstructure from(LeafSubstructure leafSubstructure) {
        if (leafSubstructure instanceof PdbLeafSubstructure) {
            return leafSubstructure.getCopy();
        } else {
            LeafIdentifier identifier = leafSubstructure.getIdentifier();
            PdbLeafIdentifier pdbLeafIdentifier = new PdbLeafIdentifier(identifier.getStructureIdentifier(), identifier.getModelIdentifier(), identifier.getChainIdentifier(), identifier.getSerial());
            PdbLeafSubstructure pdbLeafSubstructure = PdbLeafSubstructureFactory.createLeafSubstructure(pdbLeafIdentifier, leafSubstructure.getFamily());
            pdbLeafSubstructure.setAnnotatedAsHeteroAtom(leafSubstructure.isAnnotatedAsHeteroAtom());
                // copy and add all atoms
                for (Atom atom : leafSubstructure.getAllAtoms()) {
                    PdbAtom pdbAtom = new PdbAtom(atom.getAtomIdentifier(), atom.getElement(), atom.getAtomName(), atom.getPosition());
                    pdbAtom.setBFactor(atom.getBFactor());
                    pdbLeafSubstructure.addAtom(pdbAtom);
                }
                // TODO add bonds
            return pdbLeafSubstructure;
        }
    }

    public void setDivergingThreeLetterCode(String divergingThreeLetterCode) {
        this.divergingThreeLetterCode = divergingThreeLetterCode;
    }

    @Override
    public PdbLeafIdentifier getIdentifier() {
        return leafIdentifier;
    }

    @Override
    public Optional<Atom> getAtomByName(String atomName) {
        for (PdbAtom graphAtom : atoms.values()) {
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

    public void setAnnotatedAsHeteroAtom(boolean annotatedAsHetAtom) {
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
    public StructuralFamily getFamily() {
        return family;
    }

    @Override
    public Collection<PdbAtom> getAllAtoms() {
        return atoms.values();
    }

    @Override
    public Optional<PdbAtom> getAtom(Integer atomIdentifier) {
        if (atoms.containsKey(atomIdentifier)) {
            return Optional.of(atoms.get(atomIdentifier));
        }
        return Optional.empty();
    }

    public void addAtom(PdbAtom atom) {
        atoms.put(atom.getAtomIdentifier(), atom);
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        final PdbAtom atom = atoms.get(atomIdentifier);
        if (atom != null) {
            for (PdbAtom neighbor : atom.getNeighbours()) {
                neighbor.getNeighbours().remove(atom);
            }

            atoms.remove(atom.getAtomIdentifier());
            bonds.entrySet().removeIf(edge -> edge.getValue().connectsAtom(atom));
        }
    }

    public Collection<PdbBond> getBonds() {
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
    public int addBondBetween(PdbBond edge, PdbAtom source, PdbAtom target) {
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

    public int addBondBetween(PdbAtom source, PdbAtom target) {
        return addBondBetween(source, target, CovalentBondType.SINGLE_BOND);
    }

    public int addBondBetween(PdbAtom source, PdbAtom target, CovalentBondType bondType) {
        if (source == null || target == null) {
            return -1;
        }
        PdbBond bond = new PdbBond(nextEdgeIdentifier++, bondType);
        bond.setSource(source);
        bond.setTarget(target);
        bonds.put(bond.getIdentifier(), bond);
        source.addNeighbour(target);
        target.addNeighbour(source);
        return bond.getIdentifier();
    }

    public boolean hasBond(PdbAtom firstAtom, PdbAtom secondAtom) {
        return bonds.values().stream()
                .anyMatch(edge -> edge.connectsAtom(firstAtom) && edge.connectsAtom(secondAtom));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PdbLeafSubstructure that = (PdbLeafSubstructure) o;

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
