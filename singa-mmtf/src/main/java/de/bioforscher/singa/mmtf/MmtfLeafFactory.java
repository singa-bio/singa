package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.Optional;

/**
 * This is a static factory creating the three kinds of leafs used in mmtf structures.
 *
 * @author cl
 */
class MmtfLeafFactory {

    /**
     * Prevent external instantiation.
     */
    private MmtfLeafFactory() {

    }

    /**
     * Creates a new instance of {@link LeafSubstructure} using the correct implementation.
     *
     * @param data The original data.
     * @param leafIdentifier The leaf identifier.
     * @param internalGroupIndex The index of this leaf in the data array.
     * @param atomStartIndex The index of the first atom that belong to this leaf.
     * @param atomEndIndex The index of the last atom that belong to this leaf.
     * @return A instance of {@link LeafSubstructure}.
     */
    static LeafSubstructure createLeaf(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        final String threeLetterCode = data.getGroupName(data.getGroupTypeIndices()[internalGroupIndex]);
        Optional<AminoAcidFamily> aminoAcidFamily = AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(threeLetterCode);
        if (aminoAcidFamily.isPresent()) {
            return new MmtfAminoAcid(data, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
        }
        Optional<NucleotideFamily> nucleotideFamily = NucleotideFamily.getNucleotideByThreeLetterCode(threeLetterCode);
        if (nucleotideFamily.isPresent()) {
            return new MmtfNucleotide(data, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
        }
        return new MmtfLigand(data, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
    }

}
