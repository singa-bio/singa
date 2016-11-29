package de.bioforscher.chemistry.descriptive.molecules;


import javafx.scene.paint.Color;

/**
 * Created by Christoph on 24/11/2016.
 */
public class MoleculeRendererOptions {

    private Color hydrogenColor = Color.CORNFLOWERBLUE;
    private Color carbonColor = Color.SLATEGRAY;
    private Color nitrogenColor = Color.CADETBLUE;
    private Color oxygenColor = Color.INDIANRED;
    private Color sulfurColor = Color.GOLDENROD;
    private Color phosphorusColor = Color.CORAL;
    private Color defaultColor = Color.DARKOLIVEGREEN;

    public Color getHydrogenColor() {
        return this.hydrogenColor;
    }

    public void setHydrogenColor(Color hydrogenColor) {
        this.hydrogenColor = hydrogenColor;
    }

    public Color getCarbonColor() {
        return this.carbonColor;
    }

    public void setCarbonColor(Color carbonColor) {
        this.carbonColor = carbonColor;
    }

    public Color getNitrogenColor() {
        return this.nitrogenColor;
    }

    public void setNitrogenColor(Color nitrogenColor) {
        this.nitrogenColor = nitrogenColor;
    }

    public Color getOxygenColor() {
        return this.oxygenColor;
    }

    public void setOxygenColor(Color oxygenColor) {
        this.oxygenColor = oxygenColor;
    }

    public Color getSulfurColor() {
        return this.sulfurColor;
    }

    public void setSulfurColor(Color sulfurColor) {
        this.sulfurColor = sulfurColor;
    }

    public Color getPhosphorusColor() {
        return this.phosphorusColor;
    }

    public void setPhosphorusColor(Color phosphorusColor) {
        this.phosphorusColor = phosphorusColor;
    }

    public Color getColorForElement(String elementSymbol) {
        switch (elementSymbol) {
            case "H": return this.hydrogenColor;
            case "C": return this.carbonColor;
            case "N": return this.nitrogenColor;
            case "O": return this.oxygenColor;
            case "S": return this.sulfurColor;
            case "P": return this.phosphorusColor;
            default: return this.defaultColor;
        }
    }

}
