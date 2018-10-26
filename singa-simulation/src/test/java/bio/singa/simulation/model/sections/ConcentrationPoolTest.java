package bio.singa.simulation.model.sections;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import org.junit.jupiter.api.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import java.util.Set;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class ConcentrationPoolTest {

    private static final ChemicalEntity entityA = SmallMolecule.create("A").build();
    private static final ChemicalEntity entityB = SmallMolecule.create("B").build();

    @Test
    void getReferencedEntities() {
        ConcentrationPool pool = new ConcentrationPool();
        ComparableQuantity<MolarConcentration> expected = Quantities.getQuantity(1.0, MOLE_PER_LITRE);
        pool.set(entityA, expected);
        Set<ChemicalEntity> entities = pool.getReferencedEntities();
        assertEquals(entities.size(), 1);
        assertTrue(entities.contains(entityA));
    }

    @Test
    void testGetAndSet() {
        Environment.reset();
        ConcentrationPool pool = new ConcentrationPool();
        ComparableQuantity<MolarConcentration> expected = Quantities.getQuantity(1.0, MOLE_PER_LITRE);
        pool.set(entityA, expected);
        assertEquals(expected, pool.get(entityA));
        assertEquals(Environment.emptyConcentration(), pool.get(entityB));
    }

}