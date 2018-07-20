package de.bioforscher.singa.simulation.model.sections;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.entities.SmallMolecule;
import de.bioforscher.singa.features.parameters.Environment;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import java.util.Map;
import java.util.Set;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.sections.CellTopology.INNER;
import static de.bioforscher.singa.simulation.model.sections.CellTopology.OUTER;
import static org.junit.Assert.*;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class ConcentrationContainerTest {

    private static CellSubsection subsectionA = new CellSubsection("Test A");
    private static CellSubsection subsectionB = new CellSubsection("Test B");

    private static ChemicalEntity entityA = SmallMolecule.create("A").build();
    private static ChemicalEntity entityB = SmallMolecule.create("B").build();

    @Test
    public void initializeSubsection() {
        ConcentrationContainer container = new ConcentrationContainer();
        container.initializeSubsection(subsectionA, INNER);

        CellSubsection innerSubsection = container.getInnerSubsection();
        assertNull(container.getOuterSubsection());
        assertNull(container.getMembraneSubsection());
        assertEquals("Test A", innerSubsection.getIdentifier());
        assertEquals(container.getPoolsOfConcentration().size(), 1);
    }

    @Test
    public void putSubsectionPool() {
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
    public void removeSubsection() {
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);
        containerA.initializeSubsection(subsectionB, OUTER);

        assertNotNull(containerA.getOuterSubsection());
        containerA.removeSubsection(subsectionB);
        assertNull(containerA.getOuterSubsection());
    }

    @Test
    public void getReferencedSubSections() {
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);
        containerA.initializeSubsection(subsectionB, OUTER);

        assertEquals(containerA.getReferencedSubSections().size(), 2);
    }

    @Test
    public void getPoolsOfConcentration() {
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);
        containerA.initializeSubsection(subsectionB, OUTER);

        assertEquals(containerA.getPoolsOfConcentration().size(), 2);
    }

    @Test
    public void getReferencedEntities() {
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
    public void emptyCopy() {
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);
        containerA.initializeSubsection(subsectionB, OUTER);

        containerA.set(INNER, entityA, 1.0);
        containerA.set(OUTER, entityB, 0.5);

        ConcentrationContainer copy = containerA.emptyCopy();

        assertSame(copy.getInnerSubsection(), containerA.getInnerSubsection());
        assertSame(copy.getOuterSubsection(), containerA.getOuterSubsection());

        assertEquals(Environment.emptyConcentration(), copy.get(INNER, entityA));
        assertEquals(Environment.emptyConcentration(), copy.get(INNER, entityA));
    }

    @Test
    public void shouldSetAndGetInCorrectUnit() {
        Environment.setNodeDistance(Quantities.getQuantity(1.0, MICRO(METRE)));
        ConcentrationContainer containerA = new ConcentrationContainer();
        containerA.initializeSubsection(subsectionA, INNER);
        containerA.initializeSubsection(subsectionB, OUTER);

        containerA.set(INNER, entityA, 1.0);
        containerA.set(subsectionB, entityB, 0.5);

        assertEquals(1.0, containerA.get(INNER, entityA).to(MOLE_PER_LITRE).getValue().doubleValue(), 0.0);
        assertEquals(1.0, containerA.get(subsectionA, entityA).to(MOLE_PER_LITRE).getValue().doubleValue(), 0.0);
        assertEquals(0.5, containerA.get(OUTER, entityB).to(MOLE_PER_LITRE).getValue().doubleValue(), 0.0);
        assertEquals(0.5, containerA.get(subsectionB, entityB).to(MOLE_PER_LITRE).getValue().doubleValue(), 0.0);
        Environment.reset();
    }


}