package bio.singa.structure.model.cif;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.core.utility.Pair;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.general.LeafSkeleton;
import bio.singa.structure.model.pdb.PdbAminoAcid;
import bio.singa.structure.model.pdb.PdbLeafSubstructure;
import bio.singa.structure.model.pdb.PdbLigand;
import bio.singa.structure.model.pdb.PdbNucleotide;

import java.util.Map;

public class CifLeafSubstructureFactory {

    public static CifLeafSubstructure createLeafSubstructure(LeafSkeleton skeleton, CifLeafIdentifier identifier) {
        switch (skeleton.getAssignedFamily()) {
            case AMINO_ACID:
            case MODIFIED_AMINO_ACID: {
                CifAminoAcid aminoAcid = new CifAminoAcid(identifier);
                aminoAcid.setFamily(skeleton.getStructuralFamily());
                if (!skeleton.getStructuralFamily().getThreeLetterCode().equals(skeleton.getThreeLetterCode())) {
                    aminoAcid.setDivergingThreeLetterCode(skeleton.getThreeLetterCode());
                }
                return aminoAcid;
            }
            case NUCLEOTIDE:
            case MODIFIED_NUCLEOTIDE: {
                CifNucleotide nucleotide = new CifNucleotide(identifier);
                nucleotide.setFamily(skeleton.getStructuralFamily());
                if (!skeleton.getStructuralFamily().getThreeLetterCode().equals(skeleton.getThreeLetterCode())) {
                    nucleotide.setDivergingThreeLetterCode(skeleton.getThreeLetterCode());
                }
                return nucleotide;
            }
            default: {
                CifLigand ligand = new CifLigand(identifier);
                ligand.setFamily(skeleton.getStructuralFamily());
                ligand.setName(skeleton.getName());
                return ligand;
            }
        }

    }

}
