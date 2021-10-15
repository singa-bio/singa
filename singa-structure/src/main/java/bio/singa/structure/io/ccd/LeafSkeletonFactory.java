package bio.singa.structure.io.ccd;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.core.utility.Pair;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.general.LeafSkeleton;
import bio.singa.structure.model.interfaces.LigandType;
import org.rcsb.cif.model.Block;
import org.rcsb.cif.model.Category;
import org.rcsb.cif.model.CifFile;
import org.rcsb.cif.model.text.TextColumn;

import java.util.HashMap;
import java.util.Map;

public class LeafSkeletonFactory {

    private CcdParsingBehavior ccdParsingBehavior;
    private CifFile cifFile;
    private LeafSkeleton leafSkeleton;

    private String threeLetterCode;

    private Map<String, LeafSkeleton> leafSkeletonCache;

    public LeafSkeletonFactory(CcdParsingBehavior ccdParsingBehavior) {
        this.ccdParsingBehavior = ccdParsingBehavior;
        leafSkeletonCache = new HashMap<>();
    }

    public synchronized LeafSkeleton  getLeafSkeleton(String identifier) {
        if (leafSkeletonCache.containsKey(identifier)) {
            return leafSkeletonCache.get(identifier);
        }
        // create new skeleton
        initializeNewLeafSkeleton(identifier);
        // get additional information
        cifFile = ccdParsingBehavior.getCcdInformation(identifier);
        // return if no cif file can (should not) be found
        if (cifFile == null) {
            determineStructuralFamilyWithoutAdditionalInformation(identifier);
            return leafSkeleton;
        }
        Block firstBlock = cifFile.getBlocks().iterator().next();
        extractMetaInformation(firstBlock);
        extractBondInformation(firstBlock);
        return leafSkeleton;
    }

    private void initializeNewLeafSkeleton(String identifier) {
        leafSkeleton = new LeafSkeleton();
        threeLetterCode = identifier;
        leafSkeletonCache.put(identifier, leafSkeleton);
        leafSkeleton.setThreeLetterCode(identifier);
    }

    private void determineStructuralFamilyWithoutAdditionalInformation(String threeLetterCode) {
        if (StructuralFamilies.Nucleotides.isNucleotide(threeLetterCode)) {
            leafSkeleton.setLigandType(LigandType.NUCLEIC_ACID);
            leafSkeleton.setStructuralFamily(StructuralFamilies.Nucleotides.getOrUnknown(leafSkeleton.getThreeLetterCode()));
        } else if (StructuralFamilies.AminoAcids.isAminoAcid(threeLetterCode)) {
            leafSkeleton.setLigandType(LigandType.PROTEIN);
            leafSkeleton.setStructuralFamily(StructuralFamilies.AminoAcids.getOrUnknown(leafSkeleton.getThreeLetterCode()));
        } else {
            leafSkeleton.setLigandType(LigandType.UNKNOWN);
            leafSkeleton.setStructuralFamily(new StructuralFamily("", threeLetterCode));
        }
    }

    private void extractMetaInformation(Block block) {
        Category chemCompCategory = block.getCategory("chem_comp");
        // one letter code
        String oneLetterCode = chemCompCategory.getColumn("one_letter_code").getStringData(0);
        leafSkeleton.setStructuralFamily(new StructuralFamily(oneLetterCode, threeLetterCode));
        // ligand type
        LigandType ligandType = LigandType.getLigandTypeByPdbxTerm(chemCompCategory.getColumn("pdbx_type").getStringData(0));
        leafSkeleton.setLigandType(ligandType);
        // parent
        String parent = chemCompCategory.getColumn("mon_nstd_parent_comp_id").getStringData(0);
        leafSkeleton.setParent(parent);
        // name
        String name = chemCompCategory.getColumn("name").getStringData(0);
        leafSkeleton.setName(name);

        Category descriptorCategory = block.getCategory("pdbx_chem_comp_descriptor");
        TextColumn descriptorIdColumn = ((TextColumn) descriptorCategory.getColumn("type"));
        TextColumn descriptorContentColumn = ((TextColumn) descriptorCategory.getColumn("descriptor"));

        // inchi
        for (int row = 0; row < descriptorCategory.getRowCount(); row++) {
            if (descriptorIdColumn.get(row).equals("InChI")) {
                String inchi = descriptorContentColumn.get(row);
                leafSkeleton.setInchi(inchi);
                break;
            }
        }
    }

    private void extractBondInformation(Block block) {
        Category bondCategory = block.getCategory("chem_comp_bond");
        if (!bondCategory.isDefined()) {
            return;
        }
        TextColumn firstAtomColumn = ((TextColumn) bondCategory.getColumn("atom_id_1"));
        TextColumn secondAtomColumn = ((TextColumn) bondCategory.getColumn("atom_id_2"));
        TextColumn bondOrderColumn = ((TextColumn) bondCategory.getColumn("value_order"));
        for (int row = 0; row < bondCategory.getRowCount(); row++) {
            String firstAtomName = firstAtomColumn.get(row);
            String secondAtomName = secondAtomColumn.get(row);
            CovalentBondType bondType = CovalentBondType.getBondForCifString(bondOrderColumn.get(row));
            leafSkeleton.addBond(new Pair<>(firstAtomName, secondAtomName), bondType);
        }
    }

}
