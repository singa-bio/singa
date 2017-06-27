package de.bioforscher.singa.chemistry.physical.families;

import de.bioforscher.singa.chemistry.physical.model.StructuralFamily;

import java.util.EnumSet;

/**
 * This is a {@link StructuralFamily} that groups others. Using this, one can employ the definition of exchanges to be
 * allowed for a group of residues.
 *
 * @author fk
 */
public enum MatcherFamily implements StructuralFamily<MatcherFamily> {

    ALL(EnumSet.allOf(AminoAcidFamily.class), "*", "ALL"),

    /**
     * The following types are according to Gutteridge, A. and Thornton, J. M.: Understanding
     * nature's catalytic toolkit Trends in biochemical sciences, Elsevier, 2005, 30, 622-629.
     */
    GUTTERIDGE_IMIDAZOLE(EnumSet.of(AminoAcidFamily.HISTIDINE), "i", "IMI"),
    GUTTERIDGE_AMINE(EnumSet.of(AminoAcidFamily.LYSINE), "n", "AMN"),
    GUTTERIDGE_CARBOXYLATE(EnumSet.of(AminoAcidFamily.ASPARTIC_ACID, AminoAcidFamily.GLUTAMIC_ACID), "n", "AMN"),
    GUTTERIDGE_AMIDE(EnumSet.of(AminoAcidFamily.ASPARAGINE, AminoAcidFamily.GLUTAMINE), "d", "AMD"),
    GUTTERIDGE_HYDROXYL(EnumSet.of(AminoAcidFamily.SERINE, AminoAcidFamily.THREONINE, AminoAcidFamily.TYROSINE), "h", "HYD"),
    GUTTERIDGE_THIOL(EnumSet.of(AminoAcidFamily.CYSTEINE), "t", "THI"),
    GUTTERIDGE_GUANIDIUM(EnumSet.of(AminoAcidFamily.ARGININE), "g", "GND"),
    GUTTERIDGE_OTHERS(EnumSet.of(AminoAcidFamily.ALANINE, AminoAcidFamily.GLYCINE, AminoAcidFamily.ISOLEUCINE,
            AminoAcidFamily.LEUCINE, AminoAcidFamily.METHIONINE, AminoAcidFamily.PHENYLALANINE, AminoAcidFamily.PROLINE,
            AminoAcidFamily.TRYPTOPHAN, AminoAcidFamily.VALINE), "o", "OTH");

    public static EnumSet<MatcherFamily> GUTTERIDGE = EnumSet.of(GUTTERIDGE_AMIDE, GUTTERIDGE_AMINE,
            GUTTERIDGE_CARBOXYLATE, GUTTERIDGE_GUANIDIUM, GUTTERIDGE_HYDROXYL, GUTTERIDGE_IMIDAZOLE, GUTTERIDGE_OTHERS,
            GUTTERIDGE_THIOL);

    private EnumSet<AminoAcidFamily> members;
    private String oneLetterCode;
    private String threeLetterCode;

    MatcherFamily(EnumSet<AminoAcidFamily> members, String oneLetterCode, String threeLetterCode) {
        this.members = members;
        this.oneLetterCode = oneLetterCode;
        this.threeLetterCode = threeLetterCode;
    }

    public EnumSet<AminoAcidFamily> getMembers() {
        return this.members;
    }

    @Override
    public String getOneLetterCode() {
        return this.oneLetterCode;
    }

    @Override
    public String getThreeLetterCode() {
        return this.threeLetterCode;
    }
}
