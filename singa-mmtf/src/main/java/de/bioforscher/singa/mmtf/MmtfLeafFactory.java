package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.Optional;

/**
 * @author cl
 */
public class MmtfLeafFactory {

    private MmtfLeafFactory() {

    }

    static LeafSubstructure<?> createLeaf(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalIndex, int atomStartIndex, int atomEndIndex) {
        final String threeLetterCode = data.getGroupName(data.getGroupTypeIndices()[internalIndex]);
        Optional<AminoAcidFamily> aminoAcidFamily = AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(threeLetterCode);
        if (aminoAcidFamily.isPresent()) {
            return new MmtfAminoAcid(data, leafIdentifier, internalIndex, atomStartIndex, atomEndIndex);
        }
        Optional<NucleotideFamily> nucleotideFamily = NucleotideFamily.getNucleotideByThreeLetterCode(threeLetterCode);
        if (nucleotideFamily.isPresent()) {
            return new MmtfNucleotide(data, leafIdentifier, internalIndex, atomStartIndex, atomEndIndex);
        }
        return new MmtfLigand(data, leafIdentifier, internalIndex, atomStartIndex, atomEndIndex);
    }

}
