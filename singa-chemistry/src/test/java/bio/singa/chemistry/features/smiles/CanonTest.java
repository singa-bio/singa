package bio.singa.chemistry.features.smiles;

import bio.singa.structure.model.molecules.MoleculeGraph;
import bio.singa.structure.model.molecules.MoleculeGraphs;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CanonTest {

    @Test
    void canon(){
        MoleculeGraph moleculeGraph = SmilesParser.parse("OCC(CC)CCC(CN)CN");
        Canon canon = new Canon(moleculeGraph);
    }
}