import de.bioforscher.singa.units.UnitName;
import de.bioforscher.singa.units.UnitPrefix;
import de.bioforscher.singa.units.UnitPrefixes;
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
