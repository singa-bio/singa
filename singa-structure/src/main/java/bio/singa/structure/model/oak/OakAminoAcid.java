package bio.singa.structure.model.oak;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.identifiers.LeafIdentifier;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Atom;

import java.util.Optional;

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

    @Override
    public Vector3D getPosition() {
        Optional<Atom> optionalAlphaCarbon = getAtomByName(AtomName.CA.getName());
        return optionalAlphaCarbon.map(Atom::getPosition).orElseGet(super::getPosition);
    }
}
