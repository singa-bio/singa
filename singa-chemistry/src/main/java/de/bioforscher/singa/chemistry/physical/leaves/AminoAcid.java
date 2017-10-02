package de.bioforscher.singa.chemistry.physical.leaves;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.AtomName;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;

/**
 * A AminoAcid is a grouping element that should only contain atoms. Each and every AminoAcid has a associate ResidueType,
 * that determines the amino acid (and the overarching features).
 *
 * @author cl
 */
public class AminoAcid extends LeafSubstructure<AminoAcid, AminoAcidFamily> {

    private final boolean modified;
    private final String modifiedName;

    /**
     * Creates a new AminoAcid.
     *
     * @param leafIdentifier The identifier.
     * @param family The {@link AminoAcidFamily}.
     */
    public AminoAcid(LeafIdentifier leafIdentifier, AminoAcidFamily family) {
        super(leafIdentifier, family);
        this.modified = false;
        this.modifiedName = null;
    }

    /**
     * Creates a new AminoAcid with a default leaf identifier with the given serial and family type.
     *
     * @param leafsSerial The leaf serial.
     * @param family The {@link AminoAcidFamily}.
     */
    public AminoAcid(int leafsSerial, AminoAcidFamily family) {
        this(new LeafIdentifier(leafsSerial), family);
    }

    /**
     * Creates a new AminoAcid marked as modified with a identifier and {@link AminoAcidFamily}.
     *
     * @param leafIdentifier The identifier.
     * @param family The {@link AminoAcidFamily}.
     * @param modifiedName The name of the modified amino acid.
     */
    public AminoAcid(LeafIdentifier leafIdentifier, AminoAcidFamily family, String modifiedName) {
        super(leafIdentifier, family);
        this.modified = true;
        this.modifiedName = modifiedName;
    }

    /**
     * This is a copy constructor. Creates a new amino acid with the same attributes as the given amino acid. This
     * also recursively creates copies of all the underlying substructures and atoms. The neighbours of this
     * substructure are NOT copied. Due to the nature of this operation it would be bad to keep a part of the relations
     * to the lifecycle of the substructure to copy. If you want to keep the neighbouring substructures, copy the
     * superordinate substructure that contains this substructure and it will also traverse and copy the neighbouring
     * substructures.
     *
     * @param aminoAcid The amino acid to copy
     */
    public AminoAcid(AminoAcid aminoAcid) {
        super(aminoAcid);
        this.modified = aminoAcid.modified;
        this.modifiedName = aminoAcid.modifiedName;
    }

    /**
     * Returns the one letter code of this AminoAcid.
     *
     * @return The one letter code of this AminoAcid.
     */
    public String getOneLetterCode() {
        return getFamily().getOneLetterCode();
    }

    /**
     * Returns the three letter code of this AminoAcid.
     *
     * @return The three letter code of this AminoAcid.
     */
    public String getThreeLetterCode() {
        return this.modified ? this.modifiedName : getFamily().getThreeLetterCode();
    }

    /**
     * Returns true, if this amino acid is no standard amino acid.
     *
     * @return true, if this amino acid is no standard amino acid.
     */
    public boolean isModified() {
        return this.modified;
    }

    /**
     * Returns the {@link AtomName#CA alpha carbon} (carbon with the side cain attached).
     *
     * @return The C alpha carbon.
     */
    public Atom getAlphaCarbon() {
        return getAtomByName(AtomName.CA);
    }

    /**
     * Returns the {@link AtomName#CB beta carbon} (first carbon of the side cain).
     *
     * @return The C beta carbon.
     */
    public Atom getBetaCarbon() {
        return getAtomByName(AtomName.CB);
    }

    /**
     * Returns the {@link AtomName#C backbone carbon} (carbon in the backbone).
     *
     * @return The backbone carbon.
     */
    public Atom getBackboneCarbon() {
        return getAtomByName(AtomName.C);
    }

    /**
     * Returns the {@link AtomName#N backbone nitrogen} (nitrogen in the backbone).
     *
     * @return The backbone nitrogen.
     */
    public Atom getBackboneNitrogen() {
        return getAtomByName(AtomName.N);
    }

    /**
     * Returns the {@link AtomName#O backbone oxygen} (oxygen in the backbone).
     *
     * @return The backbone oxygen.
     */
    public Atom getBackboneOxygen() {
        return getAtomByName(AtomName.O);
    }

    /**
     * Returns a copy of this AminoAcid.
     *
     * @return A copy of this AminoAcid.
     */
    @Override
    public AminoAcid getCopy() {
        return new AminoAcid(this);
    }

}
