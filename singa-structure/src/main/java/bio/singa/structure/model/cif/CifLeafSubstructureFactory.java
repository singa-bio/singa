package bio.singa.structure.model.cif;

import bio.singa.structure.model.general.LeafSkeleton;

public class CifLeafSubstructureFactory {

    public static CifLeafSubstructure createLeafSubstructure(LeafSkeleton skeleton, CifLeafIdentifier identifier) {
        switch (skeleton.getLigandType()) {
            case PROTEIN:
                CifAminoAcid aminoAcid = new CifAminoAcid(identifier);
                aminoAcid.setFamily(skeleton.getStructuralFamily());
                if (!skeleton.getStructuralFamily().getThreeLetterCode().equals(skeleton.getThreeLetterCode())) {
                    aminoAcid.setDivergingThreeLetterCode(skeleton.getThreeLetterCode());
                }
                return aminoAcid;
            case NUCLEIC_ACID:
                CifNucleotide nucleotide = new CifNucleotide(identifier);
                nucleotide.setFamily(skeleton.getStructuralFamily());
                if (!skeleton.getStructuralFamily().getThreeLetterCode().equals(skeleton.getThreeLetterCode())) {
                    nucleotide.setDivergingThreeLetterCode(skeleton.getThreeLetterCode());
                }
                return nucleotide;
            default: {
                CifLigand ligand = new CifLigand(identifier);
                ligand.setFamily(skeleton.getStructuralFamily());
                ligand.setName(skeleton.getName());
                return ligand;
            }
        }
    }

}
