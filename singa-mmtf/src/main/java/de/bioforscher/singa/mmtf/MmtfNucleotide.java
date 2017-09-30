package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Nucleotide;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

/**
 * The implementation of {@link Nucleotide}s for mmtf structures.
 *
 * @author cl
 */
public class MmtfNucleotide extends MmtfLeafSubstructure implements Nucleotide {

    MmtfNucleotide(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        super(data, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
    }

    private MmtfNucleotide(MmtfLeafSubstructure mmtfLeafSubstructure) {
        super(mmtfLeafSubstructure);
    }

    @Override
    public String flatToString() {
        return "Nucleotide ("+getThreeLetterCode()+") "+getIdentifier();
    }

    @Override
    public String toString() {
        return flatToString();
    }

    @Override
    public Nucleotide getCopy() {
        return new MmtfNucleotide(this);
    }
}
