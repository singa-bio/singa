package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.singa.chemistry.physical.interfaces.Nucleotide;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

/**
 * The implementation of {@link Nucleotide}s for mmtf structures.
 *
 * @author cl
 */
public class MmtfNucleotide extends MmtfLeafSubstructure<NucleotideFamily> implements Nucleotide {

    /**
     * Creates a new {@link MmtfNucleotide}.
     *
     * @param data The original data.
     * @param family The leaf family (e.g. {@link NucleotideFamily#GUANOSINE}).
     * @param leafIdentifier The leaf identifier.
     * @param internalGroupIndex The index of this leaf in the data array.
     * @param atomStartIndex The index of the first atom that belong to this leaf.
     * @param atomEndIndex The index of the last atom that belong to this leaf.
     */
    MmtfNucleotide(StructureDataInterface data, NucleotideFamily family, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        super(data, family, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
    }

    /**
     * A copy constructor that passes all attributes of the given {@link MmtfLeafSubstructure} to a new instance.
     *
     * @param mmtfLeafSubstructure The {@link MmtfLeafSubstructure} to copy.
     */
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
