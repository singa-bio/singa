package de.bioforscher.singa.chemistry.parser.smiles;


import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeGraph;

/**
 * @author cl
 */
public class SimilesParserPlayground {

    public static void main(String[] args) {
        SmilesParser playground = new SmilesParser();
        // nested branches with aromatics (COP(O)(=O)OP(O)(=O)OP(O)(O)=O)[C@@H](O)[C@H]1O
        // String smilesString = "Nc1ncnc2n(cnc12)[C@@H]1O[C@H]";
        // simple without ring closure
        // String smilesString = "[H]C(=O)[C@H](O)[C@@H](O)[C@H](O)[C@H](O)CO";
        // simple with ring closure
        // String smilesString = "Clc(c(Cl)c(Cl)c1C(=O)O)c(Cl)c1Cl";
        // with ion
        // String smilesString = "[H]C(=O)[C@H](O)[C@@H](O)[C@H](O)[C@H](O)COS([O-])(=O)=O";
        // with isotopes
        // String smilesString = "O=[13C](O)[13C@@H]([15NH2])[13CH]([13CH3])[13CH3]";
        // unconnected molecules
        // String smilesString = "O.O.O.O.O.O.O.O.O.O.O.O.[Al+3].[K+].[O-]S([O-])(=O)=O.[O-]S([O-])(=O)=O";
        // 9,10-bis(phenylethynyl)anthracene
        String smilesString = "c1ccc2cc3ccccc3cc2c1";
        System.out.println(smilesString);
        MoleculeGraph moleculeGraph = SmilesParser.parse(smilesString);


    }

}
