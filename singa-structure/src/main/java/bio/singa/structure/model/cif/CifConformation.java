package bio.singa.structure.model.cif;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.structure.model.interfaces.AtomContainer;

import java.util.*;

public class CifConformation implements AtomContainer {

    public static final String DEFAULT_CONFORMATION_IDENTIFIER = "";

    private String conformationIdentifier;

    /**
     * The atoms representing the nodes of the atom graph.
     */
    private Map<Integer, CifAtom> atoms;

    /**
     * The bonds representing the edges of the atom graph.
     */
    private Map<Integer, CifBond> bonds;

    /**
     * A iterating variable to add a new edge.
     */
    private int nextEdgeIdentifier = 0;

    public CifConformation(String conformationIdentifier) {
        atoms = new TreeMap<>();
        bonds = new HashMap<>();
        this.conformationIdentifier = conformationIdentifier;
    }

    public CifConformation(CifConformation cifConformation) {
        this(cifConformation.conformationIdentifier);
        // copy and add all atoms
        for (CifAtom atom : cifConformation.atoms.values()) {
            atoms.put(atom.getAtomIdentifier(), atom.getCopy());
        }
        // copy and add all bonds
        for (CifBond bond : cifConformation.bonds.values()) {
            CifBond edgeCopy = bond.getCopy();
            CifAtom sourceCopy = atoms.get(bond.getSource().getAtomIdentifier());
            CifAtom targetCopy = atoms.get(bond.getTarget().getAtomIdentifier());
            addBondBetween(edgeCopy, sourceCopy, targetCopy);
        }
    }


    public String getConformationIdentifier() {
        return conformationIdentifier;
    }

    public void setConformationIdentifier(String conformationIdentifier) {
        this.conformationIdentifier = conformationIdentifier;
    }

    @Override
    public Collection<CifAtom> getAllAtoms() {
        return atoms.values();
    }

    public void addAtom(CifAtom atom) {
        atoms.put(atom.getAtomIdentifier(), atom);
    }

    @Override
    public Optional<CifAtom> getAtom(Integer atomIdentifier) {
        return Optional.ofNullable(atoms.get(atomIdentifier));
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        atoms.remove(atomIdentifier);
    }

    public Collection<CifBond> getBonds() {
        return bonds.values();
    }

    /**
     * Adds a bond connecting to the given atoms. The order of the given atoms does not matter, but is retained. The
     * bond type can be specified beforehand and the pdbIdentifier of the edge is used as the identifier in the leaf.
     *
     * @param edge The edge to be added.
     * @param source The source atom.
     * @param target The target atom.
     * @return The identifier of the added edge.
     */
    public int addBondBetween(CifBond edge, CifAtom source, CifAtom target) {
        if (source == null || target == null || hasBond(source, target)) {
            return -1;
        }
        edge.setSource(source);
        edge.setTarget(target);
        bonds.put(edge.getIdentifier(), edge);
        // source.addNeighbour(target);
        // target.addNeighbour(source);
        return edge.getIdentifier();
    }

    public int addBondBetween(CifAtom source, CifAtom target) {
        return addBondBetween(source, target, CovalentBondType.SINGLE_BOND);
    }

    public int addBondBetween(CifAtom source, CifAtom target, CovalentBondType bondType) {
        if (source == null || target == null || hasBond(source, target)) {
            return -1;
        }
        return addBondBetween(new CifBond(nextEdgeIdentifier++, bondType), source, target);
    }

    public boolean hasBond(CifAtom firstAtom, CifAtom secondAtom) {
        return bonds.values().stream()
                .anyMatch(edge -> edge.connectsAtom(firstAtom) && edge.connectsAtom(secondAtom));
    }

    public CifConformation getCopy() {
        return new CifConformation(this);
    }

}
