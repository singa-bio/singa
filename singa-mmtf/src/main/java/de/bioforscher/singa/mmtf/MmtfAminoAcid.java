package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.AminoAcid;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

/**
 * The implementation of {@link AminoAcid}s for mmtf structures.
 *
 * @author cl
 */
public class MmtfAminoAcid extends MmtfLeafSubstructure implements AminoAcid {

    /**
     * Creates a new {@link MmtfAminoAcid}.
     *
     * @param data The original data.
     * @param leafIdentifier The leaf identifier.
     * @param internalGroupIndex The index of this leaf in the data array.
     * @param atomStartIndex The index of the first atom that belong to this leaf.
     * @param atomEndIndex The index of the last atom that belong to this leaf.
     */
    MmtfAminoAcid(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        super(data, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
    }

    /**
     * A copy constructor that passes all attributes of the given {@link MmtfLeafSubstructure} to a new instance.
     *
     * @param mmtfLeafSubstructure The {@link MmtfLeafSubstructure} to copy.
     */
    private MmtfAminoAcid(MmtfLeafSubstructure mmtfLeafSubstructure) {
        super(mmtfLeafSubstructure);
    }

    @Override
    public String flatToString() {
        return "AminoAcid ("+getThreeLetterCode()+") "+getIdentifier();
    }

    @Override
    public String toString() {
        return flatToString();
    }

    @Override
    public AminoAcid getCopy() {
        return new MmtfAminoAcid(this);
    }

}
