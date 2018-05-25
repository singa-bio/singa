package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.layer.Vesicle;
import org.junit.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.concurrent.ThreadLocalRandom;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class VesicleTransportTest {

    private static final EnclosedCompartment cytoplasm = new EnclosedCompartment("cytoplasm", "cytoplasm");
    private static final ChemicalEntity water = SmallMolecule.create("water").build();

    @Test
    public void shouldTransformConcentration() {

        Environment.setSystemExtend(Quantities.getQuantity(20, MICRO(METRE)));
        Environment.setSimulationExtend(500);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        Vesicle vesicle = new Vesicle("0",
                new Vector2D(50, 50),
                Quantities.getQuantity(ThreadLocalRandom.current().nextDouble(100, 200), NANO(METRE)),
                cytoplasm);

        ComparableQuantity<MolarConcentration> originalQuantity = Quantities.getQuantity(10.0, MOLE_PER_LITRE);
        vesicle.setConcentration(water, originalQuantity);
        Quantity<MolarConcentration> concentration = vesicle.getConcentration(water);
        Quantity<MolarConcentration> transformedQuantity = concentration.to(MOLE_PER_LITRE);
        assertEquals(originalQuantity.getValue().doubleValue(), transformedQuantity.getValue().doubleValue(), 1e-8);

        Environment.reset();
    }

    @Test
    public void testSetup() {

    }





}
