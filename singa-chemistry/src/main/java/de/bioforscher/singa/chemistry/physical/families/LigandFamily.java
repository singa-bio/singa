package de.bioforscher.singa.chemistry.physical.families;

import de.bioforscher.singa.chemistry.physical.model.StructuralFamily;

/**
 * @author cl
 */
public class LigandFamily implements StructuralFamily {

    private String oneLetterCode;
    private String threeLetterCode;

    public LigandFamily(String oneLetterCode, String threeLetterCode) {
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
