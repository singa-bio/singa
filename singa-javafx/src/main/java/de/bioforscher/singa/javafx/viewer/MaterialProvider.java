package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.structure.elements.Element;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.families.StructuralFamily;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * @author cl
 */
public class MaterialProvider {

    public static PhongMaterial CARBON = crateMaterialFromColor(Color.LIGHTGRAY);
    public static PhongMaterial NITROGEN = crateMaterialFromColor(Color.CORNFLOWERBLUE);
    public static PhongMaterial OXYGEN = crateMaterialFromColor(Color.INDIANRED);
    public static PhongMaterial HYDROGEN = crateMaterialFromColor(Color.LIGHTSKYBLUE);
    public static PhongMaterial OTHER_ELEMENT = crateMaterialFromColor(Color.GREEN);

    public static PhongMaterial NUCLEOTIDE = NITROGEN;
    public static PhongMaterial AMINOACID = OXYGEN;
    public static PhongMaterial OTHER_TYPE = crateMaterialFromColor(Color.DARKOLIVEGREEN);

    public static PhongMaterial getDefaultMaterialForElement(Element element) {
        switch (element.getSymbol()) {
            case "C": return CARBON;
            case "N": return NITROGEN;
            case "O": return OXYGEN;
            case "H": return HYDROGEN;
            default: return OTHER_ELEMENT;
        }
    }

    public static PhongMaterial getMaterialForType(StructuralFamily structuralFamily) {
        if (structuralFamily instanceof NucleotideFamily) {
            return NUCLEOTIDE;
        } else if (structuralFamily instanceof AminoAcidFamily) {
            return AMINOACID;
        } else {
            return OTHER_TYPE;
        }
    }

    public static PhongMaterial crateMaterialFromColor(Color color) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color.darker());
        material.setSpecularColor(color.brighter());
        return material;
    }

}
