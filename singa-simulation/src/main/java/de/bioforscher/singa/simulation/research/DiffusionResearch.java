package de.bioforscher.singa.simulation.research;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.util.GraphFactory;
import de.bioforscher.singa.mathematics.graphs.util.RectangularGridCoordinateConverter;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.BioEdge;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.diffusion.FreeDiffusion;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.Collections;
import java.util.List;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETER_PER_SECOND;
import static tec.units.ri.unit.MetricPrefix.MICRO;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;


public class DiffusionResearch {

    public static void main(String[] args) {

        Rectangle defaultBoundingBox = new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0));
        int numberOfNodes = 200;
        Quantity<Time> timeStep = Quantities.getQuantity(5, NANO(SECOND));

        System.out.println("Fetching Species ...");

        // get required species
        //Species hydrogen = ChEBIParserService.parse("CHEBI:18276");
         Species ammonia = ChEBIParserService.parse("CHEBI:16134");
        // Species benzene = ChEBIParserService.parse("CHEBI:16716");
        // Species methanol = ChEBIParserService.parse("CHEBI:17790");
        // Species succinicAcid = ChEBIParserService.parse("CHEBI:15741");
        // Species ethaneDiol = ChEBIParserService.parse("CHEBI:30742");

        // bundle species
        List<Species> speciesList = Collections.singletonList(ammonia);

        System.out.println("Initializing Graph ...");

        // setup rectangular graph with number of nodes
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(GraphFactory.buildGridGraph(
                numberOfNodes, numberOfNodes, defaultBoundingBox, false));

        // initialize species in graph with desired concentration leaving the right "half" empty
        for (BioNode node : graph.getNodes()) {
            if (node.getIdentifier() % numberOfNodes < numberOfNodes / 2) {
                speciesList.forEach(species -> node.setConcentration(species, 1.0));
            } else {
                speciesList.forEach(species -> node.setConcentration(species, 0.0));
            }
        }

        for (BioEdge edge : graph.getEdges()) {
            speciesList.forEach(species -> edge.addPermeability(species, 1.0));
        }

        System.out.println("Setting up environment ...");

        // setup time step size as given
        EnvironmentalParameters.getInstance().setTimeStep(timeStep);
        // setup node distance to diameter / (numberOfNodes - 1)
        EnvironmentalParameters.getInstance().setNodeSpacingToDiameter(
                Quantities.getQuantity(2500.0, NANO(METRE)), numberOfNodes);

        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);

        // setup diffusion
        FreeDiffusion freeDiffusion = new FreeDiffusion();
        // fix diffusion values from literature
        // freeDiffusion.fixDiffusionCoefficientForEntity(hydrogen, Quantities.getQuantity(4.40E-05,
        //        SQUARE_CENTIMETER_PER_SECOND));
        freeDiffusion.fixDiffusionCoefficientForEntity(ammonia, Quantities.getQuantity(2.28E-05,
                SQUARE_CENTIMETER_PER_SECOND));
        // freeDiffusion.fixDiffusionCoefficientForEntity(benzene, Quantities.getQuantity(1.09E-05,
        //       SQUARE_CENTIMETER_PER_SECOND));
        //freeDiffusion.fixDiffusionCoefficientForEntity(methanol, Quantities.getQuantity(1.66E-05,
        //       SQUARE_CENTIMETER_PER_SECOND));
        //freeDiffusion.fixDiffusionCoefficientForEntity(succinicAcid, Quantities.getQuantity(8.60E-06,
        //        SQUARE_CENTIMETER_PER_SECOND));
        //freeDiffusion.fixDiffusionCoefficientForEntity(ethaneDiol, Quantities.getQuantity(6.40E-06,
        //        SQUARE_CENTIMETER_PER_SECOND));
        // add diffusion module
        simulation.getModules().add(freeDiffusion);
        // add desired species to the simulation for easy access
        simulation.getChemicalEntities().addAll(speciesList);

        RectangularGridCoordinateConverter converter = new RectangularGridCoordinateConverter(numberOfNodes,
                numberOfNodes);

        int observedNodeIdentifier = converter.convert(new Vector2D(numberOfNodes-1, (numberOfNodes/2)-1));
        System.out.println("Observing node: "+observedNodeIdentifier);
        graph.getNode(observedNodeIdentifier).setObserved(true);

        System.out.println("Starting simulation ...");

        while (graph.getNode(observedNodeIdentifier).getConcentration(ammonia).getValue().doubleValue() < 0.25) {
            simulation.nextEpoch();
            if (simulation.getEpoch() % 1000 == 0 && simulation.getEpoch() > 1) {
                System.out.println("Currently at: "+simulation.getElapsedTime().to(MICRO(SECOND)));
            }
        }

        // check correct diffusion
        System.out.println("Half life time of "+ammonia.getName()+" reached at "+simulation.getElapsedTime());

    }

}
