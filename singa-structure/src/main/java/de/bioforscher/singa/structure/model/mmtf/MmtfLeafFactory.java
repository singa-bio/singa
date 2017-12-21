package de.bioforscher.singa.structure.model.mmtf;

import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.families.LigandFamily;
import de.bioforscher.singa.structure.model.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.HashMap;
import java.util.Optional;

/**
 * This is a static factory creating the three kinds of leafs used in mmtf structures.
 *
 * @author cl
 */
class MmtfLeafFactory {

    /**
     * A cache containing the already seen ligand families.
     */
    private static HashMap<String, LigandFamily> ligandFamilyCache = new HashMap<>();

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
    static MmtfLeafSubstructure<?> createLeaf(StructureDataInterface data, byte[] bytes, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        final String threeLetterCode = data.getGroupName(data.getGroupTypeIndices()[internalGroupIndex]);
        Optional<AminoAcidFamily> aminoAcidFamily = AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(threeLetterCode);
        if (aminoAcidFamily.isPresent()) {
            return new MmtfAminoAcid(data, bytes, aminoAcidFamily.get(), leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
        }
        Optional<NucleotideFamily> nucleotideFamily = NucleotideFamily.getNucleotideByThreeLetterCode(threeLetterCode);
        if (nucleotideFamily.isPresent()) {
            return new MmtfNucleotide(data, bytes, nucleotideFamily.get(), leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
        }
        LigandFamily ligandFamily = getLigandFamily(threeLetterCode);
        return new MmtfLigand(data, bytes, ligandFamily, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
    }

    private static LigandFamily getLigandFamily(String threeLetterCode) {
        if (ligandFamilyCache.containsKey(threeLetterCode)) {
            return ligandFamilyCache.get(threeLetterCode);
        }
        LigandFamily ligandFamily = new LigandFamily(threeLetterCode);
        ligandFamilyCache.put(threeLetterCode, ligandFamily);
        return ligandFamily;
    }

}
