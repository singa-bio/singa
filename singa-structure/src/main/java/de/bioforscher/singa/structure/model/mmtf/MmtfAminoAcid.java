package de.bioforscher.singa.structure.model.mmtf;

import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import org.rcsb.mmtf.api.StructureDataInterface;

/**
 * The implementation of {@link AminoAcid}s for mmtf structures.
 *
 * @author cl
 */
public class MmtfAminoAcid extends MmtfLeafSubstructure<AminoAcidFamily> implements AminoAcid {

    /**
     * Creates a new {@link MmtfAminoAcid}.
     *
     * @param data The original data.
     * @param family The leaf family (e.g. {@link AminoAcidFamily#ALANINE}).
     * @param leafIdentifier The leaf identifier.
     * @param internalGroupIndex The index of this leaf in the data array.
     * @param atomStartIndex The index of the first atom that belong to this leaf.
     * @param atomEndIndex The index of the last atom that belong to this leaf.
     */
    MmtfAminoAcid(StructureDataInterface data, byte[] bytes, AminoAcidFamily family, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        super(data, bytes, family, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
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
    public boolean isAnnotatedAsHeteroAtom() {
        return false;
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
