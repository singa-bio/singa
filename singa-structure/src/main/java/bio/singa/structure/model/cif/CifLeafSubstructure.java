package bio.singa.structure.model.cif;

import bio.singa.core.utility.CommutablePair;
import bio.singa.core.utility.Pair;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.LeafSubstructure;

import java.util.*;

import static bio.singa.structure.model.cif.CifConformation.DEFAULT_CONFORMATION_IDENTIFIER;

public class CifLeafSubstructure implements LeafSubstructure {

    /**
     * The unique leaf identifer;
     */
    private CifLeafIdentifier leafIdentifier;

    /**
     * The structural family of this entity
     */
    private StructuralFamily family;

    private final Map<String, CifConformation> conformations;

    /**
     * pair of atom names that link the connected leaves
     */
    private final Map<Pair<String>, CifLeafIdentifier> connectedLeafs;

    /**
     * Remembers if this leaf was an HETATOM entry
     */
    private boolean annotatedAsHetAtom;

    /**
     * Remembers if this leaf is par of a polymer
     */
    private boolean isPartOfPolymer;

    public CifLeafSubstructure(CifLeafIdentifier leafIdentifier) {
        this.leafIdentifier = leafIdentifier;
        conformations = new LinkedHashMap<>();
        connectedLeafs = new HashMap<>();
    }

    public CifLeafSubstructure(CifLeafSubstructure cifLeafSubstructure) {
        this(cifLeafSubstructure.leafIdentifier);
        family = cifLeafSubstructure.family;
        annotatedAsHetAtom = cifLeafSubstructure.annotatedAsHetAtom;
        isPartOfPolymer = cifLeafSubstructure.isPartOfPolymer;
        for (Map.Entry<String, CifConformation> entry : cifLeafSubstructure.conformations.entrySet()) {
            conformations.put(entry.getKey(), entry.getValue().getCopy());
        }
        connectedLeafs.putAll(cifLeafSubstructure.connectedLeafs);
    }

    void postProcessConformations() {
        // this is only called if there are non default conformations present
        // is there any default conformation ?
        Optional<CifConformation> defaultConformationOptional = getConformation(DEFAULT_CONFORMATION_IDENTIFIER);
        if (!defaultConformationOptional.isPresent()) {
            return;
        }
        // otherwise, there is a mixture between default and divergent conformations
        // the approach is to add the default atoms to the divergent conformations,
        // such that all conformations contain all atoms of the substructure
        // hence no merging of conformations needs to be don each time a atom is requested
        // the generally low amount of divergent conformations in structures should cause low memory overhead
        CifConformation defaultConformation = defaultConformationOptional.get();
        for (CifConformation cifConformation : conformations.values()) {
            if (cifConformation.getConformationIdentifier().equals(DEFAULT_CONFORMATION_IDENTIFIER)) {
                continue;
            }
            for (CifAtom atom : defaultConformation.getAllAtoms()) {
                cifConformation.addAtom(atom);
            }
        }
    }

    public void addAtom(String conformationIdentifier, CifAtom atom) {
        if (!conformations.containsKey(conformationIdentifier)) {
            conformations.put(conformationIdentifier, new CifConformation(conformationIdentifier));
        }
        conformations.get(conformationIdentifier).addAtom(atom);
    }

    public void addAtom(CifAtom atom) {
        getFirstConformation().addAtom(atom);
    }

    public Optional<CifConformation> getConformation(String conformationIdentifier) {
        return Optional.ofNullable(conformations.get(conformationIdentifier));
    }

    public CifConformation getFirstConformation() {
        if (conformations.size() < 1) {
            throw new IllegalStateException("no conformations of " + leafIdentifier + " available");
        }
        if (conformations.size() == 1) {
            return conformations.values().iterator().next();
        }
        return getFirstAlternativeConformation().orElseThrow(() -> new IllegalStateException("no conformations of " + leafIdentifier + " available"));
    }

    public Optional<CifConformation> getFirstAlternativeConformation() {
        Iterator<CifConformation> iterator = conformations.values().iterator();
        CifConformation next = iterator.next();
        if (next.getConformationIdentifier().equals(DEFAULT_CONFORMATION_IDENTIFIER)) {
            return Optional.ofNullable(iterator.next());
        }
        return Optional.of(next);
    }

    public void connect(String atomNameOfThisLeaf, String atomNameOfOtherLeaf, CifLeafSubstructure otherLeaf) {
        connectedLeafs.put(new CommutablePair<>(atomNameOfThisLeaf, atomNameOfOtherLeaf), otherLeaf.getIdentifier());
        otherLeaf.connectedLeafs.put(new CommutablePair<>(atomNameOfOtherLeaf, atomNameOfThisLeaf), getIdentifier());
    }

    public Map<Pair<String>, CifLeafIdentifier> getConnectedLeafs() {
        return connectedLeafs;
    }

    public boolean isPartOfPolymer() {
        return isPartOfPolymer;
    }

    public void setPartOfPolymer(boolean partOfPolymer) {
        isPartOfPolymer = partOfPolymer;
    }

    @Override
    public Collection<CifAtom> getAllAtoms() {
        return getFirstConformation().getAllAtoms();
    }

    @Override
    public Optional<CifAtom> getAtom(Integer atomIdentifier) {
        return getFirstConformation().getAtom(atomIdentifier);
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        getFirstConformation().removeAtom(atomIdentifier);
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

    public void setAnnotatedAsHeteroAtom(boolean annotatedAsHetAtom) {
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

    @Override
    public String toString() {
        return flatToString();
    }

}
