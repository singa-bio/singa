package de.bioforscher.chemistry.descriptive;


import de.bioforscher.core.identifier.ECNumber;

public class EnzymeAnnotations {

    private ECNumber ecNumber;
    private String variant;

    public ECNumber getEcNumber() {
        return ecNumber;
    }

    public void setEcNumber(ECNumber ecNumber) {
        this.ecNumber = ecNumber;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

}
