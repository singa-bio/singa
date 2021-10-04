package bio.singa.structure.model.families;

import bio.singa.chemistry.model.elements.Element;
import bio.singa.chemistry.model.elements.ElementProvider;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Most (some) of the AtomNames in the PDB.
 */
public enum AtomName {

    // Hydrogen
    H("H", ElementProvider.HYDROGEN),
    H1("H1", ElementProvider.HYDROGEN),
    H2("H2", ElementProvider.HYDROGEN),
    H3("H3", ElementProvider.HYDROGEN),
    HA("HA", ElementProvider.HYDROGEN),
    HA1("HA1", ElementProvider.HYDROGEN),
    HA2("HA2", ElementProvider.HYDROGEN),
    HA3("HA3", ElementProvider.HYDROGEN),
    HB("HB", ElementProvider.HYDROGEN),
    HB1("HB1", ElementProvider.HYDROGEN),
    HB2("HB2", ElementProvider.HYDROGEN),
    HB3("HB3", ElementProvider.HYDROGEN),
    HD1("HD1", ElementProvider.HYDROGEN),
    HD11("HD11", ElementProvider.HYDROGEN),
    HD12("HD12", ElementProvider.HYDROGEN),
    HD13("HD13", ElementProvider.HYDROGEN),
    HD("HD", ElementProvider.HYDROGEN),
    HD2("HD2", ElementProvider.HYDROGEN),
    HD21("HD21", ElementProvider.HYDROGEN),
    HD22("HD22", ElementProvider.HYDROGEN),
    HD23("HD23", ElementProvider.HYDROGEN),
    HDD1("HDD1", ElementProvider.HYDROGEN),
    HDD2("HDD2", ElementProvider.HYDROGEN),
    HDD3("HDD3", ElementProvider.HYDROGEN),
    HD3("HD3", ElementProvider.HYDROGEN),
    HE("HE", ElementProvider.HYDROGEN),
    HE1("HE1", ElementProvider.HYDROGEN),
    HE2("HE2", ElementProvider.HYDROGEN),
    HE3("HE3", ElementProvider.HYDROGEN),
    HE21("HE21", ElementProvider.HYDROGEN),
    HE22("HE22", ElementProvider.HYDROGEN),
    HG("HG", ElementProvider.HYDROGEN),
    HG1("HG1", ElementProvider.HYDROGEN),
    HG2("HG2", ElementProvider.HYDROGEN),
    HG3("HG3", ElementProvider.HYDROGEN),
    HG11("HG11", ElementProvider.HYDROGEN),
    HG12("HG12", ElementProvider.HYDROGEN),
    HG13("HG13", ElementProvider.HYDROGEN),
    HG21("HG21", ElementProvider.HYDROGEN),
    HG22("HG22", ElementProvider.HYDROGEN),
    HG23("HG23", ElementProvider.HYDROGEN),
    HH("HH", ElementProvider.HYDROGEN),
    HH11("HH11", ElementProvider.HYDROGEN),
    HH12("HH12", ElementProvider.HYDROGEN),
    HH2("HH2", ElementProvider.HYDROGEN),
    HH21("HH21", ElementProvider.HYDROGEN),
    HH22("HH22", ElementProvider.HYDROGEN),
    HN("HN", ElementProvider.HYDROGEN),
    HT1("HT1", ElementProvider.HYDROGEN),
    HT2("HT2", ElementProvider.HYDROGEN),
    HT3("HT3", ElementProvider.HYDROGEN),
    HXT("HXT", ElementProvider.HYDROGEN),
    HZ("HZ", ElementProvider.HYDROGEN),
    HZ1("HZ1", ElementProvider.HYDROGEN),
    HZ2("HZ2", ElementProvider.HYDROGEN),
    HZ3("HZ3", ElementProvider.HYDROGEN),
    ONEHT("1HT", ElementProvider.HYDROGEN),
    TWOHB("2HB", ElementProvider.HYDROGEN),
    TWOHD("2HD", ElementProvider.HYDROGEN),
    TWOHE("2HE", ElementProvider.HYDROGEN),
    TWOHG("2HG", ElementProvider.HYDROGEN),
    TWOHT("2HT", ElementProvider.HYDROGEN),
    THREEHB("3HB", ElementProvider.HYDROGEN),
    THREEHT("3HT", ElementProvider.HYDROGEN),

    // Deuterium
    D("D", ElementProvider.DEUTERIUM),
    D1("D1", ElementProvider.DEUTERIUM),
    D2("D2", ElementProvider.DEUTERIUM),
    D3("D3", ElementProvider.DEUTERIUM),
    DA("DA", ElementProvider.DEUTERIUM),
    DA2("DA2", ElementProvider.DEUTERIUM),
    DA3("DA3", ElementProvider.DEUTERIUM),
    DB("DB", ElementProvider.DEUTERIUM),
    DB1("DB1", ElementProvider.DEUTERIUM),
    DB2("DB2", ElementProvider.DEUTERIUM),
    DB3("DB3", ElementProvider.DEUTERIUM),
    DD1("DD1", ElementProvider.DEUTERIUM),
    DD11("DD11", ElementProvider.DEUTERIUM),
    DD12("DD12", ElementProvider.DEUTERIUM),
    DD13("DD13", ElementProvider.DEUTERIUM),
    DD2("DD2", ElementProvider.DEUTERIUM),
    DD21("DD21", ElementProvider.DEUTERIUM),
    DD22("DD22", ElementProvider.DEUTERIUM),
    DD23("DD23", ElementProvider.DEUTERIUM),
    DDD1("DDD1", ElementProvider.DEUTERIUM),
    DDD2("DDD2", ElementProvider.DEUTERIUM),
    DDD3("DDD3", ElementProvider.DEUTERIUM),
    DD3("DD3", ElementProvider.DEUTERIUM),
    DE("DE", ElementProvider.DEUTERIUM),
    DE1("DE1", ElementProvider.DEUTERIUM),
    DE2("DE2", ElementProvider.DEUTERIUM),
    DE3("DE3", ElementProvider.DEUTERIUM),
    DE21("DE21", ElementProvider.DEUTERIUM),
    DE22("DE22", ElementProvider.DEUTERIUM),
    DG("DG", ElementProvider.DEUTERIUM),
    DG1("DG1", ElementProvider.DEUTERIUM),
    DG11("DG11", ElementProvider.DEUTERIUM),
    DG12("DG12", ElementProvider.DEUTERIUM),
    DG13("DG13", ElementProvider.DEUTERIUM),
    DG2("DG2", ElementProvider.DEUTERIUM),
    DG21("DG21", ElementProvider.DEUTERIUM),
    DG22("DG22", ElementProvider.DEUTERIUM),
    DG23("DG23", ElementProvider.DEUTERIUM),
    DG3("DG3", ElementProvider.DEUTERIUM),
    DH("DH", ElementProvider.DEUTERIUM),
    DH11("DH11", ElementProvider.DEUTERIUM),
    DH12("DH12", ElementProvider.DEUTERIUM),
    DH2("DH2", ElementProvider.DEUTERIUM),
    DH21("DH21", ElementProvider.DEUTERIUM),
    DH22("DH22", ElementProvider.DEUTERIUM),
    DXT("DXT", ElementProvider.DEUTERIUM),
    DZ("DZ", ElementProvider.DEUTERIUM),
    DZ1("DZ1", ElementProvider.DEUTERIUM),
    DZ2("DZ2", ElementProvider.DEUTERIUM),
    DZ3("DZ3", ElementProvider.DEUTERIUM),

    // Carbons
    C("C", ElementProvider.CARBON),
    C1Pr("C1'", ElementProvider.CARBON),
    C1St("C1*", ElementProvider.CARBON),
    C2("C2", ElementProvider.CARBON),
    C2Pr("C2'", ElementProvider.CARBON),
    C2St("C2*", ElementProvider.CARBON),
    C3Pr("C3'", ElementProvider.CARBON),
    C3St("C2*", ElementProvider.CARBON),
    C4("C4", ElementProvider.CARBON),
    C4Pr("C4'", ElementProvider.CARBON),
    C4St("C4*", ElementProvider.CARBON),
    C5("C5", ElementProvider.CARBON),
    C5Pr("C5'", ElementProvider.CARBON),
    C5St("C5*", ElementProvider.CARBON),
    C5M("C5M", ElementProvider.CARBON),
    C6("C6", ElementProvider.CARBON),
    C7("C7", ElementProvider.CARBON),
    C8("C8", ElementProvider.CARBON),
    C10("C10", ElementProvider.CARBON),
    C11("C11", ElementProvider.CARBON),
    C26("C26", ElementProvider.CARBON),
    C27("C27", ElementProvider.CARBON),
    C28("C28", ElementProvider.CARBON),
    C29("C29", ElementProvider.CARBON),
    C12("C12", ElementProvider.CARBON),
    C14("C14", ElementProvider.CARBON),
    C15("C15", ElementProvider.CARBON),
    C16("C16", ElementProvider.CARBON),
    C17("C17", ElementProvider.CARBON),
    C32("C32", ElementProvider.CARBON),
    C38("C38", ElementProvider.CARBON),
    C39("C39", ElementProvider.CARBON),
    C53("C53", ElementProvider.CARBON),
    C54("C54", ElementProvider.CARBON),
    C55("C55", ElementProvider.CARBON),
    C56("C56", ElementProvider.CARBON),
    CA("CA", ElementProvider.CARBON),
    CB("CB", ElementProvider.CARBON),
    CD("CD", ElementProvider.CARBON),
    CD1("CD1", ElementProvider.CARBON),
    CD2("CD2", ElementProvider.CARBON),
    CE("CE", ElementProvider.CARBON),
    CE1("CE1", ElementProvider.CARBON),
    CE2("CE2", ElementProvider.CARBON),
    CE3("CE3", ElementProvider.CARBON),
    CG("CG", ElementProvider.CARBON),
    CG1("CG1", ElementProvider.CARBON),
    CG2("CG2", ElementProvider.CARBON),
    CH2("CH2", ElementProvider.CARBON),
    CZ("CZ", ElementProvider.CARBON),
    CZ2("CZ2", ElementProvider.CARBON),
    CZ3("CZ3", ElementProvider.CARBON),

    // Nitrogen
    N("N", ElementProvider.NITROGEN),
    N1("N1", ElementProvider.NITROGEN),
    N2("N2", ElementProvider.NITROGEN),
    N3("N3", ElementProvider.NITROGEN),
    N4("N4", ElementProvider.NITROGEN),
    N5("N5", ElementProvider.NITROGEN),
    N6("N6", ElementProvider.NITROGEN),
    N7("N7", ElementProvider.NITROGEN),
    N9("N9", ElementProvider.NITROGEN),
    ND1("ND1", ElementProvider.NITROGEN),
    ND2("ND2", ElementProvider.NITROGEN),
    NE("NE", ElementProvider.NITROGEN),
    NE1("NE1", ElementProvider.NITROGEN),
    NE2("NE2", ElementProvider.NITROGEN),
    NH1("NH1", ElementProvider.NITROGEN),
    NH2("NH2", ElementProvider.NITROGEN),
    NZ("NZ", ElementProvider.NITROGEN),

    // Oxygen
    O("O", ElementProvider.OXYGEN),
    O1P("O1P", ElementProvider.OXYGEN),
    O2("O2", ElementProvider.OXYGEN),
    O2Pr("O2'", ElementProvider.OXYGEN),
    O2P("O2P", ElementProvider.OXYGEN),
    O3Pr("O3'", ElementProvider.OXYGEN),
    O3St("O3St", ElementProvider.OXYGEN),
    O3P("O3P", ElementProvider.OXYGEN),
    O4("O4", ElementProvider.OXYGEN),
    O4Pr("O4'", ElementProvider.OXYGEN),
    O4St("O4*", ElementProvider.OXYGEN),
    O5Pr("O5'", ElementProvider.OXYGEN),
    O5St("O5*", ElementProvider.OXYGEN),
    O6("O6", ElementProvider.OXYGEN),
    O9("O9", ElementProvider.OXYGEN),
    OD1("OD1", ElementProvider.OXYGEN),
    OD2("OD2", ElementProvider.OXYGEN),
    OE1("OE1", ElementProvider.OXYGEN),
    OE2("OE2", ElementProvider.OXYGEN),
    OG("OG", ElementProvider.OXYGEN),
    OG1("OG1", ElementProvider.OXYGEN),
    OG2("OG2", ElementProvider.OXYGEN),
    OH("OH", ElementProvider.OXYGEN),
    OP1("OP1", ElementProvider.OXYGEN),
    OP2("OP2", ElementProvider.OXYGEN),
    OP3("OP3", ElementProvider.OXYGEN),
    OXT("OXT", ElementProvider.OXYGEN),

    // Phosphorus
    P("P", ElementProvider.PHOSPHORUS),

    // Sulfur
    SG("SG", ElementProvider.SULFUR),
    SD("SD", ElementProvider.SULFUR),

    // non standard
    UNK("UNK", ElementProvider.UNKOWN),

    // artificial (representation types)
    CO("CO", ElementProvider.UNKOWN),
    LH("LH", ElementProvider.UNKOWN),
    SC("SC", ElementProvider.UNKOWN);

    public static final EnumSet<AtomName> ALANINE_ATOM_NAMES = EnumSet.of(H, H1, H2, HA, HB1, HB2, HB3, HN, HXT, THREEHB,
            D, D1, D2, D3, DA, DB1, DB2, DB3, C, CA, CB, N, O, OXT);


    public static final EnumSet<AtomName> ARGININE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HD1, HD2, HD3,
            HE, HG1, HG2, HG3, HH11, HH12, HH21, HH22, HN, HXT, TWOHB, TWOHD, TWOHG, D, D1, D2, D3, DA, DB2, DB3, DD2,
            DD3, DE, DG2, DG3, DH11, DH12, DH21, DH22, C, CA, CB, CD, CG, CZ, N, NE, NH1, NH2, O, OXT);


    public static final EnumSet<AtomName> ASPARAGINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA2, HA3, HB1, HB2, HB3, HD21,
            HD22, HDD1, HDD2, HN, HXT, TWOHG, D, D2, DA, DB2, DB3, DD21, DD22, DDD1, DDD2, C, CA, CB, CG, N, ND2, O,
            OD1, OXT);

    public static final EnumSet<AtomName> ASPARTIC_ACID_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA2, HA3, HB1, HB2, HB3, HD2,
            HN, HXT, TWOHB, D, D3, DA, DB2, DB3, DD2, C, CA, CB, CG, N, O, OD1, OD2, OXT);

    public static final EnumSet<AtomName> CYSTEINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HG, HB1, HB2, HB3, HN, HXT,
            TWOHB, D, DA, DG, DB2, DB3, C, CA, CB, N, O, OXT, SG);

    public static final EnumSet<AtomName> GLUTAMIC_ACID_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA2, HA3, HB1, HB2, HB3,
            HE2, HG1, HG2, HG3, HN, HXT, TWOHB, TWOHG, D, DA, DB2, DB3, DE2, DG2, DG3, C, CA, CB, CD, CG, N, O, OE1,
            OE2, OXT);

    public static final EnumSet<AtomName> GLUTAMINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HE21, HE22, HG1,
            HG2, HG3, HN, HT1, HT2, HT3, TWOHB, TWOHG, D, DA, DB2, DB3, DE21, DE22, DG2, DG3, C, CA, CB, CD, CG, N,
            NE2, O, OE1, OXT);

    public static final EnumSet<AtomName> GLYCINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA1, HA2, HA3, HB1, HB2, HB3,
            HN, HT1, HT2, HT3, HXT, D, D1, D2, D3, DA2, DA3, C, C10, C11, CA, CB, N, N3, O, O9, OXT);


    public static final EnumSet<AtomName> HISTIDINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA3, HB1, HB2, HB3, HD1, HD2,
            HD3, HE1, HE2, HN, TWOHB, D, D1, D2, D3, DA, DB2, DB3, DD1, DD2, DE1, DE2, C, CA, CB, CD2, CE1, CG, N, ND1,
            NE2, O, OXT);

    public static final EnumSet<AtomName> ISOLEUCINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB, HD1, HD11, HD12, HD13,
            HD2, HD3, HDD1, HDD2, HDD3, HG11, HG12, HG13, HG21, HG22, HG23, HN, HXT, D1, D2, D3, DA, DB, DD11, DD12,
            DD13, DD21, DDD1, DDD2, DDD3, DG12, DG13, DG21, DG22, DG23, C, CA, CB, CD, CD1, CG1, CG2, N, O, OXT);

    public static final EnumSet<AtomName> LEUCINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA2, HA3, HB1, HB2, HB3, HD11,
            HD12, HD13, HD21, HD22, HD23, HG, HN, HXT, TWOHB, D, DA, DB2, DB3, DD11, DD12, DD13, DD21, DD22, DD23, DG,
            C, C12, C14, C15, C16, C17, C32, C38, C39, C53, C54, C55, C56, CA, CB, CD1, CD2, CE, CG, N, N4, N7, O, O4,
            O6, OXT, SD);

    public static final EnumSet<AtomName> LYSINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA2, HA3, HB1, HB2, HB3, HD1,
            HD2, HD3, HD21, HD22, HD23, HE1, HE2, HE3, HG1, HG2, HG3, HN, HXT, HZ1, HZ2, HZ3, TWOHB, TWOHD, TWOHE,
            TWOHG, D, D1, DB2, DB3, DA, DB2, DB3, DD2, DD3, DE2, DE3, DG2, DG3, DXT, DZ1, DZ2, DZ3,
            C, CA, CB, CD, CE, CG, N, NZ, O, OXT);

    public static final EnumSet<AtomName> METHIONINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HE1, HE2, HE3,
            HG1, HG2, HG3, HN, HXT, ONEHT, TWOHB, TWOHG, TWOHG, THREEHT, D, D1, D2, D3, DA, DB2, DB3, DE1, DE2, DE3,
            DG2, DG3, C, CA, CB, CE, CG, N, O, OXT, SD);

    public static final EnumSet<AtomName> PHENYLALANINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HD1, HD2,
            HD3, HE1, HE2, HN, HXT, HZ, TWOHB, D, D1, D2, D3, DA, DB2, DB3, DD1, DD2, DE1, DE2, DZ, C, CA, CB, CD1, CD2,
            CE1, CE2, CG, CZ, N, O, OXT);

    public static final EnumSet<AtomName> PROLINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HD1, HD2, HD3,
            HG1, HG2, HG3, TWOHB, TWOHD, TWOHG, D, D2, DA, DB2, DB3, DD2, DD3, C, C5, C26, C27, C28, C29, CA, CB, CD,
            CG, N, N6, O, O2, OXT);

    public static final EnumSet<AtomName> SERINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HA2, HA3, HB1, HB2, HB3, HG, HN,
            HXT, TWOHB, D, D2, D3, DA, DB2, DB3, DG, C, CA, CB, N, O, OG, OXT);

    public static final EnumSet<AtomName> THREONINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HG1, HG21,
            HG22, HG23, HG3, HN, HXT, D, DA, DB, DG1, DG21, DG22, DG23, C, CA, CB, CG1, CG2, N, O, OG1, OG2, OXT);

    public static final EnumSet<AtomName> TRYPTOPHAN_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HD, HD1, HE1,
            HE3, HH, HH2, HN, HXT, HZ2, HZ3, TWOHB, D, DA, DB2, DB3, DD1, DE1, DE3, DH2, DZ2, DZ3, C, CA, CB, CD1,
            CD2, CE2, CE3, CG, CH2, CZ2, CZ3, N, NE1, O, OXT);

    public static final EnumSet<AtomName> TYROSINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB1, HB2, HB3, HD1, HD2, HD3,
            HE1, HE2, HH, HN, HXT, TWOHB, D, DA, DB2, DB3, DD1, DD2, DE1, DE2, DH, C, CA, CB, CD1, CD2, CE1, CE2, CG,
            CZ, N, O, OH, OXT);

    public static final EnumSet<AtomName> VALINE_ATOM_NAMES = EnumSet.of(H, H1, H2, H3, HA, HB, HB1, HB2, HB3, HG11, HG12,
            HG13, HG21, HG22, HG23, DG11, DG12, DG13, DG21, DG22, DG23, HN, HXT, D, D1, D2, D3, DA, DB, D, C, CA, CB,
            N, O, OXT);

    public static final EnumSet<AtomName> UNKNOWN_ATOM_NAMES = EnumSet.of(H, C, O, N);

    private final String name;
    private final Element element;

    AtomName(String name, Element element) {
        this.name = name;
        this.element = element;
    }

    public static AtomName getAtomNameFromString(String atomName) {
        // FIXME this can be sped up with a static hash map using the string atom name as key
        return Arrays.stream(values()).filter(name -> atomName.trim().equals(name.getName()))
                .findAny()
                .orElse(UNK);
    }

    public String getName() {
        return name;
    }

    public Element getElement() {
        return element;
    }

}
