package bio.singa.features.model;

import bio.singa.features.quantities.MolarVolume;
import org.junit.jupiter.api.Test;
import tec.uom.se.quantity.Quantities;

/**
 * @author cl
 */
class AbstractFeatureTest {

    @Test
    void stringLabel() {

        MolarVolume molarVolume = new MolarVolume(Quantities.getQuantity(0.5, MolarVolume.CUBIC_METRE_PER_MOLE), FeatureOrigin.MANUALLY_ANNOTATED);
        System.out.println(molarVolume);



    }
}