package bio.singa.simulation.model.sections;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.parameters.Environment;
import org.junit.jupiter.api.Test;
import bio.singa.features.units.UnitRegistry;
import tec.uom.se.quantity.Quantities;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
class CellRegionTest {

    private final ChemicalEntity entityA = new SmallMolecule.Builder("A").build();
    private final ChemicalEntity entityB = new SmallMolecule.Builder("B").build();
    private final ChemicalEntity entityC = new SmallMolecule.Builder("C").build();


    @Test
    void resembleSingleContainer() {
        // set environment
        UnitRegistry.setSpace(Quantities.getQuantity(1.0, MICRO(METRE)));
        // create region
        CellRegion region = new CellRegion("Cytoplasm");
        region.addSubSection(CellTopology.INNER, CellSubsection.SECTION_A);
        ConcentrationContainer concentrationContainer = region.setUpConcentrationContainer();
        // set concentration
        concentrationContainer.set(CellSubsection.SECTION_A, entityA, 1.0);
        // retrieve values
        assertEquals(1.0, concentrationContainer.get(CellSubsection.SECTION_A, entityA).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.0, concentrationContainer.get(CellSubsection.SECTION_B, entityA).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.0, concentrationContainer.get(CellSubsection.SECTION_A, entityB).to(MOLE_PER_LITRE).getValue().doubleValue());
        // reset environment
        Environment.reset();
    }

    @Test
    void resembleMembraneContainer() {
        // set environment
        UnitRegistry.setSpace(Quantities.getQuantity(1.0, MICRO(METRE)));
        // create region
        CellRegion region = new CellRegion("Lateral membrane");
        region.addSubSection(CellTopology.INNER, CellSubsection.SECTION_A);
        region.addSubSection(CellTopology.MEMBRANE, CellSubsection.MEMBRANE);
        region.addSubSection(CellTopology.OUTER, CellSubsection.SECTION_B);
        ConcentrationContainer concentrationContainer = region.setUpConcentrationContainer();
        // set concentration
        concentrationContainer.set(CellSubsection.SECTION_A, entityA, 1.0);
        concentrationContainer.set(CellSubsection.SECTION_B, entityB, 0.5);
        concentrationContainer.set(CellSubsection.MEMBRANE, entityC, 1.0);
        // retrieve values
        assertEquals(1.0, concentrationContainer.get(CellSubsection.SECTION_A, entityA).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.5, concentrationContainer.get(CellSubsection.SECTION_B, entityB).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.0, concentrationContainer.get(CellSubsection.MEMBRANE, entityB).to(MOLE_PER_LITRE).getValue().doubleValue());
        // reset environment
        Environment.reset();
    }

}