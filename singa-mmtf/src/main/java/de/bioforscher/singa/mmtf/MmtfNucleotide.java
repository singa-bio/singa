package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Nucleotide;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

/**
 * @author cl
 */
public class MmtfNucleotide extends MmtfLeafSubstructure<Nucleotide> implements Nucleotide {

    public MmtfNucleotide(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalIndex, int atomStartIndex, int atomEndIndex) {
        super(data, leafIdentifier, internalIndex, atomStartIndex, atomEndIndex);
    }

    @Override
    public String flatToString() {
        return "Nucleotide ("+getThreeLetterCode()+") "+getIdentifier();
    }

    @Override
    public String toString() {
        return flatToString();
    }

}
