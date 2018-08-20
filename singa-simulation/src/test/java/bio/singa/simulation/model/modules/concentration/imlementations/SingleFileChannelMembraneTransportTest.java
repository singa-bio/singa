package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.databases.chebi.ChEBIParserService;
import bio.singa.chemistry.features.databases.uniprot.UniProtParserService;
import bio.singa.chemistry.features.permeability.OsmoticPermeability;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.After;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.features.DefaultFeatureSources.BINESH2015;
import static org.junit.Assert.assertTrue;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class SingleFileChannelMembraneTransportTest {

    @After
    public void cleanUp() {
        Environment.reset();
    }

    @Test
    public void shouldSimulateChannelDiffusion() {
        Environment.setNodeDistance(Quantities.getQuantity(1, MICRO(METRE)));
        Simulation simulation = new Simulation();
        // setup species
        // water
        SmallMolecule water = ChEBIParserService.parse("CHEBI:15377", "water");
        // solutes
        SmallMolecule solute = new SmallMolecule.Builder("solutes")
                .name("solutes")
                .build();
        // aqp2
        Protein aquaporin2 = UniProtParserService.parse("P41181", "aqp2");
        aquaporin2.setFeature(new OsmoticPermeability(5.31e-14, BINESH2015));
        // graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);
        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CellRegion.MEMBRANE);
        // set concentrations
        Quantity<MolarConcentration> aqp2Concentration = MolarConcentration.moleculesToConcentration(3700, Environment.getSubsectionVolume());
        node.getConcentrationContainer().set(CellTopology.OUTER, water, 50.0);
        node.getConcentrationContainer().set(CellTopology.OUTER, solute, 0.2);
        node.getConcentrationContainer().set(CellTopology.MEMBRANE, aquaporin2, aqp2Concentration);
        node.getConcentrationContainer().set(CellTopology.INNER, solute, 0.1);
        node.getConcentrationContainer().set(CellTopology.INNER, water, 50.0);
        // single file channel membrane transport
        SingleFileChannelMembraneTransport.inSimulation(simulation)
                .transporter(aquaporin2)
                .cargo(water)
                .forSolute(solute)
                .build();
        // simulate a couple of epochs
        Quantity<MolarConcentration> previousInnerConcentration = null;
        Quantity<MolarConcentration> previousOuterConcentration = null;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // inner assertions
            Quantity<MolarConcentration> currentInnerConcentration = node.getConcentrationContainer().get(CellTopology.INNER, water).to(MOLE_PER_LITRE);
            if (previousInnerConcentration != null) {
                assertTrue(currentInnerConcentration.getValue().doubleValue() > previousInnerConcentration.getValue().doubleValue());
            }
            previousInnerConcentration = currentInnerConcentration;
            // outer assertions
            Quantity<MolarConcentration> currentOuterConcentration = node.getConcentrationContainer().get(CellTopology.OUTER, water).to(MOLE_PER_LITRE);
            if (previousOuterConcentration != null) {
                assertTrue(currentOuterConcentration.getValue().doubleValue() < previousOuterConcentration.getValue().doubleValue());
            }
            previousOuterConcentration = currentOuterConcentration;
        }
        Environment.reset();
    }

}
