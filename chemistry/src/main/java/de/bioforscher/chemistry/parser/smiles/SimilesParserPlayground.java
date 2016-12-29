package de.bioforscher.chemistry.parser.smiles;

import de.bioforscher.chemistry.descriptive.molecules.MoleculeGraph;
import de.bioforscher.chemistry.descriptive.molecules.MoleculeGraphRenderer;
import de.bioforscher.javafx.renderer.graphs.GraphDisplayApplication;
import javafx.application.Application;

/**
 * Created by leberech on 02/12/16.
 */
public class SimilesParserPlayground {

    public static void main(String[] args) {
        SmilesParser playground = new SmilesParser();
        // nested branches with aromatics
        // String smilesString = "Nc1ncnc2n(cnc12)[C@@H]1O[C@H](COP(O)(=O)OP(O)(=O)OP(O)(O)=O)[C@@H](O)[C@H]1O";
        // simple without ring closure
        // String smilesString = "[H]C(=O)[C@H](O)[C@@H](O)[C@H](O)[C@H](O)CO";
        // simple with ring closure
        // String smilesString = "Clc(c(Cl)c(Cl)c1C(=O)O)c(Cl)c1Cl";
        // with ion
        // String smilesString = "[H]C(=O)[C@H](O)[C@@H](O)[C@H](O)[C@H](O)COS([O-])(=O)=O";
        // with isotopes
        // String smilesString = "O=[13C](O)[13C@@H]([15NH2])[13CH]([13CH3])[13CH3]";
        // unconnected molecules
        String smilesString = "O.O.O.O.O.O.O.O.O.O.O.O.[Al+3].[K+].[O-]S([O-])(=O)=O.[O-]S([O-])(=O)=O";

        System.out.println(smilesString);

        GraphDisplayApplication.graph = SmilesParser.parse(smilesString);
        GraphDisplayApplication.renderer = new MoleculeGraphRenderer();
        Application.launch(GraphDisplayApplication.class);

    }

}
