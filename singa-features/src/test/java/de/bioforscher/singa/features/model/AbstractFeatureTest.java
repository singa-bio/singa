package de.bioforscher.singa.features.model;

import de.bioforscher.singa.features.quantities.MolarVolume;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

/**
 * @author cl
 */
public class AbstractFeatureTest {

    @Test
    public void stringLabel() {

        MolarVolume molarVolume = new MolarVolume(Quantities.getQuantity(0.5, MolarVolume.CUBIC_METRE_PER_MOLE), FeatureOrigin.MANUALLY_ANNOTATED);
        System.out.println(molarVolume);



    }
}