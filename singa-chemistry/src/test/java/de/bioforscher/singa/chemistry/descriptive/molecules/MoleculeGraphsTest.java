package de.bioforscher.singa.chemistry.descriptive.molecules;

import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.Test;

import java.util.Optional;

/**
 * @author cl
 */
public class MoleculeGraphsTest {

    @Test
    public void shouldConvertStructureGraphToMoleculeGraph() {

        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1C0A")
                .parse();

        System.out.println();

        Optional<LeafSubstructure<?>> arginine = structure.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "A", 1));
        Optional<LeafSubstructure<?>> amp = structure.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "A", 800));
        Optional<LeafSubstructure<?>> a = structure.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "B", 1));




    }
}