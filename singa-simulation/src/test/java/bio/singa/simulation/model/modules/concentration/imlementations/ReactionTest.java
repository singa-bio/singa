package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.*;
import bio.singa.chemistry.features.databases.chebi.ChEBIParserService;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.displacement.Vesicle;
import bio.singa.simulation.model.modules.displacement.VesicleLayer;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import static bio.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.modules.displacement.implementations.EndocytosisActinBoost.DEFAULT_CLATHRIN_DEPOLYMERIZATION_RATE;
import static bio.singa.simulation.model.sections.CellSubsection.SECTION_A;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
public class ReactionTest {

    private static final Logger logger = LoggerFactory.getLogger(ReactionTest.class);

    @After
    public void cleanUp() {
        Environment.reset();
    }

    @Test
    public void testEnzymeReaction() {
        // SABIO Entry ID: 28851
        // Kinetic properties of fructose bisphosphate aldolase from Trypanosoma brucei compared to aldolase from rabbit
        // muscle and Staphylococcus aureus.

        logger.info("Testing Biochemical Reaction Module.");
        // create simulation
        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.1, SECOND));
        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // prepare species
        SmallMolecule fp = ChEBIParserService.parse("CHEBI:18105");
        SmallMolecule gp = ChEBIParserService.parse("CHEBI:16108");
        SmallMolecule ga = ChEBIParserService.parse("CHEBI:17378");

        // prepare enzyme
        Enzyme aldolase = new Enzyme.Builder("P07752")
                .name("Fructose-bisphosphate aldolase")
                .assignFeature(new MichaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE), MANUALLY_ANNOTATED))
                .assignFeature(new TurnoverNumber(76, new ProductUnit<>(ONE.divide(MINUTE)), MANUALLY_ANNOTATED))
                .build();

        // set concentrations
        for (AutomatonNode node : graph.getNodes()) {
            node.setCellRegion(CellRegion.CYTOSOL_A);
            node.getConcentrationContainer().set(SECTION_A, fp, 1.0);
            node.getConcentrationContainer().set(SECTION_A, aldolase, 0.01);
            node.getConcentrationContainer().set(SECTION_A, ga, 0);
            node.getConcentrationContainer().set(SECTION_A, gp, 0);
        }

        // setup reaction
        MichaelisMentenReaction.inSimulation(simulation)
                .enzyme(aldolase)
                .addSubstrate(fp)
                .addProduct(ga)
                .addProduct(gp)
                .build();

        // add graph
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(39.2, SECOND);
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(90, SECOND);
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(SECOND)).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(SECOND));
                assertEquals(0.50, node.getConcentrationContainer().get(SECTION_A, fp).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-2);
                assertEquals(0.49, node.getConcentrationContainer().get(SECTION_A, gp).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-2);
                assertEquals(0.49, node.getConcentrationContainer().get(SECTION_A, ga).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-2);
                assertEquals(0.01, node.getConcentrationContainer().get(SECTION_A, aldolase).to(MOLE_PER_LITRE).getValue().doubleValue(), 0);
                firstCheckpointPassed = true;
            }
        }
        // check final values
        assertEquals(0.0, node.getConcentrationContainer().get(SECTION_A, fp).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(1.0, node.getConcentrationContainer().get(SECTION_A, gp).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(1.0, node.getConcentrationContainer().get(SECTION_A, ga).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.01, node.getConcentrationContainer().get(SECTION_A, aldolase).to(MOLE_PER_LITRE).getValue().doubleValue(), 0);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(MILLI(SECOND)));
    }

    @Test
    public void testEquilibriumReaction() {
        logger.info("Testing Equilibrium Reaction Module.");
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // prepare species
        SmallMolecule speciesA = new SmallMolecule.Builder("Species A")
                .build();
        SmallMolecule speciesB = new SmallMolecule.Builder("Species B")
                .build();

        // set concentrations
        for (AutomatonNode node : graph.getNodes()) {
            node.getConcentrationContainer().set(SECTION_A, speciesA, 1.0);
            node.getConcentrationContainer().set(SECTION_A, speciesB, 0.0);
        }

        RateConstant forwardsRate = RateConstant.create(5).forward().firstOrder().timeUnit(SECOND).build();
        RateConstant backwardsRate = RateConstant.create(10).backward().firstOrder().timeUnit(SECOND).build();

        // setup reaction
        ReversibleReaction.inSimulation(simulation)
                .addSubstrate(speciesA)
                .addProduct(speciesB)
                .forwardsRateConstant(forwardsRate)
                .backwardsRateConstant(backwardsRate)
                .build();

        // add graph
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(25.0, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(800.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(MILLI(SECOND)));
                assertEquals(0.8901, node.getConcentrationContainer().get(SECTION_A, speciesA).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.1108, node.getConcentrationContainer().get(SECTION_A, speciesB).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.66666, node.getConcentrationContainer().get(SECTION_A, speciesA).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-5);
        assertEquals(0.33333, node.getConcentrationContainer().get(SECTION_A, speciesB).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-5);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(MILLI(SECOND)));

    }

    @Test
    public void testNthOrderReaction() {
        logger.info("Testing Nth Order Reaction Module.");
        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();

        // prepare species
        SmallMolecule dpo = ChEBIParserService.parse("CHEBI:29802");
        SmallMolecule ndo = ChEBIParserService.parse("CHEBI:33101");
        SmallMolecule oxygen = ChEBIParserService.parse("CHEBI:15379");

        for (AutomatonNode node : graph.getNodes()) {
            node.getConcentrationContainer().set(SECTION_A, dpo, 0.02);
            node.getConcentrationContainer().set(SECTION_A, ndo, 0.0);
            node.getConcentrationContainer().set(SECTION_A, oxygen, 0.0);
        }

        RateConstant rateConstant = RateConstant.create(0.07).forward().firstOrder().timeUnit(SECOND).build();

        // create reaction
        NthOrderReaction.inSimulation(simulation)
                .addSubstrate(dpo, 2)
                .addProduct(ndo, 4)
                .addProduct(oxygen)
                .rateConstant(rateConstant)
                .build();

        // add graph
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(500.0, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(7000.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(MILLI(SECOND)));
                assertEquals(9E-4, node.getConcentrationContainer().get(SECTION_A, oxygen).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.003, node.getConcentrationContainer().get(SECTION_A, ndo).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.018, node.getConcentrationContainer().get(SECTION_A, dpo).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.006, node.getConcentrationContainer().get(SECTION_A, oxygen).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.025, node.getConcentrationContainer().get(SECTION_A, ndo).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.007, node.getConcentrationContainer().get(SECTION_A, dpo).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(MILLI(SECOND)));

    }

   @Test
   public void testDecayInMembrane() {
       logger.info("Testing Decay in Membrane.");
       // create simulation
       Simulation simulation = new Simulation();
       simulation.setMaximalTimeStep(Quantities.getQuantity(0.1, SECOND));

       // setup graph
       AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);

       // prepare species
       ChemicalEntity clathrinHeavyChain = new Protein.Builder("Clathrin heavy chain")
               .assignFeature(new UniProtIdentifier("Q00610"))
               .build();

       ChemicalEntity clathrinLightChain = new Protein.Builder("Clathrin light chain")
               .assignFeature(new UniProtIdentifier("P09496"))
               .build();

       ComplexedChemicalEntity clathrinTriskelion = ComplexedChemicalEntity.create("Clathrin Triskelion")
               .addAssociatedPart(clathrinHeavyChain, 3)
               .addAssociatedPart(clathrinLightChain, 3)
               .build();

       VesicleLayer layer = new VesicleLayer(simulation);
       Vesicle vesicle = new Vesicle(new Vector2D(0.0,0.0),  Quantities.getQuantity(50, NANO(METRE)));
       vesicle.getConcentrationContainer().set(CellTopology.MEMBRANE, clathrinTriskelion, MolarConcentration.moleculesToConcentration(60, vesicle.getVolume()).to(Environment.getConcentrationUnit()));
       layer.addVesicle(vesicle);
       simulation.setVesicleLayer(layer);

       NthOrderReaction reaction = NthOrderReaction.inSimulation(simulation)
               .rateConstant(DEFAULT_CLATHRIN_DEPOLYMERIZATION_RATE)
               .addSubstrate(clathrinTriskelion)
               .build();


       while (simulation.getElapsedTime().isLessThan(Quantities.getQuantity(11, SECOND))) {
           simulation.nextEpoch();
           Quantity<Dimensionless> molecules = MolarConcentration.concentrationToMolecules(vesicle.getConcentrationContainer().get(CellTopology.MEMBRANE, clathrinTriskelion), vesicle.getVolume());
           System.out.println(simulation.getElapsedTime().to(SECOND)+" - "+molecules.getValue().intValue());
       }

   }


    @Test
    @Ignore
    public void shouldPerformCalciumOscillationExample() {
        // FIXME currently there are no relations to time and space when working with dynamic reactions
        // FIXME every parameter should be scaled individually instead of applying some arbitrary scale
        // it should be recognized which reaction rate is required and they should be transformed to the corresponding
        // scales
//        logger.info("Testing Dynamic Reaction Module.");
//        Simulation simulation = SimulationExamples.createSimulationFromSBML();
//
//        SmallMolecule x = new SmallMolecule.Builder("X").build();
//        AutomatonNode node = simulation.getGraph().getNodes().iterator().next();
//        logger.info("Starting simulation ...");
//        Quantity<Time> currentTime;
//        Quantity<Time> firstCheckpoint = Quantities.getQuantity(169.0, MILLI(SECOND));
//        boolean firstCheckpointPassed = false;
//        Quantity<Time> secondCheckpoint = Quantities.getQuantity(351.0, MILLI(SECOND));
//        // run simulation
//        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
//            simulation.nextEpoch();
//            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
//                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(SECOND));
//                assertEquals(0.2958, node.getConcentrationContainer().get(INNER, x).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-4);
//                firstCheckpointPassed = true;
//            }
//        }
//
//        // check final values
//        assertEquals(0.2975, node.getConcentrationContainer().get(INNER, x).getValue().doubleValue(), 1e-4);
//        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(SECOND));

    }

}
