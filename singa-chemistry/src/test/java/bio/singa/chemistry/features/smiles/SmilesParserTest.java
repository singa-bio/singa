package bio.singa.chemistry.features.smiles;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.chemistry.model.MoleculeAtom;
import bio.singa.chemistry.model.MoleculeGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static bio.singa.chemistry.model.elements.ElementProvider.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class SmilesParserTest {

    @Test
    @DisplayName("smiles parsing - lower case letters and rings")
    void shouldParseRings() {
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
    @DisplayName("smiles parsing - branched structures")
    void shouldParseBranches() {
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
        assertEquals(moleculeGraph.getEdgeBetween(moleculeGraph.getNode(2), moleculeGraph.getNode(4)).get().getType(), CovalentBondType.DOUBLE_BOND);
        assertEquals(moleculeGraph.getEdgeBetween(moleculeGraph.getNode(2), moleculeGraph.getNode(1)).get().getType(), CovalentBondType.SINGLE_BOND);
        assertEquals(moleculeGraph.getEdgeBetween(moleculeGraph.getNode(2), moleculeGraph.getNode(3)).get().getType(), CovalentBondType.SINGLE_BOND);
        assertEquals(moleculeGraph.getEdgeBetween(moleculeGraph.getNode(2), moleculeGraph.getNode(5)).get().getType(), CovalentBondType.SINGLE_BOND);
    }


    @Test
    @DisplayName("smiles parsing - charged atoms")
    void shouldParseIon() {
        // [H]C(=O)[C@H](O)[C@@H](O)[C@H](O)[C@H](O)COS([O-])(=O)=O
        String smilesString = "[H]C(=O)[C@H](O)[C@@H](O)[C@H](O)[C@H](O)COS([O-])(=O)=O";
        MoleculeGraph moleculeGraph = SmilesParser.parse(smilesString);
        // O:14 has charge -1
        assertEquals(moleculeGraph.getNode(14).getElement().getCharge(), -1);
    }

    @Test
    @DisplayName("smiles parsing - isotopes")
    void shouldParseIsotpes() {
        // O=[13C](O)[13C@@H]([15NH2])[13CH]([13CH3])[13CH3]
        String smilesString = "O=[13C](O)[13C@@H]([15NH2])[13CH]([13CH3])[13CH3]";
        MoleculeGraph moleculeGraph = SmilesParser.parse(smilesString);
        // C:1 has 7 neutrons
        assertEquals(7, moleculeGraph.getNode(1).getElement().getNeutronNumber());
        // N:4 has 8 neutrons
        assertEquals(8, moleculeGraph.getNode(4).getElement().getNeutronNumber());
    }

    @Test
    @DisplayName("smiles parsing - disconnected molecules")
    void shouldParseUnconnectedMolecule() {
        // O.O.O.O.O.O.O.O.O.O.O.O.[Al+3].[K+].[O-]S([O-])(=O)=O.[O-]S([O-])(=O)=O
        String smilesString = "O.O.O.O.O.O.O.O.O.O.O.O.[Al+3].[K+].[O-]S([O-])(=O)=O.[O-]S([O-])(=O)=O";
        MoleculeGraph moleculeGraph = SmilesParser.parse(smilesString);
        // O:0 has no neighbours
        assertEquals(moleculeGraph.getNode(0).getNeighbours().size(), 0);
        // Al:12 has no neighbours
        assertEquals(moleculeGraph.getNode(12).getNeighbours().size(), 0);
        // S:15 has 4 neighbours
        assertEquals(moleculeGraph.getNode(15).getNeighbours().size(), 4);
    }

    @Test
    @DisplayName("smiles parsing - hydrogen saturation")
    void shouldSaturateCorrectlyWithHydrogens() {
        // O=[13C](O)[13C@@H]([15NH2])[13CH]([13CH3])[13CH3]
        String smilesString = "O=[13C](O)[13C@@H]([15NH2])[13CH]([13CH3])[13CH3]";
        MoleculeGraph moleculeGraph = SmilesParser.parse(smilesString);
        // C:3 with 1 Hydrogen
        List<MoleculeAtom> neighboursOfC3 = moleculeGraph.getNode(3).getNeighbours();
        assertEquals(1, neighboursOfC3.stream()
                .filter(atom -> atom.getElement().equals(HYDROGEN))
                .count());
        // N:4 with 2 Hydrogen
        List<MoleculeAtom> neighboursOfN4 = moleculeGraph.getNode(4).getNeighbours();
        assertEquals(2, neighboursOfN4.stream()
                .filter(atom -> atom.getElement().equals(HYDROGEN))
                .count());
        // C:6 with 3 Hydrogen
        List<MoleculeAtom> neighboursOfC6 = moleculeGraph.getNode(6).getNeighbours();
        assertEquals(3, neighboursOfC6.stream()
                .filter(atom -> atom.getElement().equals(HYDROGEN))
                .count());
    }

    @Test
    @DisplayName("smiles parsing - dangling closing atoms")
    void shouldParseFragment() {
        String smilesString = "CC(=O)N(C)C";
        MoleculeGraph moleculeGraph = SmilesParser.parse(smilesString);
        long carbonCount = moleculeGraph.getNodes().stream()
                .map(MoleculeAtom::getElement)
                .filter(element -> element.equals(CARBON))
                .count();
        assertEquals(4, carbonCount);
    }

}