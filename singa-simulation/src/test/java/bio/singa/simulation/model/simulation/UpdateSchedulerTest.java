package bio.singa.simulation.model.simulation;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellSubsections;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import org.junit.jupiter.api.Test;
import tec.uom.se.quantity.Quantities;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
class UpdateSchedulerTest {

    @Test
    void testAccuracyGainCalculation() {


        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(10, SECOND));

        // create graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CellRegions.CYTOPLASM_REGION);

        SmallMolecule firstSubstrate = SmallMolecule.create("S1").build();
        SmallMolecule secondSubstrate = SmallMolecule.create("S2").build();
        SmallMolecule product = SmallMolecule.create("P").build();

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CellSubsections.CYTOPLASM, firstSubstrate, Quantities.getQuantity(100, MICRO_MOLE_PER_LITRE));
        ci.addInitialConcentration(CellSubsections.CYTOPLASM, secondSubstrate, Quantities.getQuantity(50, MICRO_MOLE_PER_LITRE));
        simulation.setConcentrationInitializer(ci);

        RateConstant kf = RateConstant.create(1000)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        RateConstant kb = RateConstant.create(1)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(firstSubstrate)
                .addSubstrate(secondSubstrate)
                .addProduct(product)
                .complexBuilding()
                .associationRate(kf)
                .dissociationRate(kb)
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
        }
        System.out.println(simulation.getScheduler().getTimestepsDecreased()+"/"+simulation.getScheduler().getTimestepsIncreased()+" (d/i)");
        System.out.println(simulation.getElapsedTime());

    }
}