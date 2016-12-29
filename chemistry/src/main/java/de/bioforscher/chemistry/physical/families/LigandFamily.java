package de.bioforscher.chemistry.physical.families;

import de.bioforscher.chemistry.physical.model.StructuralFamily;

/**
 * Created by leberech on 08/12/16.
 */
public enum LigandFamily implements StructuralFamily {

    UNKNOWN("*", "HET");


    private String oneLetterCode;
    private String threeLetterCode;

    LigandFamily(String oneLetterCode, String threeLetterCode) {
        this.oneLetterCode = oneLetterCode;
        this.threeLetterCode = threeLetterCode;
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
