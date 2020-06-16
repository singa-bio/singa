package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.complex.ComplexEntity;
import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellSubsections;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.unit.Units.MINUTE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class ComplexBuildingReactionTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
    }

    @Test
    @DisplayName("complex building reaction - minimal setup")
    void minimalSetUpTest() {
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);

        // rate constants
        RateConstant forwardRate = RateConstant.create(1)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        RateConstant backwardRate = RateConstant.create(1)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // reactants
        ChemicalEntity bindee = SmallMolecule.create("bindee").build();
        Protein binder = Protein.create("binder").build();
        ComplexEntity complex = ComplexEntity.from(binder, bindee);

        // create and add module
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(binder, MEMBRANE)
                .addSubstrate(bindee, INNER)
                .addProduct(complex, MEMBRANE)
                .complexBuilding()
                .associationRate(forwardRate)
                .dissociationRate(backwardRate)
                .build();

        // set concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setCellRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION);
        membraneNode.getConcentrationContainer().initialize(INNER, bindee, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        membraneNode.getConcentrationContainer().initialize(MEMBRANE, binder, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
        membraneNode.getConcentrationContainer().initialize(MEMBRANE, complex, Quantities.getQuantity(1.0, MOLE_PER_LITRE));

        // forward and backward reactions should cancel each other out
        ConcentrationContainer container = membraneNode.getConcentrationContainer();
        for (int i = 0; i < 10; i++) {
            assertEquals(0.0, container.get(MEMBRANE, bindee));
            assertEquals(1.0, UnitRegistry.concentration(container.get(MEMBRANE, binder)).to(MOLE_PER_LITRE).getValue().doubleValue());
            assertEquals(1.0, UnitRegistry.concentration(container.get(MEMBRANE, complex)).to(MOLE_PER_LITRE).getValue().doubleValue());
            assertEquals(1.0, UnitRegistry.concentration(container.get(INNER, bindee)).to(MOLE_PER_LITRE).getValue().doubleValue());
            assertEquals(0, container.get(INNER, binder));
            assertEquals(0, container.get(INNER, complex));
            simulation.nextEpoch();
        }
    }

    @Test
    @DisplayName("complex building reaction - simple section changing binding")
    void testMembraneAbsorption() {
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);

        // rate constants
        RateConstant<?> forwardRate = RateConstant.create(1.0e6)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(MINUTE)
                .build();

        RateConstant<?> backwardRate = RateConstant.create(0.01)
                .backward().firstOrder()
                .timeUnit(MINUTE)
                .build();

        // reactants
        ChemicalEntity bindee = SmallMolecule.create("bindee").build();
        Protein binder = Protein.create("binder").build();
        ComplexEntity complex = ComplexEntity.from(binder, bindee);

        // create and add module
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(binder, MEMBRANE)
                .addSubstrate(bindee, INNER)
                .addProduct(complex, MEMBRANE)
                .complexBuilding()
                .associationRate(forwardRate)
                .dissociationRate(backwardRate)
                .build();

        // concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setCellRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION);
        membraneNode.getConcentrationContainer().set(INNER, bindee, 1.0);
        membraneNode.getConcentrationContainer().set(MEMBRANE, binder, 0.1);
        membraneNode.getConcentrationContainer().set(MEMBRANE, complex, 0.0);

        double previousConcentration = 0.0;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            double currentConcentration = membraneNode.getConcentrationContainer().get(CellSubsections.CELL_OUTER_MEMBRANE, complex);
            assertTrue(currentConcentration > previousConcentration);
            previousConcentration = currentConcentration;
        }
    }

}
