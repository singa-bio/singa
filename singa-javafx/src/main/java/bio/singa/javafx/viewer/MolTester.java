package bio.singa.javafx.viewer;

import bio.singa.javafx.renderer.graphs.GraphDisplayApplication;
import bio.singa.mathematics.graphs.model.Graphs;
import javafx.application.Application;

import java.io.IOException;

/**
 * @author cl
 */
public class MolTester {

    public static void main(String[] args) throws IOException {
//        Path sdfFilePath = Paths.get("/tmp/*C(C)CCC=C(C)CCC=C(C)C.mol");
//
//        // parse all at once, this takes some time
//        // List<MoleculeGraph> moleculeGraphs = MolParser.parseMultiMolFile(sdfFilePath);
//
//        // parse structures one by one
//        MolParser molParser = new MolParser(sdfFilePath, true);
//        MoleculeGraph first = molParser.parseNextMoleculeGraph();
//        MoleculeGraph second = molParser.parseNextMoleculeGraph();
//        // moves to the center (on my monitor)
//        second.getNodes().forEach(atom -> atom.setPosition(atom.getPosition().add(new Vector2D(600, 500))));

        // display
//        GraphDisplayApplication.renderer = new MoleculeGraphRenderer();
        GraphDisplayApplication.graph = Graphs.buildGridGraph(10,10);
        Application.launch(GraphDisplayApplication.class);
    }
}
