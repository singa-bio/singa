package bio.singa.chemistry.elements;

import bio.singa.structure.elements.Element;
import bio.singa.structure.elements.ElementProvider;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class ElementTest {

    @Test
    void shouldCreateAnion() {
        Element oxygenCation = ElementProvider.OXYGEN.asAnion(2);
        assertTrue(oxygenCation.isIon());
        assertTrue(oxygenCation.isAnion());
        assertFalse(oxygenCation.isCation());
    }

    @Test
    void shouldCreateCation() {
        Element manganeseCation = ElementProvider.MANGANESE.asCation(2);
        assertTrue(manganeseCation.isIon());
        assertTrue(manganeseCation.isCation());
        assertFalse(manganeseCation.isAnion());
    }

    @Test
    void shouldCreateIsotope() {
        Element uranium235 = ElementProvider.URANIUM.asIsotope(235);
        assertTrue(uranium235.isIsotope());
    }

    @Test
    void shouldCalculateCorrectNumberOfPotentialBonds() {
        assertEquals(ElementProvider.HYDROGEN.getNumberOfPotentialBonds(), 1);
        assertEquals(ElementProvider.BORON.getNumberOfPotentialBonds(), 3);
        assertEquals(ElementProvider.NITROGEN.getNumberOfPotentialBonds(), 3);
        assertEquals(ElementProvider.CARBON.getNumberOfPotentialBonds(), 4);
        assertEquals(ElementProvider.SULFUR.getNumberOfPotentialBonds(), 2);
        assertEquals(ElementProvider.OXYGEN.getNumberOfPotentialBonds(), 2);
        assertEquals(ElementProvider.CHLORINE.getNumberOfPotentialBonds(), 1);
    }


}
