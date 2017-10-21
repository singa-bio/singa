package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.AminoAcid;

/**
 * @author cl
 */
public class OakAminoAcid extends OakLeafSubstructure<AminoAcidFamily> implements AminoAcid {

    public OakAminoAcid(LeafIdentifier leafIdentifier, AminoAcidFamily family) {
        super(leafIdentifier, family);
    }

    public OakAminoAcid(LeafIdentifier identifer, AminoAcidFamily aminoAcidFamily, String threeLetterCode) {
        super(identifer, aminoAcidFamily, threeLetterCode);
    }

    public OakAminoAcid(OakAminoAcid oakAminoAcid) {
        super(oakAminoAcid);
    }

    @Override
    public OakAminoAcid getCopy() {
        return new OakAminoAcid(this);
    }
}
