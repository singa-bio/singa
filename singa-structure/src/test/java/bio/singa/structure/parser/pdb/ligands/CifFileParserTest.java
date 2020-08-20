package bio.singa.structure.parser.pdb.ligands;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.core.utility.Pair;
import bio.singa.structure.parser.pdb.structures.tokens.LeafSkeleton;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class CifFileParserTest {

    @Test
    void shouldParseSingleBondLigandStructures() {
        LeafSkeleton leafSkeleton = LigandParserService.parseLeafSkeleton("OH");
        Map<Pair<String>, CovalentBondType> bonds = leafSkeleton.getBonds();
        assertEquals(1, bonds.size()); }
}