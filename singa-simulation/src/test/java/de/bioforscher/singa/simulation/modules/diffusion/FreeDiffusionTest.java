package de.bioforscher.singa.simulation.modules.diffusion;

import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.model.FeatureOrigin;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import static junit.framework.TestCase.assertEquals;
import static tec.units.ri.unit.MetricPrefix.CENTI;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

/**
 * @author cl
 */
public class FreeDiffusionTest {

    @Test
    public void shouldScaleDiffusivityCorrectly() {
        Diffusivity diffusivity = new Diffusivity(1, FeatureOrigin.MANUALLY_ANNOTATED);
        // double time step - double the scaled quantity
        diffusivity.scale(Quantities.getQuantity(2, SECOND), Quantities.getQuantity(1, CENTI(METRE)));
        assertEquals(2.0, diffusivity.getScaledQuantity().getValue().doubleValue(), 0.0);
        // double space step - divide by square of length
        diffusivity.scale(Quantities.getQuantity(1, SECOND), Quantities.getQuantity(2, CENTI(METRE)));
        assertEquals(0.25, diffusivity.getScaledQuantity().getValue().doubleValue(), 0.0);
        // change both (5 * 1/(10*10))
        diffusivity.scale(Quantities.getQuantity(5, SECOND), Quantities.getQuantity(0.1, CENTI(METRE)));
        assertEquals(500, diffusivity.getScaledQuantity().getValue().doubleValue(), 0.0);
        // some other units
        diffusivity.scale(Quantities.getQuantity(5, MILLI(SECOND)), Quantities.getQuantity(0.1, MILLI(METRE)));
        assertEquals(50, diffusivity.getScaledQuantity().getValue().doubleValue(), 0.0);
    }

}
