package bio.singa.structure.model.oak;

import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.families.NucleotideFamily;
import bio.singa.features.identifiers.LeafIdentifier;
import bio.singa.structure.model.interfaces.Nucleotide;

/**
 * @author cl
 */
public class OakNucleotide extends OakLeafSubstructure<NucleotideFamily> implements Nucleotide {

    private boolean mutation;
    private NucleotideFamily wildTypeNucleotide;

    public OakNucleotide(LeafIdentifier leafIdentifier, NucleotideFamily family) {
        super(leafIdentifier, family);
    }

    public OakNucleotide(LeafIdentifier identifer, NucleotideFamily nucleotideFamily, String threeLetterCode) {
        super(identifer, nucleotideFamily, threeLetterCode);
    }

    public OakNucleotide(OakNucleotide oakNucleotide) {
        super(oakNucleotide);
    }

    public OakNucleotide(OakNucleotide oakNucleotide, LeafIdentifier leafIdentifier) {
        super(oakNucleotide, leafIdentifier);
    }

    @Override
    public OakNucleotide getCopy() {
        return new OakNucleotide(this);
    }

    public boolean isMutated() {
        return mutation;
    }

    public void setMutation(boolean mutation) {
        this.mutation = mutation;
    }

    public NucleotideFamily getWildTypeNucleotide() {
        return wildTypeNucleotide;
    }

    public void setWildTypeNucleotide(NucleotideFamily wildTypeNucleotide) {
        this.wildTypeNucleotide = wildTypeNucleotide;
    }

}
