package bio.singa.simulation.model.sections;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.units.UnitRegistry;
import org.junit.jupiter.api.Test;
import tec.uom.se.quantity.Quantities;

import java.util.Map;
import java.util.Set;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static bio.singa.simulation.model.sections.CellTopology.OUTER;
import static org.junit.jupiter.api.Assertions.*;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
class ConcentrationContainerTest {

    private static final CellSubsection subsectionA = new CellSubsection("Test A");
    private static final CellSubsection subsectionB = new CellSubsection("Test B");

    private static final ChemicalEntity entityA = SmallMolecule.create("A").build();
    private static final ChemicalEntity entityB = SmallMolecule.create("B").build();

    @Test
    void initializeSubsection() {
        ConcentrationContainer container = new ConcentrationContainer();
        container.initializeSubsection(subsectionA, INNER);

        CellSubsection innerSubsection = container.getInnerSubsection();
        assertNull(container.getOuterSubsection());
        assertNull(container.getMembraneSubsection());
        assertEquals("Test A", innerSubsection.getIdentifier());
        assertEquals(container.getPoolsOfConcentration().size(), 1);
    }

    @Test
    void putSubsectionPool() {
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);

        ConcentrationContainer containerB = new ConcentrationContainer();
        containerB.initializeSubsection(subsectionB, INNER);
        containerB.set(INNER, entityA, 1.0);

        Map.Entry<CellSubsection, ConcentrationPool> pool = containerB.getPool(INNER);
        containerA.putSubsectionPool(pool.getKey(), OUTER, pool.getValue());

        Map.Entry<CellSubsection, ConcentrationPool> poolFromA = containerA.getPool(OUTER);
        Map.Entry<CellTopology, ConcentrationPool> poolFromB = containerB.getPool(subsectionB);

        assertSame(poolFromA.getValue(), poolFromB.getValue());
        assertEquals(containerA.get(OUTER, entityA), containerB.get(INNER, entityA));
    }

    @Test
    void removeSubsection() {
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);
        containerA.initializeSubsection(subsectionB, OUTER);

        assertNotNull(containerA.getOuterSubsection());
        containerA.removeSubsection(subsectionB);
        assertNull(containerA.getOuterSubsection());
    }

    @Test
    void getReferencedSubSections() {
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);
        containerA.initializeSubsection(subsectionB, OUTER);

        assertEquals(containerA.getReferencedSubSections().size(), 2);
    }

    @Test
    void getPoolsOfConcentration() {
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);
        containerA.initializeSubsection(subsectionB, OUTER);

        assertEquals(containerA.getPoolsOfConcentration().size(), 2);
    }

    @Test
    void getReferencedEntities() {
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);
        containerA.initializeSubsection(subsectionB, OUTER);

        containerA.set(INNER, entityA, 1.0);
        containerA.set(OUTER, entityB, 0.5);

        Set<ChemicalEntity> referencedEntities = containerA.getReferencedEntities();
        assertEquals(referencedEntities.size(), 2);
        assertTrue(referencedEntities.contains(entityA));
        assertTrue(referencedEntities.contains(entityB));
    }

    @Test
    void emptyCopy() {
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);
        containerA.initializeSubsection(subsectionB, OUTER);

        containerA.set(INNER, entityA, 1.0);
        containerA.set(OUTER, entityB, 0.5);

        ConcentrationContainer copy = containerA.emptyCopy();

        assertSame(copy.getInnerSubsection(), containerA.getInnerSubsection());
        assertSame(copy.getOuterSubsection(), containerA.getOuterSubsection());

        assertEquals(0.0, copy.get(INNER, entityA));
        assertEquals(0.0, copy.get(INNER, entityA));
    }

    @Test
    void shouldSetAndGetInCorrectUnit() {
        UnitRegistry.setSpace(Quantities.getQuantity(1.0, MICRO(METRE)));
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);
        containerA.initializeSubsection(subsectionB, OUTER);

        containerA.initialize(INNER, entityA, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        containerA.initialize(subsectionB, entityB, Quantities.getQuantity(0.5, MOLE_PER_LITRE));

        assertEquals(1.0, UnitRegistry.concentration(containerA.get(INNER, entityA)).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(1.0, UnitRegistry.concentration(containerA.get(subsectionA, entityA)).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.5, UnitRegistry.concentration(containerA.get(OUTER, entityB)).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.5, UnitRegistry.concentration(containerA.get(subsectionB, entityB)).to(MOLE_PER_LITRE).getValue().doubleValue());
    }


}