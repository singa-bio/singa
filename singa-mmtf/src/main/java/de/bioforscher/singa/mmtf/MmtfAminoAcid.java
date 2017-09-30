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

    MmtfAminoAcid(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        super(data, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
    }

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
