package de.bioforscher.singa.structure.model.families;

import java.util.EnumSet;

import static de.bioforscher.singa.structure.model.families.AminoAcidFamily.*;

/**
 * This is a {@link StructuralFamily} that groups others. Using this, one can employ the definition of exchanges to be
 * allowed for a group of residues.
 *
 * @author fk
 */
public enum MatcherFamily implements StructuralFamily<MatcherFamily> {

    ALL(EnumSet.allOf(AminoAcidFamily.class), "*", "ALL"),


    GUTTERIDGE_IMIDAZOLE(EnumSet.of(HISTIDINE), "i", "IMI"),
    GUTTERIDGE_AMINE(EnumSet.of(LYSINE), "n", "AMN"),
    GUTTERIDGE_CARBOXYLATE(EnumSet.of(ASPARTIC_ACID, GLUTAMIC_ACID), "n", "AMN"),
    GUTTERIDGE_AMIDE(EnumSet.of(ASPARAGINE, GLUTAMINE), "d", "AMD"),
    GUTTERIDGE_HYDROXYL(EnumSet.of(SERINE, THREONINE, TYROSINE), "h", "HYD"),
    GUTTERIDGE_THIOL(EnumSet.of(CYSTEINE), "t", "THI"),
    GUTTERIDGE_GUANIDIUM(EnumSet.of(ARGININE), "g", "GND"),
    GUTTERIDGE_OTHERS(EnumSet.of(ALANINE, GLYCINE, ISOLEUCINE,
            LEUCINE, METHIONINE, PHENYLALANINE, PROLINE,
            TRYPTOPHAN, VALINE), "o", "OTH"),

    FUNCTIONAL_AROMATIC(EnumSet.of(PHENYLALANINE, TYROSINE, TRYPTOPHAN), "a", "ARO"),
    FUNCTIONAL_NEGATIVE(EnumSet.of(ASPARTIC_ACID, GLUTAMIC_ACID), "e", "NEG"),
    FUNCTIONAL_POSITIVE(EnumSet.of(LYSINE, ARGININE, HISTIDINE), "p", "POS"),
    FUNCTIONAL_POLAR(EnumSet.of(PROLINE, ASPARAGINE, GLUTAMINE,
            CYSTEINE, THREONINE, SERINE), "p", "POL"),
    FUNCTIONAL_UNPOLAR(EnumSet.of(GLYCINE, ALANINE, VALINE,
            LEUCINE, METHIONINE, ISOLEUCINE), "u", "UPO");

    /**
     * The following types are grouped according to
     * <pre>
     *      Gutteridge, A. and Thornton, J. M.:
     *      Understanding nature's catalytic toolkit Trends in biochemical sciences, Elsevier, 2005, 30, 622-629.
     * </pre>
     */
    public static EnumSet<MatcherFamily> GUTTERIDGE = EnumSet.of(GUTTERIDGE_AMIDE, GUTTERIDGE_AMINE,
            GUTTERIDGE_CARBOXYLATE, GUTTERIDGE_GUANIDIUM, GUTTERIDGE_HYDROXYL, GUTTERIDGE_IMIDAZOLE, GUTTERIDGE_OTHERS,
            GUTTERIDGE_THIOL);

    /**
     * The following ypes are grouped according to functional chemical groups:
     * <pre>
     *      aromatic (a)             F,Y,W
     *      negatively charged (n)   D,E
     *      positively charged (p)   K,R,H
     *      polar, uncharged (o)     P,N,Q,C,T,S
     *      nonpolar, aliphatic (i)  G,A,V,L,M,I
     * </pre>
     */
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
        return members;
    }

    @Override
    public String getOneLetterCode() {
        return oneLetterCode;
    }

    @Override
    public String getThreeLetterCode() {
        return threeLetterCode;
    }
}
