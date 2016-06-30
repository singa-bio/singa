package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.core.utility.Range;

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
    ELEMENT_SYMBOL(Range.of(79, 80));

    private final Range<Integer> columns;

    AtomToken(Range<Integer> columns) {
        this.columns = columns;
    }

    public Range<Integer> getColumns() {
        return columns;
    }

    public String extract(String line) {
        return extractValueFromPDBLine(line, this);
    }

    public static String extractValueFromPDBLine(String line, AtomToken atomToken) {
        // pdb numbering starts at column 1 - string starts at 0 - therefore -1
        // pdb numbering is including the last letter  - substring is excluding the last letter - therefore +1
        if (line.length() >= atomToken.getColumns().getLowerBound() + 1 && line.length() >= atomToken.getColumns()
                .getUpperBound() + 1) {
            return line.substring(
                    atomToken.getColumns().getLowerBound() - 1, atomToken.getColumns().getUpperBound() + 1).trim();
        } else {
            return "";
        }
    }
}
