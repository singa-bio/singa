package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Atom;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public abstract class MmtfLeafSubstructure<LeafType extends LeafSubstructure> implements LeafSubstructure<LeafType> {

    private StructureDataInterface data;

    /**
     * Position of this group in data array
     */
    private int internalIndex;
    private LeafIdentifier leafIdentifier;

    private int atomStartIndex;
    private int atomEndIndex;

    MmtfLeafSubstructure(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalIndex, int atomStartIndex, int atomEndIndex) {
        this.data = data;
        this.leafIdentifier = leafIdentifier;
        this.internalIndex = internalIndex;
        this.atomStartIndex = atomStartIndex;
        this.atomEndIndex = atomEndIndex;
    }


    @Override
    public LeafIdentifier getIdentifier() {
        return leafIdentifier;
    }

    @Override
    public String getThreeLetterCode() {
        return data.getGroupName(data.getGroupTypeIndices()[internalIndex]);
    }

    @Override
    public List<Atom> getAllAtoms() {
        // terminate records are fucking the numbering up
        List<Atom> results = new ArrayList<>();
        for (int internalAtomIndex = atomStartIndex; internalAtomIndex <= atomEndIndex; internalAtomIndex++) {
            results.add(new MmtfAtom(data, internalIndex, internalAtomIndex-atomStartIndex, internalAtomIndex));
        }
        return results;
    }

    @Override
    public LeafType getCopy() {
        return null;
    }

}
