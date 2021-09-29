package bio.singa.structure.model.oak;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Atom;

import java.util.Optional;

/**
 * @author cl
 */
public class OakAminoAcid extends OakLeafSubstructure<AminoAcidFamily> implements AminoAcid {

    private boolean mutation;
    private AminoAcidFamily wildTypeResidue;

    public OakAminoAcid(PdbLeafIdentifier leafIdentifier, AminoAcidFamily family) {
        super(leafIdentifier, family);
    }

    public OakAminoAcid(PdbLeafIdentifier identifer, AminoAcidFamily aminoAcidFamily, String threeLetterCode) {
        super(identifer, aminoAcidFamily, threeLetterCode);
    }

    public OakAminoAcid(OakAminoAcid oakAminoAcid) {
        super(oakAminoAcid);
        mutation = oakAminoAcid.mutation;
        wildTypeResidue = oakAminoAcid.wildTypeResidue;
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

    public boolean isMutated() {
        return mutation;
    }

    public void setMutation(boolean mutation) {
        this.mutation = mutation;
    }

    public AminoAcidFamily getWildTypeResidue() {
        return wildTypeResidue;
    }

    public void setWildTypeResidue(AminoAcidFamily wildTypeResidue) {
        this.wildTypeResidue = wildTypeResidue;
    }

    @Override
    public String toString() {
        return super.toString() + (mutation ? "*" : "");
    }
}
