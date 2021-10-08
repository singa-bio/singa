package bio.singa.structure.model.pdb;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.general.StructuralMotif;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fk
 */
class StructuralMotifTest {

    private static StructuralMotif motif;

    @BeforeAll
    static void initialize() {
        Structure motifStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("Asn_3m4p.pdb"))
                .everything()
                .parse();
        motif = StructuralMotif.fromLeafSubstructures(motifStructure.getAllLeafSubstructures());
    }

    @Test
    void shouldRetainSubstructureOrdering() {
        final List<LeafSubstructure> allLeafSubstructures = motif.getAllLeafSubstructures();
        LeafSubstructure lower = allLeafSubstructures.get(0);
        LeafSubstructure higher = allLeafSubstructures.get(allLeafSubstructures.size() - 1);
        // switch order in list
        List<LeafSubstructure> aminoAcids = new ArrayList<>();
        aminoAcids.add(higher);
        aminoAcids.add(lower);
        // create new motif from switched order
        StructuralMotif reorderedMotif = StructuralMotif.fromLeafSubstructures(aminoAcids);
        final List<LeafSubstructure> motifLeafSubstructures = reorderedMotif.getAllLeafSubstructures();
        // first entry is higher
        final LeafSubstructure first = motifLeafSubstructures.get(0);
        final LeafSubstructure last = motifLeafSubstructures.get(motifLeafSubstructures.size() - 1);
        assertTrue(first.getIdentifier().getSerial() > last.getIdentifier().getSerial());
        // retain copy order
        StructuralMotif copiedMotif = reorderedMotif.getCopy();
        final List<LeafSubstructure> copiedMotifLeafSubstructures = copiedMotif.getAllLeafSubstructures();
        final LeafSubstructure firstCopy = copiedMotifLeafSubstructures.get(0);
        final LeafSubstructure lastCopy = copiedMotifLeafSubstructures.get(copiedMotifLeafSubstructures.size() - 1);
        assertTrue(firstCopy.getIdentifier().getSerial() > lastCopy.getIdentifier().getSerial());
    }


}