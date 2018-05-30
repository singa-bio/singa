package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.entities.Transporter;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot.UniProtParserService;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.OsmoticPermeability;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.model.newsections.CellTopology;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertTrue;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class SingleFileChannelMembraneTransportTest {

    static final FeatureOrigin BINESH2015 = new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION, "Binesh 2015", "Binesh, A. R., and R. Kamali. \"Molecular dynamics insights into human aquaporin 2 water channel.\" Biophysical chemistry 207 (2015): 107-113.");

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
        Transporter aquaporin2 = UniProtParserService.parse("P41181", "aqp2").asTransporter();
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
    }

}