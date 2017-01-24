package de.bioforscher.chemistry.physical.families;

import de.bioforscher.chemistry.physical.model.StructuralFamily;

import java.util.EnumSet;

/**
 * Created by fkaiser on 1/23/17.
 */
public enum MatcherFamily implements StructuralFamily {

    ALL(EnumSet.allOf(AminoAcidFamily.class), "*", "ALL");

    MatcherFamily(EnumSet<AminoAcidFamily> members, String oneLetterCode, String threeLetterCode) {
        this.members = members;
        this.oneLetterCode = oneLetterCode;
        this.threeLetterCode = threeLetterCode;
    }

    private EnumSet<AminoAcidFamily> members;
    private String oneLetterCode;
    private String threeLetterCode;

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
