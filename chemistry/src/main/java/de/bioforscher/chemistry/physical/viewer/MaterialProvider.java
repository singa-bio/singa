package de.bioforscher.chemistry.physical.viewer;

import de.bioforscher.chemistry.descriptive.elements.Element;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * Created by Christoph on 05/12/2016.
 */
public class MaterialProvider {

    public static PhongMaterial CARBON = crateMaterialFromColor(Color.LIGHTGRAY);
    public static PhongMaterial NITROGEN = crateMaterialFromColor(Color.CORNFLOWERBLUE);
    public static PhongMaterial OXYGEN = crateMaterialFromColor(Color.INDIANRED);
    public static PhongMaterial HYDROGEN = crateMaterialFromColor(Color.LIGHTSKYBLUE);
    public static PhongMaterial OTHER = crateMaterialFromColor(Color.CORAL);

    public static PhongMaterial getDefaultMaterialForElement(Element element) {
        switch (element.getSymbol()) {
            case "C": return CARBON;
            case "N": return NITROGEN;
            case "O": return OXYGEN;
            case "H": return HYDROGEN;
            default: return OTHER;
        }
    }

    public static PhongMaterial crateMaterialFromColor(Color color) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color.darker());
        material.setSpecularColor(color.brighter());
        return material;
    }

}
