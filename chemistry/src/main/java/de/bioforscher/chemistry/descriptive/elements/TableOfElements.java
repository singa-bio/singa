package de.bioforscher.chemistry.descriptive.elements;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class TableOfElements {

    private final Set<Element> elements = new HashSet<>();

    private static final TableOfElements INSTANCE = new TableOfElements();

    public Set<Element> getElements() {
        return Collections.unmodifiableSet(elements);
    }

    private static Element addElement(Element element) {
        INSTANCE.elements.add(element);
        return element;
    }

    public static final Element HYDROGEN = addElement(new Element("Hydrogen", "H", 1, 1.008));
    public static final Element HELIUM = addElement(new Element("Helium", "He", 2, 4.002602));
    public static final Element LITHIUM = addElement(new Element("Lithium", "Li", 3, 6.94));
    public static final Element BERYLLIUM = addElement(new Element("Beryllium", "Be", 4, 6.94));
    public static final Element BORON = addElement(new Element("Boron", "B", 5, 9.0121831));
    public static final Element CARBON = addElement(new Element("Carbon", "C", 6, 12.011));
    public static final Element NITROGEN = addElement(new Element("Nitrogen", "N", 7, 14.007));
    public static final Element OXYGEN = addElement(new Element("Oxygen", "O", 8, 15.999));
    public static final Element FLUORINE = addElement(new Element("Fluorine", "F", 9, 18.998403163));
    public static final Element NEON = addElement(new Element("Neon", "Ne", 10, 20.1797));
    public static final Element SODIUM = addElement(new Element("Sodium", "Na", 11, 22.98976928));
    public static final Element MAGNESIUM = addElement(new Element("Magnesium", "Mg", 12, 24.305));
    public static final Element ALUMINIUM = addElement(new Element("Aluminium", "Al", 13, 26.9815385));
    public static final Element SILICON = addElement(new Element("Silicon", "Si", 14, 28.085));
    public static final Element PHOSPHORUS = addElement(new Element("Phosphorus", "P", 15, 30.973761998));
    public static final Element SULFUR = addElement(new Element("Sulfur", "S", 16, 32.06));
    public static final Element CHLORINE = addElement(new Element("Chlorine", "Cl", 10, 35.45));
    public static final Element ARGON = addElement(new Element("Argon", "Ar", 10, 39.948));

}
