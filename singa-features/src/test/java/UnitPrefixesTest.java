import de.bioforscher.singa.features.units.UnitName;
import de.bioforscher.singa.features.units.UnitPrefix;
import de.bioforscher.singa.features.units.UnitPrefixes;
import org.junit.Test;

/**
 * @author cl
 */
public class UnitPrefixesTest {

    @Test
    public void shouldGenerateUnitsWithPrefixes() {
        UnitPrefixes.generateUnitNamesForPrefixes(UnitPrefix
                .getDefaultTimePrefixes(), UnitName.SECOND).forEach(System.out::println);
    }

}
