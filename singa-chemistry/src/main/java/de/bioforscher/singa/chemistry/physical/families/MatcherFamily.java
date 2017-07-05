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
     * The following types are grouped according to
     * <p>
     * <pre>
     *      Gutteridge, A. and Thornton, J. M.:
     *      Understanding nature's catalytic toolkit Trends in biochemical sciences, Elsevier, 2005, 30, 622-629.
     * </pre>
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
            AminoAcidFamily.TRYPTOPHAN, AminoAcidFamily.VALINE), "o", "OTH"),
    /**
     * The following ypes are grouped according to functional chemical groups:
     * <p>
     * <pre>
     *      aromatic (a)             F,Y,W
     *      negatively charged (n)   D,E
     *      positively charged (p)   K,R,H
     *      polar, uncharged (o)     P,N,Q,C,T,S
     *      nonpolar, aliphatic (i)  G,A,V,L,M,I
     * </pre>
     * <p>
     */
    FUNCTIONAL_AROMATIC(EnumSet.of(AminoAcidFamily.PHENYLALANINE, AminoAcidFamily.TYROSINE, AminoAcidFamily.TRYPTOPHAN), "a", "ARO"),
    FUNCTIONAL_NEGATIVE(EnumSet.of(AminoAcidFamily.ASPARTIC_ACID, AminoAcidFamily.GLUTAMIC_ACID), "e", "NEG"),
    FUNCTIONAL_POSITIVE(EnumSet.of(AminoAcidFamily.LYSINE, AminoAcidFamily.ARGININE, AminoAcidFamily.HISTIDINE), "p", "POS"),
    FUNCTIONAL_POLAR(EnumSet.of(AminoAcidFamily.PROLINE, AminoAcidFamily.ASPARAGINE, AminoAcidFamily.GLUTAMINE,
            AminoAcidFamily.CYSTEINE, AminoAcidFamily.THREONINE, AminoAcidFamily.SERINE), "p", "POL"),
    FUNCTIONAL_UNPOLAR(EnumSet.of(AminoAcidFamily.GLYCINE, AminoAcidFamily.ALANINE, AminoAcidFamily.VALINE,
            AminoAcidFamily.LEUCINE, AminoAcidFamily.METHIONINE, AminoAcidFamily.ISOLEUCINE), "u", "UPO");


    public static EnumSet<MatcherFamily> GUTTERIDGE = EnumSet.of(GUTTERIDGE_AMIDE, GUTTERIDGE_AMINE,
            GUTTERIDGE_CARBOXYLATE, GUTTERIDGE_GUANIDIUM, GUTTERIDGE_HYDROXYL, GUTTERIDGE_IMIDAZOLE, GUTTERIDGE_OTHERS,
            GUTTERIDGE_THIOL);

    public static EnumSet<MatcherFamily> FUNCTIONAL = EnumSet.of(FUNCTIONAL_AROMATIC, FUNCTIONAL_NEGATIVE,
            FUNCTIONAL_POSITIVE, FUNCTIONAL_POLAR, FUNCTIONAL_UNPOLAR);

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
