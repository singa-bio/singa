package bio.singa.javafx.viewer;

import bio.singa.chemistry.model.elements.Element;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * @author cl
 */
public class MaterialProvider {

    public static final PhongMaterial CARBON = crateMaterialFromColor(Color.LIGHTGRAY);
    public static final PhongMaterial NITROGEN = crateMaterialFromColor(Color.CORNFLOWERBLUE);
    public static final PhongMaterial OXYGEN = crateMaterialFromColor(Color.INDIANRED);
    public static final PhongMaterial HYDROGEN = crateMaterialFromColor(Color.LIGHTSKYBLUE);
    public static final PhongMaterial OTHER_ELEMENT = crateMaterialFromColor(Color.GREEN);

    public static final PhongMaterial NUCLEOTIDE = NITROGEN;
    public static final PhongMaterial AMINOACID = OXYGEN;
    public static final PhongMaterial OTHER_TYPE = crateMaterialFromColor(Color.DARKOLIVEGREEN);

    public static PhongMaterial getDefaultMaterialForElement(Element element) {
        switch (element.getSymbol()) {
            case "C":
                return CARBON;
            case "N":
                return NITROGEN;
            case "O":
                return OXYGEN;
            case "H":
                return HYDROGEN;
            default:
                return OTHER_ELEMENT;
        }
    }

    public static PhongMaterial getMaterialForType(StructuralFamily structuralFamily) {
        if (StructuralFamilies.Nucleotides.isNucleotide(structuralFamily)) {
            return NUCLEOTIDE;
        } else if (StructuralFamilies.AminoAcids.isAminoAcid(structuralFamily)) {
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
