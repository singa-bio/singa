package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Atom;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The implementation of {@link LeafSubstructure} for mmtf structures. Remembers the internal group index, the leaf
 * identifier and the indices of the first and the last atom belonging to this group.
 *
 * @author cl
 */
public abstract class MmtfLeafSubstructure implements LeafSubstructure {

    /**
     * The original mmtf data.
     */
    private StructureDataInterface data;

    /**
     * The index of this leaf in the group data arrays.
     */
    private int internalGroupIndex;

    /**
     * The generated leaf identifier.
     */
    private LeafIdentifier leafIdentifier;

    /**
     * The index of the first atom that belong to this leaf.
     */
    private int atomStartIndex;

    /**
     * The index of the last atom that belong to this leaf.
     */
    private int atomEndIndex;

    /**
     * Creates a new {@link MmtfLeafSubstructure}.
     *
     * @param data The original data.
     * @param leafIdentifier The leaf identifier.
     * @param internalGroupIndex The index of this leaf in the data array.
     * @param atomStartIndex The index of the first atom that belong to this leaf.
     * @param atomEndIndex The index of the last atom that belong to this leaf.
     */
    MmtfLeafSubstructure(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        this.data = data;
        this.leafIdentifier = leafIdentifier;
        this.internalGroupIndex = internalGroupIndex;
        this.atomStartIndex = atomStartIndex;
        this.atomEndIndex = atomEndIndex;
    }

    /**
     * A copy constructor that passes all attributes of the given {@link MmtfLeafSubstructure} to a new instance.
     *
     * @param mmtfLeafSubstructure The {@link MmtfLeafSubstructure} to copy.
     */
    protected MmtfLeafSubstructure(MmtfLeafSubstructure mmtfLeafSubstructure) {
        this.data = mmtfLeafSubstructure.data;
        this.leafIdentifier = mmtfLeafSubstructure.leafIdentifier;
        this.atomStartIndex = mmtfLeafSubstructure.atomStartIndex;
        this.atomEndIndex = mmtfLeafSubstructure.atomEndIndex;
    }

    @Override
    public LeafIdentifier getIdentifier() {
        return leafIdentifier;
    }

    @Override
    public String getThreeLetterCode() {
        return data.getGroupName(data.getGroupTypeIndices()[internalGroupIndex]);
    }

    @Override
    public List<Atom> getAllAtoms() {
        // terminate records are fucking the numbering up
        List<Atom> results = new ArrayList<>();
        for (int internalAtomIndex = atomStartIndex; internalAtomIndex <= atomEndIndex; internalAtomIndex++) {
            results.add(new MmtfAtom(data, internalGroupIndex, internalAtomIndex - atomStartIndex, internalAtomIndex));
        }
        return results;
    }

    @Override
    public Optional<Atom> getAtom(int atomIdentifier) {
        if (atomIdentifier < atomStartIndex || atomIdentifier > atomEndIndex) {
            return Optional.empty();
        }
        return Optional.of(new MmtfAtom(data, internalGroupIndex, atomIdentifier - atomStartIndex, atomIdentifier));
    }

}
