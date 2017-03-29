import de.bioforscher.units.UnitName;
import de.bioforscher.units.UnitPrefix;
import de.bioforscher.units.UnitPrefixes;
import org.junit.Test;

/**
 * Created by Christoph on 12.05.2016.
 */
public class UnitPrefixesTest {

    @Test
    public void shouldGenerateUnitsWithPrefixes() {
        UnitPrefixes.generateUnitNamesForPrefixes(UnitPrefix
                .getDefaultTimePrefixes(), UnitName.SECOND).forEach(System.out::println);
    }

}
