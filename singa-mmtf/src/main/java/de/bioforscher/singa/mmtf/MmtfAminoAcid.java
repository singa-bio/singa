package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.AminoAcid;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

/**
 * @author cl
 */
public class MmtfAminoAcid extends MmtfLeafSubstructure<AminoAcid> implements AminoAcid {

    public MmtfAminoAcid(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalIndex, int atomStartIndex, int atomEndIndex) {
        super(data, leafIdentifier, internalIndex, atomStartIndex, atomEndIndex);
    }

    @Override
    public String flatToString() {
        return "AminoAcid ("+getThreeLetterCode()+") "+getIdentifier();
    }

    @Override
    public String toString() {
        return flatToString();
    }
}
