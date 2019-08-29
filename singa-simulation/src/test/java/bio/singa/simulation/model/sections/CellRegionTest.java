package bio.singa.simulation.model.sections;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.features.units.UnitRegistry;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
class CellRegionTest {

    private final ChemicalEntity entityA = SmallMolecule.create("A").build();
    private final ChemicalEntity entityB = SmallMolecule.create("B").build();
    private final ChemicalEntity entityC = SmallMolecule.create("C").build();

    @Test
    void resembleSingleContainer() {
        // set environment
        UnitRegistry.setSpace(Quantities.getQuantity(1.0, MICRO(METRE)));
        // create region
        CellRegion region = new CellRegion("Cytoplasm");
        region.addSubsection(CellTopology.INNER, CellSubsections.CYTOPLASM);
        ConcentrationContainer concentrationContainer = region.setUpConcentrationContainer();
        // set concentration
        concentrationContainer.initialize(CellSubsections.CYTOPLASM, entityA, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        // retrieve values
        assertEquals(1.0, UnitRegistry.concentration(concentrationContainer.get(CellSubsections.CYTOPLASM, entityA)).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.0, UnitRegistry.concentration(concentrationContainer.get(CellSubsections.EXTRACELLULAR_REGION, entityA)).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.0, UnitRegistry.concentration(concentrationContainer.get(CellSubsections.CYTOPLASM, entityB)).to(MOLE_PER_LITRE).getValue().doubleValue());
    }

    @Test
    void resembleMembraneContainer() {
        // set environment
        UnitRegistry.setSpace(Quantities.getQuantity(1.0, MICRO(METRE)));
        // create region
        CellRegion region = new CellRegion("Lateral membrane");
        region.addSubsection(CellTopology.INNER, CellSubsections.CYTOPLASM);
        region.addSubsection(CellTopology.MEMBRANE, CellSubsections.CELL_OUTER_MEMBRANE);
        region.addSubsection(CellTopology.OUTER, CellSubsections.EXTRACELLULAR_REGION);
        ConcentrationContainer concentrationContainer = region.setUpConcentrationContainer();
        // set concentration
        concentrationContainer.initialize(CellSubsections.CYTOPLASM, entityA, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        concentrationContainer.initialize(CellSubsections.EXTRACELLULAR_REGION, entityB, Quantities.getQuantity(0.5, MOLE_PER_LITRE));
        concentrationContainer.initialize(CellSubsections.CELL_OUTER_MEMBRANE, entityC, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        // retrieve values
        assertEquals(1.0, UnitRegistry.concentration(concentrationContainer.get(CellSubsections.CYTOPLASM, entityA)).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.5, UnitRegistry.concentration(concentrationContainer.get(CellSubsections.EXTRACELLULAR_REGION, entityB)).to(MOLE_PER_LITRE).getValue().doubleValue());
        assertEquals(0.0, UnitRegistry.concentration(concentrationContainer.get(CellSubsections.CELL_OUTER_MEMBRANE, entityB)).to(MOLE_PER_LITRE).getValue().doubleValue());
    }

}