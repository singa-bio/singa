package bio.singa.structure.model.cif;

import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.pdb.PdbAtom;
import bio.singa.structure.model.pdb.PdbBond;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class CifLeafSubstructure implements LeafSubstructure {

    /**
     * The unique leaf identifer;
     */
    private CifLeafIdentifier leafIdentifier;

    /**
     * The structural family of this entity
     */
    private StructuralFamily family;

    /**
     * The atoms representing the nodes of the atom graph.
     */
    private Map<Integer, CifAtom> atoms;

    /**
     * Remembers if this Leaf was an HETATOM entry
     */
    private boolean annotatedAsHetAtom;

    public CifLeafSubstructure(CifLeafIdentifier leafIdentifier) {
        this.leafIdentifier = leafIdentifier;
        atoms = new TreeMap<>();
    }

    public CifLeafSubstructure(CifLeafSubstructure cifLeafSubstructure) {
        leafIdentifier = cifLeafSubstructure.leafIdentifier;
        family = cifLeafSubstructure.family;
        atoms = new TreeMap<>();
        // copy and add all atoms
        for (CifAtom atom : cifLeafSubstructure.atoms.values()) {
            atoms.put(atom.getAtomIdentifier(), atom.getCopy());
        }
        annotatedAsHetAtom = cifLeafSubstructure.annotatedAsHetAtom;
    }

    public void addAtom(CifAtom atom) {
        atoms.put(atom.getAtomIdentifier(), atom);
    }

    @Override
    public Collection<CifAtom> getAllAtoms() {
        return atoms.values();
    }

    @Override
    public Optional<CifAtom> getAtom(Integer atomIdentifier) {
        return Optional.ofNullable(atoms.get(atomIdentifier));
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        atoms.remove(atomIdentifier);
    }

    @Override
    public CifLeafIdentifier getIdentifier() {
        return leafIdentifier;
    }

    @Override
    public Optional<CifAtom> getAtomByName(String atomName) {
        return getAllAtoms().stream()
                .filter(atom -> atom.getAtomName().equals(atomName))
                .findAny();
    }

    public void setAnnotatedAsHetAtom(boolean annotatedAsHetAtom) {
        this.annotatedAsHetAtom = annotatedAsHetAtom;
    }

    @Override
    public boolean isAnnotatedAsHeteroAtom() {
        return annotatedAsHetAtom;
    }

    @Override
    public String getThreeLetterCode() {
        return family.getThreeLetterCode();
    }

    @Override
    public CifLeafSubstructure getCopy() {
        return new CifLeafSubstructure(this);
    }

    @Override
    public StructuralFamily getFamily() {
        return family;
    }

    public void setFamily(StructuralFamily family) {
        this.family = family;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CifLeafSubstructure that = (CifLeafSubstructure) o;

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

}
