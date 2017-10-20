package de.bioforscher.singa.descriptive.elements;

import de.bioforscher.singa.chemistry.descriptive.elements.Element;
import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class ElementTest {

    @Test
    public void shouldCreateAnion() {
        Element oxygenCation = ElementProvider.OXYGEN.asAnion(2);
        assertTrue(oxygenCation.isIon());
        assertTrue(oxygenCation.isAnion());
        assertFalse(oxygenCation.isCation());
    }

    @Test
    public void shouldCreateCation() {
        Element manganeseCation = ElementProvider.MANGANESE.asCation(2);
        assertTrue(manganeseCation.isIon());
        assertTrue(manganeseCation.isCation());
        assertFalse(manganeseCation.isAnion());
    }

    @Test
    public void shouldCreateIsotope() {
        Element uranium235 = ElementProvider.URANIUM.asIsotope(235);
        assertTrue(uranium235.isIsotope());
    }

    @Test
    public void shouldCalculateCorrectNumberOfPotentialBonds() {
        assertEquals(ElementProvider.HYDROGEN.getNumberOfPotentialBonds(), 1, 0);
        assertEquals(ElementProvider.BORON.getNumberOfPotentialBonds(), 3, 0);
        assertEquals(ElementProvider.NITROGEN.getNumberOfPotentialBonds(), 3, 0);
        assertEquals(ElementProvider.CARBON.getNumberOfPotentialBonds(), 4, 0);
        assertEquals(ElementProvider.SULFUR.getNumberOfPotentialBonds(), 2, 0);
        assertEquals(ElementProvider.OXYGEN.getNumberOfPotentialBonds(), 2, 0);
        assertEquals(ElementProvider.CHLORINE.getNumberOfPotentialBonds(), 1, 0);
    }




}
