package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.AtomContainer;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class CifConformation implements AtomContainer {

    public static final String DEFAULT_CONFORMATION_IDENTIFIER = "";

    private String conformationIdentifier;

    /**
     * The atoms representing the nodes of the atom graph.
     */
    private Map<Integer, CifAtom> atoms;

    public CifConformation(String conformationIdentifier) {
        atoms = new TreeMap<>();
        this.conformationIdentifier = conformationIdentifier;
    }

    public CifConformation(CifConformation cifConformation) {
        this(cifConformation.conformationIdentifier);
        // copy and add all atoms
        for (CifAtom atom : cifConformation.atoms.values()) {
            atoms.put(atom.getAtomIdentifier(), atom.getCopy());
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

    public CifConformation getCopy() {
        return new CifConformation(this);
    }

}
