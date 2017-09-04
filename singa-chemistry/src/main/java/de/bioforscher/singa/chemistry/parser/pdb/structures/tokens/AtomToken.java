package de.bioforscher.singa.chemistry.parser.pdb.structures.tokens;

import de.bioforscher.singa.chemistry.descriptive.elements.Element;
import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.RegularAtom;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.core.utility.Range;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static de.bioforscher.singa.chemistry.parser.pdb.structures.tokens.Justification.LEFT;
import static de.bioforscher.singa.chemistry.parser.pdb.structures.tokens.Justification.RIGHT;

public enum AtomToken implements PDBToken {

    RECORD_TYPE(Range.of(1, 6), LEFT),
    ATOM_SERIAL(Range.of(7, 11), RIGHT),
    ATOM_NAME(Range.of(13, 16), LEFT),
    ALTERNATE_LOCATION_INDICATOR(Range.of(17), LEFT),
    RESIDUE_NAME(Range.of(18, 20), RIGHT),
    CHAIN_IDENTIFIER(Range.of(22), LEFT),
    RESIDUE_SERIAL(Range.of(23, 26), RIGHT),
    RESIDUE_INSERTION(Range.of(27), LEFT) {
        @Override
        public String extract(String line) {
            if (line.length() >= getColumns().getUpperBound()) {
                return line.substring(
                        getColumns().getLowerBound() - 1, getColumns().getUpperBound());
            } else {
                return "";
            }
        }
    },
    X_COORDINATE(Range.of(31, 38), RIGHT),
    Y_COORDINATE(Range.of(39, 46), RIGHT),
    Z_COORDINATE(Range.of(47, 54), RIGHT),
    OCCUPANCY(Range.of(55, 60), RIGHT),
    TEMPERATURE_FACTOR(Range.of(61, 66), RIGHT),
    ELEMENT_SYMBOL(Range.of(77, 78), RIGHT),
    ELEMENT_CHARGE(Range.of(79, 80), LEFT);

    /**
     * @author cl
     */
    /**
     * A pattern describing all record names associated with this token structure. Use this to filter for lines that are
     * parsable with this token.
     */
    public static final Pattern RECORD_PATTERN = Pattern.compile("^(ATOM|HETATM).*");
    private static DecimalFormat coordinateFormat = new DecimalFormat("0.000",
            new DecimalFormatSymbols(Locale.US));
    private static DecimalFormat temperatureFormat = new DecimalFormat("0.00",
            new DecimalFormatSymbols(Locale.US));

    private final Range<Integer> columns;
    private final Justification justification;

    AtomToken(Range<Integer> columns, Justification justification) {
        this.columns = columns;
        this.justification = justification;
    }

    public static Atom assembleAtom(String atomLine) {
        // coordinates
        Double x = Double.valueOf(X_COORDINATE.extract(atomLine));
        Double y = Double.valueOf(Y_COORDINATE.extract(atomLine));
        Double z = Double.valueOf(Z_COORDINATE.extract(atomLine));
        Vector3D coordinates = new Vector3D(x, y, z);
        // serial
        Integer atomSerial = Integer.valueOf(ATOM_SERIAL.extract(atomLine));
        // atom name string
        String atomName = ATOM_NAME.extract(atomLine);
        // element
        Element element = ElementProvider.getElementBySymbol(ELEMENT_SYMBOL.extract(atomLine))
                .orElse(ElementProvider.UNKOWN);
        return new RegularAtom(atomSerial, element, atomName, coordinates);
    }

    public static List<String> assemblePDBLine(LeafSubstructure<?, ?> leaf) {
        List<String> lines = new ArrayList<>();
        for (Atom atom : leaf.getNodes()) {
            StringBuilder currentLine = new StringBuilder();
            if (!leaf.isAnnotatedAsHetAtom()) {
                currentLine.append(RECORD_TYPE.createTokenString("ATOM"));
            } else {
                currentLine.append(RECORD_TYPE.createTokenString("HETATM"));
            }
            currentLine.append(ATOM_SERIAL.createTokenString(String.valueOf(atom.getIdentifier())))
                    .append(" ")
                    .append(formatAtomName(atom))
                    .append(" ") // ALTERNATE_LOCATION_INDICATOR not yet implemented
                    .append(RESIDUE_NAME.createTokenString(leaf.getName().toUpperCase()))
                    .append(" ")
                    .append(leaf.getChainIdentifier())
                    .append(RESIDUE_SERIAL.createTokenString(String.valueOf(leaf.getIdentifier().getSerial())))
                    .append("    ") // RESIDUE_INSERTION not yet implemented + 3 spaces for coordinates
                    .append(X_COORDINATE.createTokenString(coordinateFormat.format(atom.getPosition().getX())))
                    .append(Y_COORDINATE.createTokenString(coordinateFormat.format(atom.getPosition().getY())))
                    .append(Z_COORDINATE.createTokenString(coordinateFormat.format(atom.getPosition().getZ())))
                    .append("  1.00") // OCCUPANCY not yet implemented
                    .append("  0.00") // TEMPERATURE_FACTOR not yet implemented
                    .append("          ") // 10 spaces
                    .append(ELEMENT_SYMBOL.createTokenString(atom.getElement().getSymbol()))
                    .append(formatCharge(atom.getElement()));
            lines.add(currentLine.toString());
        }
        return lines;
    }

    static String formatAtomName(Atom atom) {
        String fullName = null;
        String name = atom.getAtomNameString();
        String element = atom.getElement().getSymbol();

        // RULES FOR ATOM NAME PADDING: 4 columns in total: 13, 14, 15, 16

        // if length 4: nothing to do
        if (name.length() == 4) {
            fullName = name;
        } else if (name.length() == 3) {
            fullName = " " + name;
        } else if (name.length() == 2) {
            if (element.equals("C") || element.equals("N") || element.equals("O") || element.equals("P") || element.equals("S")) {
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
            return String.valueOf(charge) + "+";
        }
        if (charge < 0) {
            return String.valueOf(Math.abs(charge)) + "-";
        }
        return "  ";
    }

    @Override
    public Range<Integer> getColumns() {
        return this.columns;
    }

    @Override
    public Pattern getRecordNamePattern() {
        return RECORD_PATTERN;
    }

    private String createTokenString(String content) {
        int totalLength = this.columns.getUpperBound() - this.columns.getLowerBound() - content.length();
        StringBuilder filler = new StringBuilder();
        for (int i = 0; i < totalLength + 1; i++) {
            filler.append(" ");
        }
        if (this.justification == LEFT) {
            return content + filler;
        }
        return filler + content;
    }

}