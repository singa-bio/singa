package bio.singa.simulation.model.sections;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.units.UnitRegistry;
import org.junit.jupiter.api.Test;
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
        region.addSubsection(CellTopology.INNER, CellSubsection.SECTION_A);
        ConcentrationContainer concentrationContainer = region.setUpConcentrationContainer();
        // set concentration
        concentrationContainer.initialize(CellSubsection.SECTION_A, entityA, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        // retrieve values
        assertEquals(1.0, UnitRegistry.concentration(concentrationContainer.get(CellSubsection.SECTION_A, entityA)).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.0, UnitRegistry.concentration(concentrationContainer.get(CellSubsection.SECTION_B, entityA)).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.0, UnitRegistry.concentration(concentrationContainer.get(CellSubsection.SECTION_A, entityB)).to(MOLE_PER_LITRE).getValue().doubleValue());
    }

    @Test
    void resembleMembraneContainer() {
        // set environment
        UnitRegistry.setSpace(Quantities.getQuantity(1.0, MICRO(METRE)));
        // create region
        CellRegion region = new CellRegion("Lateral membrane");
        region.addSubsection(CellTopology.INNER, CellSubsection.SECTION_A);
        region.addSubsection(CellTopology.MEMBRANE, CellSubsection.MEMBRANE);
        region.addSubsection(CellTopology.OUTER, CellSubsection.SECTION_B);
        ConcentrationContainer concentrationContainer = region.setUpConcentrationContainer();
        // set concentration
        concentrationContainer.initialize(CellSubsection.SECTION_A, entityA, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        concentrationContainer.initialize(CellSubsection.SECTION_B, entityB, Quantities.getQuantity(0.5, MOLE_PER_LITRE));
        concentrationContainer.initialize(CellSubsection.MEMBRANE, entityC, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        // retrieve values
        assertEquals(1.0, UnitRegistry.concentration(concentrationContainer.get(CellSubsection.SECTION_A, entityA)).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.5, UnitRegistry.concentration(concentrationContainer.get(CellSubsection.SECTION_B, entityB)).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.0, UnitRegistry.concentration(concentrationContainer.get(CellSubsection.MEMBRANE, entityB)).to(MOLE_PER_LITRE).getValue().doubleValue());
    }

}