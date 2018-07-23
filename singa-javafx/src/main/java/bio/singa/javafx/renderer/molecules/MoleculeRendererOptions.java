package bio.singa.javafx.renderer.molecules;


import javafx.scene.paint.Color;

/**
 * @author cl
 */
public class MoleculeRendererOptions {

    private final Color defaultColor = Color.DARKOLIVEGREEN;
    private Color hydrogenColor = Color.CORNFLOWERBLUE;
    private Color carbonColor = Color.SLATEGRAY;
    private Color nitrogenColor = Color.CADETBLUE;
    private Color oxygenColor = Color.INDIANRED;
    private Color sulfurColor = Color.GOLDENROD;
    private Color phosphorusColor = Color.CORAL;

    public Color getHydrogenColor() {
        return hydrogenColor;
    }

    public void setHydrogenColor(Color hydrogenColor) {
        this.hydrogenColor = hydrogenColor;
    }

    public Color getCarbonColor() {
        return carbonColor;
    }

    public void setCarbonColor(Color carbonColor) {
        this.carbonColor = carbonColor;
    }

    public Color getNitrogenColor() {
        return nitrogenColor;
    }

    public void setNitrogenColor(Color nitrogenColor) {
        this.nitrogenColor = nitrogenColor;
    }

    public Color getOxygenColor() {
        return oxygenColor;
    }

    public void setOxygenColor(Color oxygenColor) {
        this.oxygenColor = oxygenColor;
    }

    public Color getSulfurColor() {
        return sulfurColor;
    }

    public void setSulfurColor(Color sulfurColor) {
        this.sulfurColor = sulfurColor;
    }

    public Color getPhosphorusColor() {
        return phosphorusColor;
    }

    public void setPhosphorusColor(Color phosphorusColor) {
        this.phosphorusColor = phosphorusColor;
    }

    public Color getColorForElement(String elementSymbol) {
        switch (elementSymbol) {
            case "H":
                return hydrogenColor;
            case "C":
                return carbonColor;
            case "N":
                return nitrogenColor;
            case "O":
                return oxygenColor;
            case "S":
                return sulfurColor;
            case "P":
                return phosphorusColor;
            default:
                return defaultColor;
        }
    }

}
