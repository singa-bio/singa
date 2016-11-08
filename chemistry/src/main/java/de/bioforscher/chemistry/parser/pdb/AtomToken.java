package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.core.utility.Range;
import de.bioforscher.mathematics.vectors.Vector3D;

import java.util.regex.Pattern;

/**
 * Created by Christoph on 23.06.2016.
 */
public enum AtomToken {

    ATOM_SERIAL(Range.of(7, 11)),
    ATOM_NAME(Range.of(13, 16)),
    ALTERNATE_LOCATION_INDICATOR(Range.of(17)),
    RESIDUE_NAME(Range.of(18, 20)),
    CHAIN_IDENTIFIER(Range.of(22)),
    RESIDUE_SERIAL(Range.of(23, 26)),
    RESIDUE_INSERTION(Range.of(27)),
    X_COORDINATE(Range.of(31, 38)),
    Y_COORDINATE(Range.of(39, 46)),
    Z_COORDINATE(Range.of(47, 54)),
    OCCUPANCY(Range.of(55, 60)),
    TEMPERATURE_FACTOR(Range.of(61, 66)),
    SEGMENT_IDENTIFIER(Range.of(77, 78)),
    ELEMENT_SYMBOL(Range.of(77, 78));

    /**
     * A pattern describing all record names associated with this token structure. Use this to filter for lines that are
     * parsable with this token.
     */
    public static final Pattern RECORD_PATTERN = Pattern.compile("^ATOM .*"); // TODO add HETATOM

    private final Range<Integer> columns;

    AtomToken(Range<Integer> columns) {
        this.columns = columns;
    }

    public static String extractValueFromPDBLine(String line, AtomToken atomToken) {
        // pdb numbering starts at column 1 - string starts at 0 - therefore -1
        // pdb numbering is including the last letter  - substring is excluding the last letter this account for the
        // offset
        if (line.length() >= atomToken.getColumns().getUpperBound()) {
            return line.substring(
                    atomToken.getColumns().getLowerBound()-1, atomToken.getColumns().getUpperBound()).trim();
        } else {
            return "";
        }
    }

    public Range<Integer> getColumns() {
        return this.columns;
    }

    public String extract(String line) {
        return extractValueFromPDBLine(line, this);
    }

    public static Atom assembleAtom(String atomLine) {
        // coordinates
        Double x = Double.valueOf(X_COORDINATE.extract(atomLine));
        Double y = Double.valueOf(Y_COORDINATE.extract(atomLine));
        Double z = Double.valueOf(Z_COORDINATE.extract(atomLine));
        Vector3D coordinates = new Vector3D(x, y, z);
        // serial
        Integer atomSerial = Integer.valueOf(ATOM_SERIAL.extract(atomLine));
        // atom name
        AtomName atomName = AtomName.getAtomNameFromString(ATOM_NAME.extract(atomLine))
                .orElseThrow(IllegalArgumentException::new);
        // element
        Element element = ElementProvider.getElementBySymbol(ELEMENT_SYMBOL.extract(atomLine))
                .orElseThrow(IllegalArgumentException::new);
        return new Atom(atomSerial, element, atomName, coordinates);
    }

}