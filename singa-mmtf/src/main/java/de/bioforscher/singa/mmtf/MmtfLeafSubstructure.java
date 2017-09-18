package de.bioforscher.singa.mmtf;

import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MmtfLeafSubstructure implements LeafSubstructureInterface {

    private StructureDataInterface data;

    /**
     * Position of this group in data array
     */
    private int internalIndex;

    private int atomStartIndex;
    private int atomEndIndex;

    public MmtfLeafSubstructure(StructureDataInterface data, int internalIndex, int atomStartIndex, int atomEndIndex) {
        this.data = data;
        this.internalIndex = internalIndex;
        this.atomStartIndex = atomStartIndex;
        this.atomEndIndex = atomEndIndex;
    }

    @Override
    public String getThreeLetterCode() {
        // get relevant string for this group type
        return data.getGroupName(data.getGroupTypeIndices()[internalIndex]);
    }

    @Override
    public List<AtomInterface> getAtoms() {
        List<AtomInterface> results = new ArrayList<>();
        for (int internalAtomIndex = atomStartIndex; internalAtomIndex <= atomEndIndex; internalAtomIndex++) {
            results.add(new MmtfAtom(data, internalIndex, internalAtomIndex-atomStartIndex, internalAtomIndex));
        }
        return results;
    }

    @Override
    public String toString() {
        return "MmtfLeafSubstructure{" +
                "data=" + data +
                ", internalIndex=" + internalIndex +
                ", atomStartIndex=" + atomStartIndex +
                ", atomEndIndex=" + atomEndIndex +
                '}';
    }
}
