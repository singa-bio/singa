package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.structure.model.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.Nucleotide;

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
