package de.bioforscher.singa.simulation.model.sections;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.entities.SmallMolecule;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import org.junit.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import java.util.Set;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class ConcentrationPoolTest {

    private static ChemicalEntity entityA = SmallMolecule.create("A").build();
    private static ChemicalEntity entityB = SmallMolecule.create("B").build();

    @Test
    public void getReferencedEntities() {
        ConcentrationPool pool = new ConcentrationPool();
        ComparableQuantity<MolarConcentration> expected = Quantities.getQuantity(1.0, MOLE_PER_LITRE);
        pool.set(entityA, expected);
        Set<ChemicalEntity> entities = pool.getReferencedEntities();
        assertEquals(entities.size(), 1);
        assertTrue(entities.contains(entityA));
    }

    @Test
    public void testGetAndSet() {
        Environment.reset();
        ConcentrationPool pool = new ConcentrationPool();
        ComparableQuantity<MolarConcentration> expected = Quantities.getQuantity(1.0, MOLE_PER_LITRE);
        pool.set(entityA, expected);
        assertEquals(expected, pool.get(entityA));
        assertEquals(Environment.emptyConcentration(), pool.get(entityB));
    }

}