package de.bioforscher.singa.simulation.modules.reactions.model;

import de.bioforscher.singa.chemistry.descriptive.entities.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.units.UnitProvider;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.MichaelisConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber;
import de.bioforscher.singa.features.parameters.EnvironmentalParameterExamples;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.implementations.EquilibriumReaction;
import de.bioforscher.singa.simulation.modules.reactions.implementations.MichaelisMentenReaction;
import de.bioforscher.singa.simulation.modules.reactions.implementations.NthOrderReaction;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import java.util.Arrays;

import static de.bioforscher.singa.features.units.UnitProvider.*;
import static org.junit.Assert.assertEquals;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.SECOND;
import static de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber.PER_MINUTE;
import static de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber.PER_SECOND;
import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.SECOND;

/**
 * @author cl
 */
public class ReactionsTest {

    @Test
    public void testEnzymeReaction() {
        // SABIO Entry ID: 28851
        // Kinetic properties of fructose bisphosphate aldolase from Trypanosoma
        // brucei compared to aldolase from rabbit muscle and Staphylococcus
        // aureus.

        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = prepareGraph();

        // prepare species
        Species fp = ChEBIParserService.parse("CHEBI:18105");
        Species gp = ChEBIParserService.parse("CHEBI:16108");
        Species ga = ChEBIParserService.parse("CHEBI:17378");

        // prepare enzyme
        Enzyme aldolase = new Enzyme.Builder("P07752")
                .name("Fructose-bisphosphate aldolase")
                .addSubstrate(fp)
                .assignFeature(new MolarMass(82142, MANUALLY_ANNOTATED))
                .assignFeature(new MichaelisConstant(Quantities.getQuantity(9.0e-3, MOLE_PER_LITRE), MANUALLY_ANNOTATED))
                .assignFeature(new TurnoverNumber(Quantities.getQuantity(76, PER_MINUTE), MANUALLY_ANNOTATED))
                .build();

        // set concentrations
        for (BioNode node : graph.getNodes()) {
            node.setConcentration(fp, 0.1);
            node.setConcentration(aldolase, 0.2);
            node.setConcentration(ga, 0);
            node.setConcentration(gp, 0);
        }

        // setup environment
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(1.0, MILLI(SECOND)));

        MichaelisMentenReaction reaction = new MichaelisMentenReaction(simulation, aldolase);
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(fp, ReactantRole.DECREASING),
                new StoichiometricReactant(ga, ReactantRole.INCREASING),
                new StoichiometricReactant(gp, ReactantRole.INCREASING)
        ));

        // add graph
        simulation.setGraph(graph);
        // add the reaction module
        simulation.getModules().add(reaction);

        BioNode node = graph.getNode(0);
        // run simulation
        for (int time = 0; time <= 1000; time++) {
            reactions.applyTo(graph);
            if (time == 200) {
                // keto-D-fructose 1-phosphate
                assertEquals(0.05452943852996704, node.getConcentration(fp).getValue().doubleValue(), 1e10);
                // D-glyceraldehyde
                assertEquals(0.09094112294006595, node.getConcentration(gp).getValue().doubleValue(), 1e10);
                // dihydroxyacetone phosphate
                assertEquals(0.1818822458801319, node.getConcentration(ga).getValue().doubleValue(), 1e10);
                // Fructose-bisphosphate aldolase
                assertEquals(0.2, node.getConcentration(aldolase).getValue().doubleValue(), 0);
            }
            if (time == 1000) {
                // keto-D-fructose 1-phosphate
                assertEquals(3.1476422346651687E-9, node.getConcentration(fp).getValue().doubleValue(), 1e10);
                // D-glyceraldehyde
                assertEquals(0.1999999937047156, node.getConcentration(gp).getValue().doubleValue(), 1e10);
                // dihydroxyacetone phosphate
                assertEquals(0.3999999874094312, node.getConcentration(ga).getValue().doubleValue(), 1e10);
                // Fructose-bisphosphate aldolase
                assertEquals(0.2, node.getConcentration(aldolase).getValue().doubleValue(), 0);
            }
        }
    }

    @Test
    public void testEquilibriumReaction() {

        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = prepareGraph();

        // prepare species
        Species speciesA = new Species.Builder("Species A")
                .build();
        Species speciesB = new Species.Builder("Species B")
                .build();

        // set concentrations
        for (BioNode node : graph.getNodes()) {
            node.setConcentration(speciesA, 1.0);
            node.setConcentration(speciesB, 0.0);
        }

        // setup environment
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(0.5, MILLI(SECOND)));


        EquilibriumReaction reaction = new EquilibriumReaction(simulation,
                Quantities.getQuantity(5, PER_SECOND),
                Quantities.getQuantity(10, PER_SECOND));
        // set reactants
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(speciesA, ReactantRole.DECREASING),
                new StoichiometricReactant(speciesB, ReactantRole.INCREASING)
        ));
        // set as elementary (no complex reaction)
        reaction.setElementary(true);

        // add graph
        simulation.setGraph(graph);
        // add the reaction module
        simulation.getModules().add(reaction);

        BioNode node = graph.getNode(0);
        // run simulation
        for (int time = 0; time < 1000; time++) {
            reactions.applyTo(graph);
            if (time == 50) {
                // species A
                assertEquals(0.9052985315868114, node.getConcentration(speciesA).getValue().doubleValue(), 1e10);
                // species B
                assertEquals(0.18940293682637716, node.getConcentration(speciesB).getValue().doubleValue(), 1e10);
            }
            if (time == 700) {
                // species A
                assertEquals(0.8000296169347756, node.getConcentration(speciesA).getValue().doubleValue(), 1e10);
                // species B
                assertEquals(0.3999407661304493, node.getConcentration(speciesB).getValue().doubleValue(), 1e10);
            }
        }

    }

    @Test
    public void testNthOrderReaction() {

        Simulation simulation = new Simulation();

        // setup graph
        AutomatonGraph graph = prepareGraph();

        // prepare species
        Species dpo = ChEBIParserService.parse("CHEBI:29802");
        Species ndo = ChEBIParserService.parse("CHEBI:33101");
        Species oxygen = ChEBIParserService.parse("CHEBI:15379");

        for (BioNode node : graph.getNodes()) {
            node.setConcentration(dpo, 0.020);
            node.setConcentration(ndo, 0);
            node.setConcentration(oxygen, 0);
        }


        // setup environment
        EnvironmentalParameters.getInstance().setTimeStep(Quantities.getQuantity(10, MILLI(SECOND)));

        // create reaction
        NthOrderReaction reaction = new NthOrderReaction(simulation, Quantities.getQuantity(0.07, PER_SECOND));
        // set reactants
        reaction.getStoichiometricReactants().addAll(Arrays.asList(
                new StoichiometricReactant(dpo, ReactantRole.DECREASING, 2),
                new StoichiometricReactant(ndo, ReactantRole.INCREASING, 4),
                new StoichiometricReactant(oxygen, ReactantRole.INCREASING)
        ));
        // set as elementary (no complex reaction)
        reaction.setElementary(true);

        // add graph
        simulation.setGraph(graph);
        // add the reaction
        simulation.getModules().add(reaction);

        BioNode node = graph.getNode(0);
        // run simulation
        for (int time = 0; time < 700; time++) {
            reactions.applyTo(graph);
            if (time == 50) {
                // dioxygen
                assertEquals(0.0011543040774821558, node.getConcentration(oxygen).getValue().doubleValue(), 1e10);
                // nitrogen
                assertEquals(0.009234432619857246, node.getConcentration(ndo).getValue().doubleValue(), 1e10);
                // dinitrogen pentaoxide
                assertEquals(0.013074175535107053, node.getConcentration(dpo).getValue().doubleValue(), 1e10);
            }
            if (time == 700) {
                // dioxygen
                assertEquals(0.0031589374517021875, node.getConcentration(oxygen).getValue().doubleValue(), 1e10);
                // nitrogen
                assertEquals(0.0252714996136175, node.getConcentration(ndo).getValue().doubleValue(), 1e10);
                // dinitrogen pentaoxide
                assertEquals(0.0010463752897868599, node.getConcentration(dpo).getValue().doubleValue(), 1e10);
            }
        }

    }

    private AutomatonGraph prepareGraph() {
        return AutomatonGraphs.copyStructureToBioGraph(Graphs.buildLinearGraph(1,
                new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0))));
    }

}