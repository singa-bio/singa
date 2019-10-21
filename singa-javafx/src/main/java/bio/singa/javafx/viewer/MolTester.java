package bio.singa.javafx.viewer;

import bio.singa.javafx.renderer.graphs.GraphDisplayApplication;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.structure.model.molecules.MoleculeGraph;
import bio.singa.structure.parser.mol.MolParser;
import javafx.application.Application;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author cl
 */
public class MolTester {

    public static void main(String[] args) throws IOException {
        Path sdfFilePath = Paths.get("/home/leberech/Downloads/fragments/structures/*CCCC(C)=CCC1=C(C)C(=O)C(OC)=C(OC)C1=O.mol");

        // parse all at once, this takes some time
        // List<MoleculeGraph> moleculeGraphs = MolParser.parseMultiMolFile(sdfFilePath);

        // parse structures one by one
        MolParser molParser = new MolParser(sdfFilePath);
        MoleculeGraph first = molParser.parseNextMoleculeGraph();
        MoleculeGraph second = molParser.parseNextMoleculeGraph();
        // moves to the center (on my monitor)
        // second.getNodes().forEach(atom -> atom.setPosition(atom.getPosition().add(new Vector2D(600,500))));

        // display
//        GraphDisplayApplication.renderer = new MoleculeGraphRenderer();
        GraphDisplayApplication.graph = Graphs.buildLinearGraph(20);
        Application.launch(GraphDisplayApplication.class);
    }

}
