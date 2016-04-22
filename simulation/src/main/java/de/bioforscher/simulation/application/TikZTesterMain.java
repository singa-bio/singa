package de.bioforscher.simulation.application;

import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.parser.GraphMLParserService;

public class TikZTesterMain {

    public static void main(String[] args) {
        GraphMLParserService parserService = new GraphMLParserService("data/BleachTestGraph_r250.xml");
        AutomatonGraph graph = parserService.fetchGraph();
        ExperimentalTikZConverter.exportGraphToTikZ(graph);
    }

}
