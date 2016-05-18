import de.bioforscher.units.UnitName;
import de.bioforscher.units.UnitPrefix;
import de.bioforscher.units.UnitUtilities;
import org.junit.Test;

/**
 * Created by Christoph on 12.05.2016.
 */
public class UnitUtilitiesTest {

    @Test
    public void shouldGenerateUnitsWithPrefixes() {
        UnitUtilities.generateUnitNamesForPrefixes(UnitPrefix
                .getDefaultTimePrefixes(), UnitName.SECOND).forEach(System.out::println);
    }

}
