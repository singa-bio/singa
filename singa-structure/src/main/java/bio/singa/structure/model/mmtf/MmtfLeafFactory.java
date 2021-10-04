package bio.singa.structure.model.mmtf;

import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
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
    private static final HashMap<String, StructuralFamily> ligandFamilyCache = new HashMap<>();

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
    static MmtfLeafSubstructure createLeaf(StructureDataInterface data, byte[] bytes, PdbLeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        final String threeLetterCode = data.getGroupName(data.getGroupTypeIndices()[internalGroupIndex]);
        Optional<StructuralFamily> aminoAcidFamily = StructuralFamilies.AminoAcids.get(threeLetterCode);
        if (aminoAcidFamily.isPresent()) {
            int[] secondaryStructureCodes = data.getSecStructList();
            MmtfSecondaryStructure secondaryStructure;
            if (secondaryStructureCodes.length < internalGroupIndex) {
                secondaryStructure = MmtfSecondaryStructure.UNDEFINED;
            } else {
                secondaryStructure = MmtfSecondaryStructure.getByMmtfCode(secondaryStructureCodes[internalGroupIndex]);
            }
            return new MmtfAminoAcid(data, bytes, aminoAcidFamily.get(), secondaryStructure, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
        }
        Optional<StructuralFamily> nucleotideFamily = StructuralFamilies.Nucleotides.get(threeLetterCode);
        if (nucleotideFamily.isPresent()) {
            return new MmtfNucleotide(data, bytes, nucleotideFamily.get(), leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
        }
        StructuralFamily ligandFamily = getLigandFamily(threeLetterCode);
        return new MmtfLigand(data, bytes, ligandFamily, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
    }

    private static StructuralFamily getLigandFamily(String threeLetterCode) {
        if (ligandFamilyCache.containsKey(threeLetterCode)) {
            return ligandFamilyCache.get(threeLetterCode);
        }
        StructuralFamily ligandFamily = new StructuralFamily("?", threeLetterCode);
        ligandFamilyCache.put(threeLetterCode, ligandFamily);
        return ligandFamily;
    }

}
