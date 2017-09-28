package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Atom;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author cl
 */
public abstract class MmtfLeafSubstructure<LeafType extends LeafSubstructure> implements LeafSubstructure<LeafType> {

    private StructureDataInterface data;

    /**
     * Position of this leaf (group) in data array
     */
    private int internalGroupIndex;
    private LeafIdentifier leafIdentifier;

    private int atomStartIndex;
    private int atomEndIndex;

    MmtfLeafSubstructure(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        this.data = data;
        this.leafIdentifier = leafIdentifier;
        this.internalGroupIndex = internalGroupIndex;
        this.atomStartIndex = atomStartIndex;
        this.atomEndIndex = atomEndIndex;
    }

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
            results.add(new MmtfAtom(data, internalGroupIndex, internalAtomIndex-atomStartIndex, internalAtomIndex));
        }
        return results;
    }

    @Override
    public Optional<Atom> getAtom(int atomIdentifier) {
        if (atomIdentifier < atomStartIndex || atomIdentifier > atomEndIndex) {
            return Optional.empty();
        }
        return Optional.of(new MmtfAtom(data, internalGroupIndex, atomIdentifier-atomStartIndex, atomIdentifier));
    }

}
