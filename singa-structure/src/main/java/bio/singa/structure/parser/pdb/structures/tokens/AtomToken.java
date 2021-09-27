package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.chemistry.model.elements.Element;
import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.structure.model.oak.LeafIdentifier;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.oak.OakAtom;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The atom and hetatm token. Containing information of atoms.
 */
public enum AtomToken implements PDBToken {

    RECORD_TYPE(Range.of(1, 6), Justification.LEFT),
    ATOM_SERIAL(Range.of(7, 11), Justification.RIGHT),
    ATOM_NAME(Range.of(13, 16), Justification.LEFT),
    ALTERNATE_LOCATION_INDICATOR(Range.of(17), Justification.LEFT),
    RESIDUE_NAME(Range.of(18, 20), Justification.RIGHT),
    CHAIN_IDENTIFIER(Range.of(22), Justification.LEFT),
    RESIDUE_SERIAL(Range.of(23, 26), Justification.RIGHT),
    RESIDUE_INSERTION(Range.of(27, 30), Justification.LEFT),
    X_COORDINATE(Range.of(31, 38), Justification.RIGHT),
    Y_COORDINATE(Range.of(39, 46), Justification.RIGHT),
    Z_COORDINATE(Range.of(47, 54), Justification.RIGHT),
    OCCUPANCY(Range.of(55, 60), Justification.RIGHT),
    TEMPERATURE_FACTOR(Range.of(61, 66), Justification.RIGHT),
    ELEMENT_SYMBOL(Range.of(77, 78), Justification.RIGHT),
    ELEMENT_CHARGE(Range.of(79, 80), Justification.LEFT);

    public static final Pattern RECORD_PATTERN = Pattern.compile("^(ATOM|HETATM).*");

    private static final DecimalFormat coordinateFormat = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));
    private static DecimalFormat temperatureFormat = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));

    private final Range<Integer> columns;
    private final Justification justification;

    AtomToken(Range<Integer> columns, Justification justification) {
        this.columns = columns;
        this.justification = justification;
    }

    public static OakAtom assembleAtom(String atomLine) {
        // coordinates
        double x = Double.parseDouble(X_COORDINATE.extract(atomLine));
        double y = Double.parseDouble(Y_COORDINATE.extract(atomLine));
        double z = Double.parseDouble(Z_COORDINATE.extract(atomLine));
        Vector3D coordinates = new Vector3D(x, y, z);
        // serial
        int atomSerial = Integer.parseInt(ATOM_SERIAL.extract(atomLine));
        // atom name string
        String atomName = ATOM_NAME.extract(atomLine);
        // element
        Element element = ElementProvider.getElementBySymbol(ELEMENT_SYMBOL.extract(atomLine))
                .orElse(ElementProvider.UNKOWN);
        // bfactor
        double bFactor = Double.parseDouble(TEMPERATURE_FACTOR.extract(atomLine));
        OakAtom atom = new OakAtom(atomSerial, element, atomName, coordinates);
        atom.setBFactor(bFactor);
        return atom;
    }

    public static List<String> assemblePDBLine(LeafSubstructure leaf) {
        List<String> lines = new ArrayList<>();
        for (Atom atom : leaf.getAllAtoms()) {
            StringBuilder currentLine = new StringBuilder();
            if (!leaf.isAnnotatedAsHeteroAtom()) {
                currentLine.append(RECORD_TYPE.createTokenString("ATOM"));
            } else {
                currentLine.append(RECORD_TYPE.createTokenString("HETATM"));
            }
            currentLine.append(ATOM_SERIAL.createTokenString(String.valueOf(atom.getAtomIdentifier())))
                    .append(" ")
                    .append(formatAtomName(atom))
                    .append(" ") // ALTERNATE_LOCATION_INDICATOR not yet implemented
                    .append(RESIDUE_NAME.createTokenString(leaf.getThreeLetterCode().toUpperCase()))
                    .append(" ")
                    .append(leaf.getChainIdentifier())
                    .append(RESIDUE_SERIAL.createTokenString(String.valueOf(leaf.getIdentifier().getSerial())))
                    .append(RESIDUE_INSERTION.createTokenString(String.valueOf(
                            leaf.getInsertionCode() == LeafIdentifier.DEFAULT_INSERTION_CODE
                                    ? " " : leaf.getInsertionCode())))
                    .append(X_COORDINATE.createTokenString(coordinateFormat.format(atom.getPosition().getX())))
                    .append(Y_COORDINATE.createTokenString(coordinateFormat.format(atom.getPosition().getY())))
                    .append(Z_COORDINATE.createTokenString(coordinateFormat.format(atom.getPosition().getZ())))
                    .append("  1.00") // OCCUPANCY not yet implemented
                    .append(TEMPERATURE_FACTOR.createTokenString(temperatureFormat.format(atom.getBFactor()))) // TEMPERATURE_FACTOR not yet implemented
                    .append("          ") // 10 spaces
                    .append(ELEMENT_SYMBOL.createTokenString(atom.getElement().getSymbol()).toUpperCase())
                    .append(formatCharge(atom.getElement()));
            lines.add(currentLine.toString());
        }
        return lines;
    }

    static String formatAtomName(Atom atom) {
        String fullName = null;
        String name = atom.getAtomName();
        String element = atom.getElement().getSymbol();

        // RULES FOR ATOM NAME PADDING: 4 columns in total: 13, 14, 15, 16
        // if length 4: nothing to do
        if (name.length() == 4) {
            fullName = name;
        } else if (name.length() == 3) {
            fullName = " " + name;
        } else if (name.length() == 2) {
            if (element.equals("C") || element.equals("N") || element.equals("O") || element.equals("P") || element.equals("S")
                    // this is for pseudo atoms that are labeled with CA and unknown Element
                    || atom.getAtomName().equals("CA")) {
                fullName = " " + name + " ";
            } else {
                fullName = name + "  ";
            }
        } else if (name.length() == 1) {
            fullName = " " + name + "  ";
        }

        return fullName;
    }

    private static String formatCharge(Element element) {
        int charge = element.getCharge();
        if (charge > 0) {
            return charge + "+";
        }
        if (charge < 0) {
            return Math.abs(charge) + "-";
        }
        return "  ";
    }

    @Override
    public Range<Integer> getColumns() {
        return columns;
    }

    @Override
    public Pattern getRecordNamePattern() {
        return RECORD_PATTERN;
    }

    private String createTokenString(String content) {
        int totalLength = columns.getUpperBound() - columns.getLowerBound() - content.length();
        StringBuilder filler = new StringBuilder();
        for (int i = 0; i < totalLength + 1; i++) {
            filler.append(" ");
        }
        if (justification == Justification.LEFT) {
            return content + filler;
        }
        return filler + content;
    }

}