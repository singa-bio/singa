package de.bioforscher.singa.chemistry.descriptive.features.smiles;

import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeAtom;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeBondType;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider.OXYGEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class SmilesParserTest {

    @Test
    public void shouldParseRings() {
        String smilesString = "c1ccc2cc3ccccc3cc2c1";
        MoleculeGraph moleculeGraph = SmilesParser.parse(smilesString);
        // first (C:0) is connected to last (C:13) and next (C:1)
        List<MoleculeAtom> neighboursOf0 = moleculeGraph.getNode(0).getNeighbours();
        assertTrue(neighboursOf0.stream()
                .anyMatch(atom -> atom.getIdentifier() == 13));
        assertTrue(neighboursOf0.stream()
                .anyMatch(atom -> atom.getIdentifier() == 1));
        // fourth (C:3) is connected to before (C:2) next (C:4) and second to last (C:12)
        List<MoleculeAtom> neighboursOf3 = moleculeGraph.getNode(3).getNeighbours();
        assertTrue(neighboursOf3.stream()
                .anyMatch(atom -> atom.getIdentifier() == 2));
        assertTrue(neighboursOf3.stream()
                .anyMatch(atom -> atom.getIdentifier() == 4));
        assertTrue(neighboursOf3.stream()
                .anyMatch(atom -> atom.getIdentifier() == 12));
    }


    @Test
    public void shhouldParseBranches() {
        // original: (COP(O)(=O)OP(O)(=O)OP(O)(O)=O)[C@@H](O)[C@H]1O
        String smilesString = "COP(O)(=O)OP(O)(=O)OP(O)(O)=O[C@@H](O)[C@H]1O";
        MoleculeGraph moleculeGraph = SmilesParser.parse(smilesString);
        // P:2 is connected to four Oxygen (1,3,4,5)
        List<MoleculeAtom> neighboursOfP2 = moleculeGraph.getNode(2).getNeighbours();
        assertTrue(neighboursOfP2.stream()
                .anyMatch(atom -> atom.getIdentifier() == 1 && atom.getElement().equals(OXYGEN)));
        assertTrue(neighboursOfP2.stream()
                .anyMatch(atom -> atom.getIdentifier() == 3 && atom.getElement().equals(OXYGEN)));
        assertTrue(neighboursOfP2.stream()
                .anyMatch(atom -> atom.getIdentifier() == 4 && atom.getElement().equals(OXYGEN)));
        assertTrue(neighboursOfP2.stream()
                .anyMatch(atom -> atom.getIdentifier() == 5 && atom.getElement().equals(OXYGEN)));
        // where one is a double bond and the rest are single bonds
        assertEquals(moleculeGraph.getEdgeBetween(2,4).getType(), MoleculeBondType.DOUBLE_BOND);
        assertEquals(moleculeGraph.getEdgeBetween(2,1).getType(), MoleculeBondType.SINGLE_BOND);
        assertEquals(moleculeGraph.getEdgeBetween(2,3).getType(), MoleculeBondType.SINGLE_BOND);
        assertEquals(moleculeGraph.getEdgeBetween(2,5).getType(), MoleculeBondType.SINGLE_BOND);
    }


    @Test
    public void shhouldParseIon() {
        // [H]C(=O)[C@H](O)[C@@H](O)[C@H](O)[C@H](O)COS([O-])(=O)=O
        Assert.fail();
    }


    // with nested branches
    // Clc(c(Cl)c(Cl)c1C(=O)O)c(Cl)c1Cl

    // with isotopes
    // String smilesString = "O=[13C](O)[13C@@H]([15NH2])[13CH]([13CH3])[13CH3]";

    // unconnected molecules
    // String smilesString = "O.O.O.O.O.O.O.O.O.O.O.O.[Al+3].[K+].[O-]S([O-])(=O)=O.[O-]S([O-])(=O)=O";


}