package de.bioforscher.chemistry.descriptive.elements;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This library provides Elements 1 (Hydrogen) to 111 (Roentgenium) with their names, symbols, atomic numbers, and
 * atomic mass.
 *
 * @author cl
 */
public final class ElementProvider {

    private static final ElementProvider INSTANCE = new ElementProvider();

    public static final Element HYDROGEN = addElement(new Element("Hydrogen", "H", 1, 1.008, "1s1"));
    public static final Element DEUTERIUM = addElement(new Element("Deuterium", "D", 1, 1.008, "1s1")).asIsotope(2);
    public static final Element TRITIUM = addElement(new Element("Tritium", "T", 1, 1.008, "1s1")).asIsotope(3);
    public static final Element HELIUM = addElement(new Element("Helium", "He", 2, 4.002602, "1s2"));
    public static final Element LITHIUM = addElement(new Element("Lithium", "Li", 3, 6.94, "1s2-2s1"));
    public static final Element BERYLLIUM = addElement(new Element("Beryllium", "Be", 4, 6.94, "1s2-2s2"));
    public static final Element BORON = addElement(new Element("Boron", "B", 5, 9.0121831, "1s2-2s2-2p1"));
    public static final Element CARBON = addElement(new Element("Carbon", "C", 6, 12.011, "1s2-2s2-2p2"));
    public static final Element NITROGEN = addElement(new Element("Nitrogen", "N", 7, 14.007, "1s2-2s2-2p3"));
    public static final Element OXYGEN = addElement(new Element("Oxygen", "O", 8, 15.999, "1s2-2s2-2p4"));
    public static final Element FLUORINE = addElement(new Element("Fluorine", "F", 9, 18.998403163, "1s2-2s2-2p5"));
    public static final Element NEON = addElement(new Element("Neon", "Ne", 10, 20.1797, "1s2-2s2-2p6"));
    public static final Element SODIUM = addElement(new Element("Sodium", "Na", 11, 22.98976928, "1s2-2s2-2p6-3s1"));
    public static final Element MAGNESIUM = addElement(new Element("Magnesium", "Mg", 12, 24.305, "1s2-2s2-2p6-3s2"));
    public static final Element ALUMINIUM = addElement(new Element("Aluminium", "Al", 13, 26.9815385, "1s2-2s2-2p6-3s2-3p1"));
    public static final Element SILICON = addElement(new Element("Silicon", "Si", 14, 28.085, "1s2-2s2-2p6-3s2-3p2"));
    public static final Element PHOSPHORUS = addElement(new Element("Phosphorus", "P", 15, 30.973761998, "1s2-2s2-2p6-3s2-3p3"));
    public static final Element SULFUR = addElement(new Element("Sulfur", "S", 16, 32.06, "1s2-2s2-2p6-3s2-3p4"));
    public static final Element CHLORINE = addElement(new Element("Chlorine", "Cl", 17, 35.45, "1s2-2s2-2p6-3s2-3p5"));
    public static final Element ARGON = addElement(new Element("Argon", "Ar", 18, 39.948, "1s2-2s2-2p6-3s2-3p6"));
    public static final Element POTASSIUM = addElement(new Element("Potassium", "K", 19, 39.948, "1s2-2s2-2p6-3s2-3p6-4s1"));
    public static final Element CALCIUM = addElement(new Element("Calcium", "Ca", 20, 40.078, "1s2-2s2-2p6-3s2-3p6-4s2"));
    public static final Element SCANDIUM = addElement(new Element("Scandium", "Sc", 21, 44.9559, "1s2-2s2-2p6-3s2-3p6-3d1-4s2"));
    public static final Element TITANIUM = addElement(new Element("Titanium", "Ti", 22, 47.867, "1s2-2s2-2p6-3s2-3p6-3d2-4s2"));
    public static final Element VANADIUM = addElement(new Element("Vanadium", "V", 23, 50.9415, "1s2-2s2-2p6-3s2-3p6-3d3-4s2"));
    public static final Element CHROMIUM = addElement(new Element("Chromium", "Cr", 24, 51.9961, "1s2-2s2-2p6-3s2-3p6-3d5-4s1"));
    public static final Element MANGANESE = addElement(new Element("Manganese", "Mg", 25, 54.938, "1s2-2s2-2p6-3s2-3p6-3d5-4s2"));
    public static final Element IRON = addElement(new Element("Iron", "Fe", 26, 55.845, "1s2-2s2-2p6-3s2-3p6-3d6-4s2"));
    public static final Element NICKEL = addElement(new Element("Nickel", "Ni", 27, 58.6934, "1s2-2s2-2p6-3s2-3p6-3d7-4s2"));
    public static final Element COBALT = addElement(new Element("Cobalt", "Co", 28, 58.9332, "1s2-2s2-2p6-3s2-3p6-3d8-4s2"));
    public static final Element COPPER = addElement(new Element("Copper", "Cu", 29, 63.546, "1s2-2s2-2p6-3s2-3p6-3d10-4s1"));
    public static final Element ZINC = addElement(new Element("Zinc", "Zn", 30, 65.39, "1s2-2s2-2p6-3s2-3p6-3d10-4s2"));
    public static final Element GALLIUM = addElement(new Element("Gallium", "Ga", 31, 69.723, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p1"));
    public static final Element GERMANIUM = addElement(new Element("Germanium", "Ge", 32, 72.64, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p2"));
    public static final Element ARSENIC = addElement(new Element("Arsenic", "As", 33, 74.9216, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p3"));
    public static final Element SELENIUM = addElement(new Element("Selenium", "Se", 34, 78.96, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p4"));
    public static final Element BROMINE = addElement(new Element("Bromine", "Br", 35, 79.904, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p5"));
    public static final Element KRYPTON = addElement(new Element("Krypton", "Kr", 36, 83.8, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6"));
    public static final Element RUBIDIUM = addElement(new Element("Rubidium", "Rb", 37, 85.4678, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-5s1"));
    public static final Element STRONTIUM = addElement(new Element("Strontium", "Sr", 38, 87.62, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-5s2"));
    public static final Element YTTRIUM = addElement(new Element("Yttrium", "Y", 39, 88.9059, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d1-5s2"));
    public static final Element ZIRCONIUM = addElement(new Element("Zirconium", "Zr", 40, 91.224, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d2-5s2"));
    public static final Element NIOBIUM = addElement(new Element("Niobium", "Nb", 41, 92.9064, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d4-5s1"));
    public static final Element MOLYBDENUM = addElement(new Element("Molybdenum", "Mo", 42, 95.94, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d5-5s1"));
    public static final Element TECHNETIUM = addElement(new Element("Technetium", "Tc", 43, 98, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d5-5s2"));
    public static final Element RUTHENIUM = addElement(new Element("Ruthenium", "Ru", 44, 101.07, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d7-5s1"));
    public static final Element RHODIUM = addElement(new Element("Rhodium", "Rh", 45, 102.9055, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d8-5s1"));
    public static final Element PALLADIUM = addElement(new Element("Palladium", "Pd", 46, 106.42, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10"));
    public static final Element SILVER = addElement(new Element("Silver", "Ag", 47, 107.8682, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-5s1"));
    public static final Element CADMIUM = addElement(new Element("Cadmium", "Cd", 48, 112.411, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-5s2"));
    public static final Element INDIUM = addElement(new Element("Indium", "In", 49, 114.818, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-5s2-5p1"));
    public static final Element TIN = addElement(new Element("Tin", "Sn", 50, 118.71, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-5s2-5p2"));
    public static final Element ANTIMONY = addElement(new Element("Antimony", "Sb", 51, 121.76, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-5s2-5p3"));
    public static final Element IODINE = addElement(new Element("Iodine", "I", 53, 126.9045, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-5s2-5p5"));
    public static final Element TELLURIUM = addElement(new Element("Tellurium", "Te", 52, 127.6, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-5s2-5p4"));
    public static final Element XENON = addElement(new Element("Xenon", "Xe", 54, 131.293, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-5s2-5p6"));
    public static final Element CESIUM = addElement(new Element("Cesium", "Cs", 55, 132.9055, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-5s2-5p6-6s1"));
    public static final Element BARIUM = addElement(new Element("Barium", "Ba", 56, 137.327, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-5s2-5p6-6s2"));
    public static final Element LANTHANUM = addElement(new Element("Lanthanum", "La", 57, 138.9055, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-5s2-5p6-5d1-6s2"));
    public static final Element CERIUM = addElement(new Element("Cerium", "Ce", 58, 140.116, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f1-5s2-5p6-5d1-6s2"));
    public static final Element PRASEODYMIUM = addElement(new Element("Praseodymium", "Pr", 59, 140.9077, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f3-5s2-5p6-6s2"));
    public static final Element NEODYMIUM = addElement(new Element("Neodymium", "Nd", 60, 144.24, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f4-5s2-5p6-6s2"));
    public static final Element PROMETHIUM = addElement(new Element("Promethium", "Pm", 61, 145, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f5-5s2-5p6-6s2"));
    public static final Element SAMARIUM = addElement(new Element("Samarium", "Sm", 62, 150.36, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f6-5s2-5p6-6s2"));
    public static final Element EUROPIUM = addElement(new Element("Europium", "Eu", 63, 151.964, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f7-5s2-5p6-6s2"));
    public static final Element GADOLINIUM = addElement(new Element("Gadolinium", "Gd", 64, 157.25, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f7-5s2-5p6-5d1-6s2"));
    public static final Element TERBIUM = addElement(new Element("Terbium", "Tb", 65, 158.9253, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f9-5s2-5p6-6s2"));
    public static final Element DYSPROSIUM = addElement(new Element("Dysprosium", "Dy", 66, 162.5, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f10-5s2-5p6-6s2"));
    public static final Element HOLMIUM = addElement(new Element("Holmium", "Ho", 67, 164.9303, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f11-5s2-5p6-6s2"));
    public static final Element ERBIUM = addElement(new Element("Erbium", "Er", 68, 167.259, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f12-5s2-5p6-6s2"));
    public static final Element THULIUM = addElement(new Element("Thulium", "Tm", 69, 168.9342, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f13-5s2-5p6-6s2"));
    public static final Element YTTERBIUM = addElement(new Element("Ytterbium", "Yb", 70, 173.04, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-6s2"));
    public static final Element LUTETIUM = addElement(new Element("Lutetium", "Lu", 71, 174.967, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d1-6s2"));
    public static final Element HAFNIUM = addElement(new Element("Hafnium", "Hf", 72, 178.49, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d2-6s2"));
    public static final Element TANTALUM = addElement(new Element("Tantalum", "Ta", 73, 180.9479, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d3-6s2"));
    public static final Element TUNGSTEN = addElement(new Element("Tungsten", "W", 74, 183.84, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d4-6s2"));
    public static final Element RHENIUM = addElement(new Element("Rhenium", "Re", 75, 186.207, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d5-6s2"));
    public static final Element OSMIUM = addElement(new Element("Osmium", "Os", 76, 190.23, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d6-6s2"));
    public static final Element IRIDIUM = addElement(new Element("Iridium", "Ir", 77, 192.217, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d7-6s2"));
    public static final Element PLATINUM = addElement(new Element("Platinum", "Pt", 78, 195.078, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d9-6s1"));
    public static final Element GOLD = addElement(new Element("Gold", "Au", 79, 196.9665, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s1"));
    public static final Element MERCURY = addElement(new Element("Mercury", "Hg", 80, 200.59, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s2"));
    public static final Element THALLIUM = addElement(new Element("Thallium", "Tl", 81, 204.3833, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s2-6p1"));
    public static final Element LEAD = addElement(new Element("Lead", "Pb", 82, 207.2, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s2-6p2"));
    public static final Element BISMUTH = addElement(new Element("Bismuth", "Bi", 83, 208.9804, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s2-6p3"));
    public static final Element POLONIUM = addElement(new Element("Polonium", "Po", 84, 209, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s2-6p4"));
    public static final Element ASTATINE = addElement(new Element("Astatine", "A ", 85, 210, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s2-6p5"));
    public static final Element RADON = addElement(new Element("Radon", "Rn", 86, 222, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s2-6p6"));
    public static final Element FRANCIUM = addElement(new Element("Francium", "Fr", 87, 223, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s2-6p6-7s1"));
    public static final Element RADIUM = addElement(new Element("Radium", "Ra", 88, 226, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s2-6p6-7s2"));
    public static final Element ACTINIUM = addElement(new Element("Actinium", "Ac", 89, 227, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s2-6p6-6d1-7s2"));
    public static final Element THORIUM = addElement(new Element("Thorium", "Th", 90, 232.0381, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-6s2-6p6-6d2-7s2"));
    public static final Element PROTACTINIUM = addElement(new Element("Protactinium", "Pa", 91, 231.0359, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f2-6s2-6p6-6d1-7s2"));
    public static final Element URANIUM = addElement(new Element("Uranium", "U", 92, 238.0289, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f3-6s2-6p6-6d1-7s2"));
    public static final Element NEPTUNIUM = addElement(new Element("Neptunium", "Np", 93, 237, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f4-6s2-6p6-6d1-7s2"));
    public static final Element PLUTONIUM = addElement(new Element("Plutonium", "Pu", 94, 244, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f6-6s2-6p6-7s2"));
    public static final Element AMERICIUM = addElement(new Element("Americium", "Am", 95, 243, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f7-6s2-6p6-7s2"));
    public static final Element CURIUM = addElement(new Element("Curium", "Cm", 96, 247, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f7-6s2-6p6-6d1-7s2"));
    public static final Element BERKELIUM = addElement(new Element("Berkelium", "Bk", 97, 247, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f9-6s2-6p6-7s2"));
    public static final Element CALIFORNIUM = addElement(new Element("Californium", "Cf", 98, 251, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f10-6s2-6p6-7s2"));
    public static final Element EINSTEINIUM = addElement(new Element("Einsteinium", "Es", 99, 252, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f11-6s2-6p6-7s2"));
    public static final Element FERMIUM = addElement(new Element("Fermium", "Fm", 100, 257, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f12-6s2-6p6-7s2"));
    public static final Element MENDELEVIUM = addElement(new Element("Mendelevium", "Md", 101, 258, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f13-6s2-6p6-7s2"));
    public static final Element NOBELIUM = addElement(new Element("Nobelium", "No", 102, 259, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f14-6s2-6p6-7s2"));
    public static final Element LAWRENCIUM = addElement(new Element("Lawrencium", "Lr", 103, 262, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f14-6s2-6p6-7s2-7p1"));
    public static final Element RUTHERFORDIUM = addElement(new Element("Rutherfordium", "Rf", 104, 261, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f14-6s2-6p6-6d2-7s2"));
    public static final Element DUBNIUM = addElement(new Element("Dubnium", "Db", 105, 262, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f14-6s2-6p6-6d3-7s2"));
    public static final Element SEABORGIUM = addElement(new Element("Seaborgium", "Sg", 106, 266, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f14-6s2-6p6-6d4-7s2"));
    public static final Element BOHRIUM = addElement(new Element("Bohrium", "Bh", 107, 264, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f14-6s2-6p6-6d5-7s2"));
    public static final Element HASSIUM = addElement(new Element("Hassium", "Hs", 108, 277, "1s2-2s2-2p6-3s2-3p6-3d10-4s2-4p6-4d10-4f14-5s2-5p6-5d10-5f14-6s2-6p6-6d6-7s2"));

    public static final Element UNKOWN = addElement(new Element("Unkown", "?", 0, 0, "1s0"));

    private final Set<Element> elements = new HashSet<>();

    private static Element addElement(Element element) {
        INSTANCE.elements.add(element);
        return element;
    }

    /**
     * Retrieves an {@link Optional} of an {@link Element} by ity symbol.
     *
     * @param symbol The element symbol for which an {@link Element} should be retrieved.
     * @return {@link Optional} of the {@link Element}.
     */
    public static Optional<Element> getElementBySymbol(String symbol) {
        // by contract one symbol cannot decode for multiple elements
        return INSTANCE.elements.stream()
                .filter(element -> element.getSymbol().equalsIgnoreCase(symbol))
                .findAny();
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