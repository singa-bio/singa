package bio.singa.structure.model.oak;

import bio.singa.structure.model.families.NucleotideFamily;
import bio.singa.features.identifiers.LeafIdentifier;
import bio.singa.structure.model.interfaces.Nucleotide;

/**
 * @author cl
 */
public class OakNucleotide extends OakLeafSubstructure<NucleotideFamily> implements Nucleotide {

    public OakNucleotide(LeafIdentifier leafIdentifier, NucleotideFamily family) {
        super(leafIdentifier, family);
    }


    public OakNucleotide(LeafIdentifier identifer, NucleotideFamily nucleotideFamily, String threeLetterCode) {
        super(identifer, nucleotideFamily, threeLetterCode);
    }

    public OakNucleotide(OakNucleotide oakNucleotide) {
        super(oakNucleotide);
    }

    @Override
    public OakNucleotide getCopy() {
        return new OakNucleotide(this);
    }

}
