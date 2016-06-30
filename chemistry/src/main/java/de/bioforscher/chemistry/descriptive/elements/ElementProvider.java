package de.bioforscher.chemistry.descriptive.elements;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This library provides Elements 1 (Hydrogen) to 111 Roentgenium with their names, symbols, atomic numbers, and atomic
 * mass.
 *
 * @author Christoph Leberecht
 * @version 1.0.0
 */
public final class ElementProvider {

    private static final ElementProvider INSTANCE = new ElementProvider();

    public static final Element HYDROGEN = addElement(new Element("Hydrogen", "H", 1, 1.008));
    public static final Element DEUTERIUM = addElement(HYDROGEN.asIsotope(2));
    public static final Element TRITIUM = addElement(HYDROGEN.asIsotope(3));
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
    public static final Element CHLORINE = addElement(new Element("Chlorine", "Cl", 17, 35.45));
    public static final Element POTASSIUM = addElement(new Element("Potassium", "K", 18, 39.948));
    public static final Element CALCIUM = addElement(new Element("Calcium", "Ca", 20, 40.078));
    public static final Element SCANDIUM = addElement(new Element("Scandium", "Sc", 21, 44.9559));
    public static final Element TITANIUM = addElement(new Element("Titanium", "Ti", 22, 47.867));
    public static final Element VANADIUM = addElement(new Element("Vanadium", "V", 23, 50.9415));
    public static final Element CHROMIUM = addElement(new Element("Chromium", "Cr", 24, 51.9961));
    public static final Element MANGANESE = addElement(new Element("Manganese", "Mg", 25, 54.938));
    public static final Element IRON = addElement(new Element("Iron", "Fe", 26, 55.845));
    public static final Element NICKEL = addElement(new Element("Nickel", "Ni", 27, 58.6934));
    public static final Element COBALT = addElement(new Element("Cobalt", "Co", 28, 58.9332));
    public static final Element COPPER = addElement(new Element("Copper", "Cu", 29, 63.546));
    public static final Element ZINC = addElement(new Element("Zinc", "Zi", 30, 65.39));
    public static final Element GALLIUM = addElement(new Element("Gallium", "Ga", 31, 69.723));
    public static final Element GERMANIUM = addElement(new Element("Germanium", "Ge", 32, 72.64));
    public static final Element ARSENIC = addElement(new Element("Arsenic", "As", 33, 74.9216));
    public static final Element SELENIUM = addElement(new Element("Selenium", "Se", 34, 78.96));
    public static final Element BROMINE = addElement(new Element("Bromine", "Br", 35, 79.904));
    public static final Element KRYPTON = addElement(new Element("Krypton", "Kr", 36, 83.8));
    public static final Element RUBIDIUM = addElement(new Element("Rubidium", "Rb", 37, 85.4678));
    public static final Element STRONTIUM = addElement(new Element("Strontium", "Sr", 38, 87.62));
    public static final Element YTTRIUM = addElement(new Element("Yttrium", "Y", 39, 88.9059));
    public static final Element ZIRCONIUM = addElement(new Element("Zirconium", "Zr", 40, 91.224));
    public static final Element NIOBIUM = addElement(new Element("Niobium", "Nb", 41, 92.9064));
    public static final Element MOLYBDENUM = addElement(new Element("Molybdenum", "Mo", 42, 95.94));
    public static final Element TECHNETIUM = addElement(new Element("Technetium", "Tc", 43, 98));
    public static final Element RUTHENIUM = addElement(new Element("Ruthenium", "Ru", 44, 101.07));
    public static final Element RHODIUM = addElement(new Element("Rhodium", "Rh", 45, 102.9055));
    public static final Element PALLADIUM = addElement(new Element("Palladium", "Pd", 46, 106.42));
    public static final Element SILVER = addElement(new Element("Silver", "Ag", 47, 107.8682));
    public static final Element CADMIUM = addElement(new Element("Cadmium", "Cd", 48, 112.411));
    public static final Element INDIUM = addElement(new Element("Indium", "In", 49, 114.818));
    public static final Element TIN = addElement(new Element("Tin", "Sn", 50, 118.71));
    public static final Element ANTIMONY = addElement(new Element("Antimony", "Sb", 51, 121.76));
    public static final Element IODINE = addElement(new Element("Iodine", "I", 53, 126.9045));
    public static final Element TELLURIUM = addElement(new Element("Tellurium", "Te", 52, 127.6));
    public static final Element XENON = addElement(new Element("Xenon", "Xe", 54, 131.293));
    public static final Element CESIUM = addElement(new Element("Cesium", "Cs", 55, 132.9055));
    public static final Element BARIUM = addElement(new Element("Barium", "Ba", 56, 137.327));
    public static final Element LANTHANUM = addElement(new Element("Lanthanum", "La", 57, 138.9055));
    public static final Element CERIUM = addElement(new Element("Cerium", "Ce", 58, 140.116));
    public static final Element PRASEODYMIUM = addElement(new Element("Praseodymium", "Pr", 59, 140.9077));
    public static final Element NEODYMIUM = addElement(new Element("Neodymium", "Nd", 60, 144.24));
    public static final Element PROMETHIUM = addElement(new Element("Promethium", "Pm", 61, 145));
    public static final Element SAMARIUM = addElement(new Element("Samarium", "Sm", 62, 150.36));
    public static final Element EUROPIUM = addElement(new Element("Europium", "Eu", 63, 151.964));
    public static final Element GADOLINIUM = addElement(new Element("Gadolinium", "Gd", 64, 157.25));
    public static final Element TERBIUM = addElement(new Element("Terbium", "Tb", 65, 158.9253));
    public static final Element DYSPROSIUM = addElement(new Element("Dysprosium", "Dy", 66, 162.5));
    public static final Element HOLMIUM = addElement(new Element("Holmium", "Ho", 67, 164.9303));
    public static final Element ERBIUM = addElement(new Element("Erbium", "Er", 68, 167.259));
    public static final Element THULIUM = addElement(new Element("Thulium", "Tm", 69, 168.9342));
    public static final Element YTTERBIUM = addElement(new Element("Ytterbium", "Yb", 70, 173.04));
    public static final Element LUTETIUM = addElement(new Element("Lutetium", "Lu", 71, 174.967));
    public static final Element HAFNIUM = addElement(new Element("Hafnium", "Hf", 72, 178.49));
    public static final Element TANTALUM = addElement(new Element("Tantalum", "Ta", 73, 180.9479));
    public static final Element TUNGSTEN = addElement(new Element("Tungsten", "W", 74, 183.84));
    public static final Element RHENIUM = addElement(new Element("Rhenium", "Re", 75, 186.207));
    public static final Element OSMIUM = addElement(new Element("Osmium", "Os", 76, 190.23));
    public static final Element IRIDIUM = addElement(new Element("Iridium", "Ir", 77, 192.217));
    public static final Element PLATINUM = addElement(new Element("Platinum", "P ", 78, 195.078));
    public static final Element GOLD = addElement(new Element("Gold", "Au", 79, 196.9665));
    public static final Element MERCURY = addElement(new Element("Mercury", "Hg", 80, 200.59));
    public static final Element THALLIUM = addElement(new Element("Thallium", "Tl", 81, 204.3833));
    public static final Element LEAD = addElement(new Element("Lead", "Pb", 82, 207.2));
    public static final Element BISMUTH = addElement(new Element("Bismuth", "Bi", 83, 208.9804));
    public static final Element POLONIUM = addElement(new Element("Polonium", "Po", 84, 209));
    public static final Element ASTATINE = addElement(new Element("Astatine", "A ", 85, 210));
    public static final Element RADON = addElement(new Element("Radon", "Rn", 86, 222));
    public static final Element FRANCIUM = addElement(new Element("Francium", "Fr", 87, 223));
    public static final Element RADIUM = addElement(new Element("Radium", "Ra", 88, 226));
    public static final Element ACTINIUM = addElement(new Element("Actinium", "Ac", 89, 227));
    public static final Element PROTACTINIUM = addElement(new Element("Protactinium", "Pa", 91, 231.0359));
    public static final Element THORIUM = addElement(new Element("Thorium", "Th", 90, 232.0381));
    public static final Element NEPTUNIUM = addElement(new Element("Neptunium", "Np", 93, 237));
    public static final Element URANIUM = addElement(new Element("Uranium", "U", 92, 238.0289));
    public static final Element AMERICIUM = addElement(new Element("Americium", "Am", 95, 243));
    public static final Element PLUTONIUM = addElement(new Element("Plutonium", "Pu", 94, 244));
    public static final Element CURIUM = addElement(new Element("Curium", "Cm", 96, 247));
    public static final Element BERKELIUM = addElement(new Element("Berkelium", "Bk", 97, 247));
    public static final Element CALIFORNIUM = addElement(new Element("Californium", "Cf", 98, 251));
    public static final Element EINSTEINIUM = addElement(new Element("Einsteinium", "Es", 99, 252));
    public static final Element FERMIUM = addElement(new Element("Fermium", "Fm", 100, 257));
    public static final Element MENDELEVIUM = addElement(new Element("Mendelevium", "Md", 101, 258));
    public static final Element NOBELIUM = addElement(new Element("Nobelium", "No", 102, 259));
    public static final Element RUTHERFORDIUM = addElement(new Element("Rutherfordium", "Rf", 104, 261));
    public static final Element LAWRENCIUM = addElement(new Element("Lawrencium", "Lr", 103, 262));
    public static final Element DUBNIUM = addElement(new Element("Dubnium", "Db", 105, 262));
    public static final Element BOHRIUM = addElement(new Element("Bohrium", "Bh", 107, 264));
    public static final Element SEABORGIUM = addElement(new Element("Seaborgium", "Sg", 106, 266));
    public static final Element MEITNERIUM = addElement(new Element("Meitnerium", "M ", 109, 268));
    public static final Element ROENTGENIUM = addElement(new Element("Roentgenium", "Rg", 111, 272));
    public static final Element HASSIUM = addElement(new Element("Hassium", "Hs", 108, 277));

    private final Set<Element> elements = new HashSet<>();

    private static Element addElement(Element element) {
        INSTANCE.elements.add(element);
        return element;
    }

    /**
     * Contains all the elements of this library.
     *
     * @return All elements in this library.
     */
    public static Set<Element> getElements() {
        return Collections.unmodifiableSet(INSTANCE.elements);
    }

}
