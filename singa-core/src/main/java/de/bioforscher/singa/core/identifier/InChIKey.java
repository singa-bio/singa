package de.bioforscher.singa.core.identifier;

import de.bioforscher.singa.core.identifier.model.AbstractIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;

import java.util.regex.Pattern;

/**
 * The InChIKey, or hashed InChI, is a fixed length (25 character) condensed digital representation of the InChI.
 * <p>
 * From the official documents:
 * The InChIKey is a character signature based on a hash code of the InChI string. Also, this hash
 * may serve as a checksum for verifying InChI, for example, after transmission over a network."
 * <p>
 * InChIKey has four (4) distinct components: a 14-character hash of the basic (Mobile-H)
 * InChI layer (without /p segment accounting for added or removed protons); a 8-character
 * hash of the remaining layers; a 1 character is a flag indicating selected features (e.g.
 * presence of fixed-H layer); a 1 character is a “check” character. The overall length of
 * InChIKey is fixed at 25 characters, including separator:
 * AAAAAAAAAAAAAA-BBBBBBBBCD
 * <p>
 * This is significantly shorter than a typical InChI string (for example, the average length
 * of InChI string for Pubchem collection is 146 characters).
 * <p>
 * InChIKey layout is as follows:
 * <p>
 * AAAAAAAAAAAAAA <br>
 * First block (14 letters)<br>
 * Encodes molecular skeleton (connectivity)<br>
 * <p>
 * BBBBBBBB<br>
 * Second block (8 letters)<br>
 * Encodes proton positions (tautomers), stereochemistry, isotopomers, reconnected layer<br>
 * <p>
 * C<br>
 * Flag character<br>
 * Indicates InChI version, presence of a fixed-H layer, isotopes, and stereochemical information.<br><br>
 * D<br>
 * Check character, obtained from all symbols except delimiters
 * <p>
 * All symbols except the delimiter (a dash, that is, a minus) are uppercase English letters
 * representing a “base-26” encoding.
 */
public class InChIKey extends AbstractIdentifier {

    // GZUITABIAKMVPG-UHFFFAOYSA-N
    public static final Pattern PATTERN = Pattern.compile("[A-Z]{14}-[A-Z]{10}-N");

    public InChIKey(String key) throws IllegalArgumentException {
        super(key, PATTERN);
    }

    public static boolean check(Identifier identifier) {
        return PATTERN.matcher(identifier.toString()).matches();
    }

    public static Pattern getPattern() {
        return PATTERN;
    }
}
