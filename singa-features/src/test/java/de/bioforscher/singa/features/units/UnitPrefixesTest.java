package de.bioforscher.singa.features.units;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class UnitPrefixesTest {

    @Test
    public void shouldGenerateUnitsWithPrefixes() {
        List<String> unitStrings = UnitPrefixes.generateUnitNamesForPrefixes(UnitPrefix.getDefaultTimePrefixes(), UnitName.SECOND);
        assertEquals("ns", unitStrings.get(0));
        assertEquals("\u00B5s", unitStrings.get(1));
        assertEquals("ms", unitStrings.get(2));
        assertEquals("s", unitStrings.get(3));
    }

}