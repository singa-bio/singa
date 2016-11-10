package de.bioforscher.chemistry.physical.atoms;

import de.bioforscher.chemistry.descriptive.elements.Element;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

import static de.bioforscher.chemistry.descriptive.elements.ElementProvider.*;

/**
 * Most of the AtomNames in the PDB.
 */
public enum AtomName {

    // Hydrogen
    H("H", HYDROGEN),
    H1("H1", HYDROGEN),
    H2("H2", HYDROGEN),
    H3("H3", HYDROGEN),
    HA("HA", HYDROGEN),
    HA1("HA1", HYDROGEN),
    HA2("HA2", HYDROGEN),
    HA3("HA3", HYDROGEN),
    HB("HB", HYDROGEN),
    HB1("HB1", HYDROGEN),
    HB2("HB2", HYDROGEN),
    HB3("HB3", HYDROGEN),
    HD1("HD1", HYDROGEN),
    HD11("HD11", HYDROGEN),
    HD12("HD12", HYDROGEN),
    HD13("HD13", HYDROGEN),
    HD("HD", HYDROGEN),
    HD2("HD2", HYDROGEN),
    HD21("HD21", HYDROGEN),
    HD22("HD22", HYDROGEN),
    HD23("HD23", HYDROGEN),
    HDD1("HDD1", HYDROGEN),
    HDD2("HDD2", HYDROGEN),
    HDD3("HDD3", HYDROGEN),
    HD3("HD3", HYDROGEN),
    HE("HE", HYDROGEN),
    HE1("HE1", HYDROGEN),
    HE2("HE2", HYDROGEN),
    HE3("HE3", HYDROGEN),
    HE21("HE21", HYDROGEN),
    HE22("HE22", HYDROGEN),
    HG("HG", HYDROGEN),
    HG1("HG1", HYDROGEN),
    HG2("HG2", HYDROGEN),
    HG3("HG3", HYDROGEN),
    HG11("HG11", HYDROGEN),
    HG12("HG12", HYDROGEN),
    HG13("HG13", HYDROGEN),
    HG21("HG21", HYDROGEN),
    HG22("HG22", HYDROGEN),
    HG23("HG23", HYDROGEN),
    HH("HH", HYDROGEN),
    HH11("HH11", HYDROGEN),
    HH12("HH12", HYDROGEN),
    HH2("HH2", HYDROGEN),
    HH21("HH21", HYDROGEN),
    HH22("HH22", HYDROGEN),
    HN("HN", HYDROGEN),
    HT1("HT1", HYDROGEN),
    HT2("HT2", HYDROGEN),
    HT3("HT3", HYDROGEN),
    HXT("HXT", HYDROGEN),
    HZ("HZ", HYDROGEN),
    HZ1("HZ1", HYDROGEN),
    HZ2("HZ2", HYDROGEN),
    HZ3("HZ3", HYDROGEN),
    ONEHT("1HT", HYDROGEN),
    TWOHB("2HB", HYDROGEN),
    TWOHD("2HD", HYDROGEN),
    TWOHE("2HE", HYDROGEN),
    TWOHG("2HG", HYDROGEN),
    TWOHT("2HT", HYDROGEN),
    THREEHB("3HB", HYDROGEN),
    THREEHT("3HT", HYDROGEN),

    // Deuterium
    D("D", DEUTERIUM),
    D1("D1", DEUTERIUM),
    D2("D2", DEUTERIUM),
    D3("D3", DEUTERIUM),
    DA("DA", DEUTERIUM),
    DA2("DA2", DEUTERIUM),
    DA3("DA3", DEUTERIUM),
    DB("DB", DEUTERIUM),
    DB1("DB1", DEUTERIUM),
    DB2("DB2", DEUTERIUM),
    DB3("DB3", DEUTERIUM),
    DD1("DD1", DEUTERIUM),
    DD11("DD11", DEUTERIUM),
    DD12("DD12", DEUTERIUM),
    DD13("DD13", DEUTERIUM),
    DD2("DD2", DEUTERIUM),
    DD21("DD21", DEUTERIUM),
    DD22("DD22", DEUTERIUM),
    DD23("DD23", DEUTERIUM),
    DDD1("DDD1", DEUTERIUM),
    DDD2("DDD2", DEUTERIUM),
    DDD3("DDD3", DEUTERIUM),
    DD3("DD3", DEUTERIUM),
    DE("DE", DEUTERIUM),
    DE1("DE1", DEUTERIUM),
    DE2("DE2", DEUTERIUM),
    DE3("DE3", DEUTERIUM),
    DE21("DE21", DEUTERIUM),
    DE22("DE22", DEUTERIUM),
    DG("DG", DEUTERIUM),
    DG1("DG1", DEUTERIUM),
    DG11("DG11", DEUTERIUM),
    DG12("DG12", DEUTERIUM),
    DG13("DG13", DEUTERIUM),
    DG2("DG2", DEUTERIUM),
    DG21("DG21", DEUTERIUM),
    DG22("DG22", DEUTERIUM),
    DG23("DG23", DEUTERIUM),
    DG3("DG3", DEUTERIUM),
    DH("DH", DEUTERIUM),
    DH11("DH11", DEUTERIUM),
    DH12("DH12", DEUTERIUM),
    DH2("DH2", DEUTERIUM),
    DH21("DH21", DEUTERIUM),
    DH22("DH22", DEUTERIUM),
    DXT("DXT", DEUTERIUM),
    DZ("DZ", DEUTERIUM),
    DZ1("DZ1", DEUTERIUM),
    DZ2("DZ2", DEUTERIUM),
    DZ3("DZ3", DEUTERIUM),

    // Carbons
    C("C", CARBON),
    C5("C5", CARBON),
    C10("C10", CARBON),
    C11("C11", CARBON),
    C26("C26", CARBON),
    C27("C27", CARBON),
    C28("C28", CARBON),
    C29("C29", CARBON),
    C12("C12", CARBON),
    C14("C14", CARBON),
    C15("C15", CARBON),
    C16("C16", CARBON),
    C17("C17", CARBON),
    C32("C32", CARBON),
    C38("C38", CARBON),
    C39("C39", CARBON),
    C53("C53", CARBON),
    C54("C54", CARBON),
    C55("C55", CARBON),
    C56("C56", CARBON),
    CA("CA", CARBON),
    CB("CB", CARBON),
    CD("CD", CARBON),
    CD1("CD1", CARBON),
    CD2("CD2", CARBON),
    CE("CE", CARBON),
    CE1("CE1", CARBON),
    CE2("CE2", CARBON),
    CE3("CE3", CARBON),
    CG("CG", CARBON),
    CG1("CG1", CARBON),
    CG2("CG2", CARBON),
    CH2("CH2", CARBON),
    CZ("CZ", CARBON),
    CZ2("CZ2", CARBON),
    CZ3("CZ3", CARBON),

    // Nitrogen
    N("N", NITROGEN),
    N3("N", NITROGEN),
    N4("N4", NITROGEN),
    N6("N6", NITROGEN),
    N7("N7", NITROGEN),
    ND1("ND1", NITROGEN),
    ND2("ND2",NITROGEN),
    NE("NE", NITROGEN),
    NE1("NE1", NITROGEN),
    NE2("NE2", NITROGEN),
    NH1("NH1", NITROGEN),
    NH2("NH2", NITROGEN),
    NZ("NZ", NITROGEN),

    // Oxygen
    O("O", OXYGEN),
    O2("O2", OXYGEN),
    O4("O4", OXYGEN),
    O6("O6", OXYGEN),
    O9("O9", OXYGEN),
    OD1("OD1", OXYGEN),
    OD2("OD2", OXYGEN),
    OE1("OE1", OXYGEN),
    OE2("OE2", OXYGEN),
    OG("OG", OXYGEN),
    OG1("OG1", OXYGEN),
    OG2("OG2", OXYGEN),
    OH("OH", OXYGEN),
    OXT("OXT", OXYGEN),

    // Sulfur
    SG("SG", SULFUR),
    SD("SD", SULFUR),

    // non standard
    UNK("UNK", UNKOWN);


    public static EnumSet<AtomName> ALANINE_ATOM_NAMES = EnumSet.of(H, H1, H2, HA, HB1, HB2, HB3, HN, HXT, THREEHB,
            D, D1, D2, D3, DA, DB1, DB2, DB3, C, CA, CB, N, O, OXT);


    public static EnumSet<AtomName> ARGININE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HD1, HD2, HD3,
            HE, HG1, HG2, HG3, HH11, HH12, HH21, HH22, HN, HXT, TWOHB, TWOHD, TWOHG, D, D1, D2, D3, DA, DB2, DB3, DD2,
            DD3, DE, DG2, DG3, DH11, DH12, DH21, DH22, C, CA, CB, CD, CG, CZ, N, NE, NH1, NH2, O, OXT);


    public static EnumSet<AtomName> ASPARAGINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA2, HA3, HB1, HB2, HB3, HD21,
            HD22, HDD1, HDD2, HN, HXT, TWOHG, D, D2, DA, DB2, DB3, DD21, DD22, DDD1, DDD2, C,  CA, CB, CG, N, ND2, O,
            OD1, OXT);

    public static EnumSet<AtomName> ASPARTIC_ACID_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA2, HA3, HB1, HB2, HB3, HD2,
            HN, HXT, TWOHB, D, D3, DA, DB2, DB3, DD2, C, CA, CB, CG, N, O, OD1, OD2, OXT);

    public static EnumSet<AtomName> CYSTEINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HG, HB1, HB2, HB3, HN, HXT,
            TWOHB, D, DA, DG, DB2, DB3, C, CA, CB, N, O, OXT, SG);

    public static EnumSet<AtomName> GLUTAMIC_ACID_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA2, HA3, HB1, HB2, HB3,
            HE2, HG1, HG2, HG3, HN, HXT, TWOHB, TWOHG, D, DA, DB2, DB3, DE2, DG2, DG3, C, CA, CB, CD, CG, N, O, OE1,
            OE2, OXT);

    public static EnumSet<AtomName> GLUTAMINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HE21, HE22, HG1,
            HG2, HG3, HN, HT1, HT2, HT3, TWOHB, TWOHG, D, DA, DB2, DB3, DE21, DE22, DG2, DG3, C, CA, CB, CD, CG, N,
            NE2, O, OE1, OXT);

    public static EnumSet<AtomName> GLYCINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA1, HA2, HA3, HB1, HB2, HB3,
            HN, HT1, HT2, HT3, HXT, D, D1, D2, D3, DA2, DA3, C, C10, C11, CA, CB, N, N3, O, O9, OXT);


    public static EnumSet<AtomName> HISTIDINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA3, HB1, HB2, HB3, HD1, HD2,
            HD3, HE1, HE2, HN, TWOHB, D, D1, D2, D3, DA, DB2, DB3, DD1, DD2, DE1, DE2, C, CA, CB, CD2, CE1, CG, N, ND1,
            NE2, O, OXT);

    public static EnumSet<AtomName> ISOLEUCINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB, HD1, HD11, HD12, HD13,
            HD2, HD3, HDD1, HDD2, HDD3, HG11, HG12, HG13, HG21, HG22, HG23, HN, HXT, D1, D2, D3, DA, DB, DD11, DD12,
            DD13, DD21, DDD1, DDD2, DDD3, DG12, DG13, DG21, DG22, DG23, C, CA, CB, CD, CD1, CG1, CG2, N, O, OXT);

    public static EnumSet<AtomName> LEUCINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA2, HA3, HB1, HB2, HB3, HD11,
            HD12, HD13, HD21, HD22, HD23, HG, HN, HXT, TWOHB, D, DA, DB2, DB3, DD11, DD12, DD13, DD21, DD22, DD23, DG,
            C, C12, C14, C15, C16, C17, C32, C38, C39, C53, C54, C55, C56, CA, CB, CD1, CD2, CE, CG, N, N4, N7, O, O4,
            O6, OXT, SD);

    public static EnumSet<AtomName> LYSINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA2, HA3, HB1, HB2, HB3, HD1,
            HD2, HD3, HD21, HD22, HD23, HE1, HE2, HE3, HG1, HG2, HG3, HN, HXT, HZ1, HZ2, HZ3, TWOHB, TWOHD, TWOHE,
            TWOHG, D, D1, DB2, DB3, DA, DB2, DB3, DD2, DD3, DE2, DE3, DG2, DG3, DXT, DZ1, DZ2, DZ3,
            C, CA, CB, CD, CE, CG, N, NZ, O, OXT);

    public static EnumSet<AtomName> METHIONINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HE1, HE2, HE3,
            HG1, HG2, HG3, HN, HXT, ONEHT, TWOHB, TWOHG, TWOHG, THREEHT, D, D1, D2, D3, DA, DB2, DB3, DE1, DE2, DE3,
            DG2, DG3, C, CA, CB, CE, CG, N, O, OXT, SD);

    public static EnumSet<AtomName> PHENYLALANINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HD1, HD2,
            HD3, HE1, HE2, HN, HXT, HZ, TWOHB, D, D1, D2, D3, DA, DB2, DB3, DD1, DD2, DE1, DE2, DZ, C, CA, CB, CD1, CD2,
            CE1, CE2, CG, CZ, N, O, OXT);

    public static EnumSet<AtomName> PROLINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HD1, HD2, HD3,
            HG1, HG2, HG3, TWOHB, TWOHD, TWOHG, D, D2, DA, DB2, DB3, DD2, DD3, C, C5, C26, C27, C28, C29, CA, CB, CD,
            CG, N, N6, O, O2, OXT);

    public static EnumSet<AtomName> SERINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA2, HA3, HB1, HB2, HB3, HG, HN,
            HXT, TWOHB, D, D2, D3, DA, DB2, DB3, DG, C, CA, CB, N, O, OG, OXT);

    public static EnumSet<AtomName> THREONINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HG1, HG21,
            HG22, HG23, HG3, HN, HXT, D, DA, DB, DG1, DG21, DG22, DG23, C, CA, CB, CG1, CG2, N, O, OG1, OG2, OXT);

    public static EnumSet<AtomName> TRYPTOPHAN_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HD, HD1, HE1,
            HE3, HH, HH2, HN, HXT, HZ2, HZ3, TWOHB, D, DA, DB2, DB3, DD1, DE1, DE3, DH2, DZ2, DZ3, C, CA, CB, CD1,
            CD2, CE2, CE3, CG, CH2, CZ2, CZ3, N, NE1, O, OXT);

    public static EnumSet<AtomName> TYROSINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HD1, HD2, HD3,
            HE1, HE2, HH, HN, HXT, TWOHB, D, DA, DB2, DB3, DD1, DD2, DE1, DE2, DH, C, CA, CB, CD1, CD2, CE1, CE2, CG,
            CZ, N, O, OH, OXT);

    public static EnumSet<AtomName> VALINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB, HB1, HB2, HB3, HG11, HG12,
            HG13, HG21, HG22, HG23, DG11, DG12, DG13, DG21, DG22, DG23, HN, HXT, D, D1, D2, D3, DA, DB, D, C, CA, CB,
            N, O, OXT);

    private String name;
    private Element element;

    AtomName(String name, Element element) {
        this.name = name;
        this.element = element;
    }

    public String getName() {
        return this.name;
    }

    public Element getElement() {
        return this.element;
    }

    public static AtomName getAtomNameFromString(String atomName) {
        // FIXME this can be sped up with a static hash map using the string atom name as key
        return Arrays.stream(values()).filter(name -> atomName.trim().equals(name.getName()))
                .findAny()
                .orElse(UNK);
    }

}
