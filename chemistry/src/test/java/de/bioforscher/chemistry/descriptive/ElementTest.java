package de.bioforscher.chemistry.descriptive;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Christoph on 05.06.2016.
 */
public class ElementTest {

    @Test
    public void shouldCreateAnion() {
        Element oxygenCation = ElementProvider.OXYGEN.asAnion(2);
        Assert.assertTrue(oxygenCation.isIon());
        Assert.assertTrue(oxygenCation.isAnion());
        Assert.assertFalse(oxygenCation.isCation());
    }

    @Test
    public void shouldCreateCation() {
        Element manganeseCation = ElementProvider.MANGANESE.asCation(2);
        Assert.assertTrue(manganeseCation.isIon());
        Assert.assertTrue(manganeseCation.isCation());
        Assert.assertFalse(manganeseCation.isAnion());
    }

    @Test
    public void shouldCreateIsotope() {
        Element uranium235 = ElementProvider.URANIUM.asIsotope(235);
        Assert.assertTrue(uranium235.isIsotope());
    }

}
