package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.graphs.model.GridCoordinateConverter;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.features.permeability.MembraneEntry;
import de.bioforscher.singa.simulation.features.permeability.MembraneExit;
import de.bioforscher.singa.simulation.features.permeability.MembraneFlipFlop;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import static de.bioforscher.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class CombinedTransportTest {

    private static final Rectangle boundingBox = new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0));

    @Test
    public void shouldSimulateBothTransportModules() {

        Species domperidone = new Species.Builder("CHEBI:31515")
                .name("domperidone")
                .assignFeature(Diffusivity.class)
                .assignFeature(new MembraneEntry(1.48e9, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneExit(1.76e3, MANUALLY_ANNOTATED))
                .assignFeature(new MembraneFlipFlop(3.50e2, MANUALLY_ANNOTATED))
                .build();

        Simulation simulation = new Simulation();
        GridCoordinateConverter gcc = new GridCoordinateConverter(40, 30);
        // setup rectangular graph with number of nodes
        AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(
                30, 40, boundingBox, false));
        // create compartments and membrane
        EnclosedCompartment inner = new EnclosedCompartment("I", "Inner");
        EnclosedCompartment outer = new EnclosedCompartment("O", "Outer");
        Membrane membrane = Membrane.forCompartment(inner);
        // initialize species in graph with desired concentration
        for (AutomatonNode node : graph.getNodes()) {
            Vector2D coordinate = gcc.convert(node.getIdentifier());
            if ((coordinate.getX() == 2 && coordinate.getY() > 2 && coordinate.getY() < 27) ||
                    (coordinate.getX() == 37 && coordinate.getY() > 2 && coordinate.getY() < 27) ||
                    (coordinate.getY() == 2 && coordinate.getX() > 1 && coordinate.getX() < 38) ||
                    (coordinate.getY() == 27 && coordinate.getX() > 1 && coordinate.getX() < 38)) {
                // setup membrane
                node.setCellSection(membrane);
                node.setConcentrationContainer(new MembraneContainer(outer, inner, membrane));
                node.setAvailableConcentration(domperidone, inner, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
                System.out.print('=');
            } else if (coordinate.getX() > 2 && coordinate.getY() > 2 && coordinate.getX() < 37 && coordinate.getY() < 27) {
                System.out.print('X');
                node.setCellSection(inner);
                node.setConcentration(domperidone, 0.0);
            } else {
                System.out.print('O');
                node.setCellSection(outer);
                node.setConcentration(domperidone, 1.0);
            }
            if (coordinate.getX() == 39) {
                System.out.print('\n');
            }
        }

        simulation.setGraph(graph);

        FreeDiffusion diffusion = new FreeDiffusion(simulation);
        PassiveMembraneTransport membraneTransport = new PassiveMembraneTransport(simulation);

        simulation.getModules().add(diffusion);
        simulation.getModules().add(membraneTransport);

        simulation.nextEpoch();
    }

}
