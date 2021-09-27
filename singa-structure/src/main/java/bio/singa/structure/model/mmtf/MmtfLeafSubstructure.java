package bio.singa.structure.model.mmtf;

import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.oak.LeafIdentifier;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.*;

/**
 * The implementation of {@link LeafSubstructure} for mmtf structures. Remembers the internal group index, the leaf
 * identifier and the indices of the first and the last atom belonging to this group.
 *
 * @author cl
 */
public abstract class MmtfLeafSubstructure<FamilyType extends StructuralFamily> implements LeafSubstructure<FamilyType> {

    /**
     * The original bytes kept to copy.
     */
    private final byte[] bytes;
    /**
     * The original mmtf data.
     */
    private final StructureDataInterface data;
    /**
     * The index of this leaf in the group data arrays.
     */
    private final int internalGroupIndex;
    /**
     * The generated leaf identifier.
     */
    private final LeafIdentifier leafIdentifier;
    /**
     * The atoms that have already been requested.
     */
    private final Map<Integer, MmtfAtom> cachedAtoms;
    /**
     * The index of the first atom that belong to this leaf.
     */
    private final int atomStartIndex;
    /**
     * The index of the last atom that belong to this leaf.
     */
    private final int atomEndIndex;
    /**
     * The set of atoms anot available
     */
    private final Set<Integer> removedAtoms;

    /**
     * The structural family of this entity
     */
    protected FamilyType family;
    /**
     * The families to which the {@link LeafSubstructure} can be exchanged.
     */
    protected Set<FamilyType> exchangeableFamilies;


    /**
     * Creates a new {@link MmtfLeafSubstructure}.
     *
     * @param data The original data.
     * @param family The leaf family.
     * @param leafIdentifier The leaf identifier.
     * @param internalGroupIndex The index of this leaf in the data array.
     * @param atomStartIndex The index of the first atom that belong to this leaf.
     * @param atomEndIndex The index of the last atom that belong to this leaf.
     */
    MmtfLeafSubstructure(StructureDataInterface data, byte[] bytes, FamilyType family, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        this.data = data;
        this.bytes = bytes;
        this.family = family;
        this.leafIdentifier = leafIdentifier;
        this.internalGroupIndex = internalGroupIndex;
        this.atomStartIndex = atomStartIndex;
        this.atomEndIndex = atomEndIndex;
        removedAtoms = new HashSet<>();
        // take care of alternative positions by moving them to removed atoms
        final char[] alternativeLocationCodes = data.getAltLocIds();
        for (int internalAtomIndex = atomStartIndex; internalAtomIndex <= atomEndIndex; internalAtomIndex++) {
            final char alternativeLocationCode = alternativeLocationCodes[internalAtomIndex];
            // using 'A' to identify the first alternative location might be vulnerable
            if (alternativeLocationCode != LeafIdentifier.DEFAULT_INSERTION_CODE && alternativeLocationCode != 'A') {
                removedAtoms.add(internalAtomIndex);
            }
        }
        exchangeableFamilies = new HashSet<>();
        cachedAtoms = new HashMap<>();
    }

    /**
     * A copy constructor that passes all attributes of the given {@link MmtfLeafSubstructure} to a new instance.
     *
     * @param mmtfLeafSubstructure The {@link MmtfLeafSubstructure} to copy.
     */
    protected MmtfLeafSubstructure(MmtfLeafSubstructure<?> mmtfLeafSubstructure) {
        bytes = mmtfLeafSubstructure.bytes;
        data = mmtfLeafSubstructure.data;
        leafIdentifier = mmtfLeafSubstructure.leafIdentifier;
        internalGroupIndex = mmtfLeafSubstructure.internalGroupIndex;
        atomStartIndex = mmtfLeafSubstructure.atomStartIndex;
        atomEndIndex = mmtfLeafSubstructure.atomEndIndex;
        removedAtoms = new HashSet<>(mmtfLeafSubstructure.removedAtoms);

        // effectively copy atoms
        cachedAtoms = new HashMap<>();
        for (Map.Entry<Integer, MmtfAtom> entry : mmtfLeafSubstructure.cachedAtoms.entrySet()) {
            cachedAtoms.put(entry.getKey(), (MmtfAtom) entry.getValue().getCopy());
        }
    }

    @Override
    public LeafIdentifier getIdentifier() {
        return leafIdentifier;
    }

    @Override
    public String getThreeLetterCode() {
        return family.getThreeLetterCode();
    }

    @Override
    public List<Atom> getAllAtoms() {
        List<Atom> results = new ArrayList<>();
        for (int internalAtomIndex = atomStartIndex; internalAtomIndex <= atomEndIndex; internalAtomIndex++) {
            // skip removed atoms
            if (removedAtoms.contains(internalAtomIndex)) {
                continue;
            }
            // cache atoms
            if (cachedAtoms.containsKey(internalAtomIndex)) {
                results.add(cachedAtoms.get(internalAtomIndex));
            } else {
                MmtfAtom mmtfAtom = new MmtfAtom(data, internalGroupIndex, internalAtomIndex - atomStartIndex, internalAtomIndex);
                cachedAtoms.put(internalAtomIndex, mmtfAtom);
                results.add(mmtfAtom);
            }
        }
        return results;
    }

    @Override
    public Optional<Atom> getAtom(Integer internalAtomIndex) {
        // offset between internal and external indices
        internalAtomIndex--;
        if (internalAtomIndex < atomStartIndex || internalAtomIndex > atomEndIndex || removedAtoms.contains(internalAtomIndex)) {
            return Optional.empty();
        }
        return cacheAtom(internalAtomIndex);
    }

    @Override
    public void removeAtom(Integer atomIdentifier) {
        removedAtoms.add(atomIdentifier - 1);
    }

    @Override
    public boolean containsAtomWithName(String atomName) {
        for (Atom atom : getAllAtoms()) {
            if (atom.getAtomName().equals(atomName)) {
                return true;
            }
        }
        return false;
    }

    private Optional<Atom> cacheAtom(int internalAtomIndex) {
        if (cachedAtoms.containsKey(internalAtomIndex)) {
            return Optional.of(cachedAtoms.get(internalAtomIndex));
        } else {
            MmtfAtom mmtfAtom = new MmtfAtom(data, internalGroupIndex, internalAtomIndex - atomStartIndex, internalAtomIndex);
            cachedAtoms.put(internalAtomIndex, mmtfAtom);
            return Optional.of(mmtfAtom);
        }
    }

    @Override
    public Optional<Atom> getAtomByName(String atomName) {
        for (int internalAtomIndex = atomStartIndex; internalAtomIndex <= atomEndIndex; internalAtomIndex++) {
            // skip removed atoms
            if (removedAtoms.contains(internalAtomIndex)) {
                continue;
            }
            final String actualAtomName = data.getGroupAtomNames(data.getGroupTypeIndices()[internalGroupIndex])[internalAtomIndex - atomStartIndex];
            if (atomName.equals(actualAtomName)) {
                return cacheAtom(internalAtomIndex);
            }
        }
        return Optional.empty();
    }

    @Override
    public FamilyType getFamily() {
        return family;
    }

    @Override
    public Set<FamilyType> getExchangeableFamilies() {
        return exchangeableFamilies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MmtfLeafSubstructure<?> that = (MmtfLeafSubstructure<?>) o;
        return Objects.equals(family, that.family) &&
                Objects.equals(leafIdentifier, that.leafIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(family, leafIdentifier);
    }

    @Override
    public String toString() {
        return flatToString();
    }

}
