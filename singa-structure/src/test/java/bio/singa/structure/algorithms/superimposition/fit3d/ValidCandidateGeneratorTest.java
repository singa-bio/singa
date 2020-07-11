package bio.singa.structure.algorithms.superimposition.fit3d;

import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.features.identifiers.LeafIdentifier;
import bio.singa.features.identifiers.LeafIdentifiers;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.StructuralMotif;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fk
 */
class ValidCandidateGeneratorTest {

    @Test
    void shouldGenerateValidCandidates() {

        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1a0j")
                .parse();

        StructuralMotif queryMotif = StructuralMotif.fromLeafIdentifiers(structure, LeafIdentifiers.of(
                "A-57",
                "A-102",
                "A-195"));

        queryMotif.addExchangeableFamily(LeafIdentifier.fromSimpleString("A-57"), AminoAcidFamily.METHIONINE);
        queryMotif.addExchangeableFamily(LeafIdentifier.fromSimpleString("A-57"), AminoAcidFamily.VALINE);
        queryMotif.addExchangeableFamily(LeafIdentifier.fromSimpleString("A-102"), AminoAcidFamily.VALINE);

        List<LeafSubstructure<?>> environment = StructuralMotif.fromLeafIdentifiers(structure, LeafIdentifiers.of(
                "A-104",
                "A-138",
                "A-139",
                "A-140",
                "A-141",
                "A-142",
                "A-143",
                "A-177",
                "A-179",
                "A-180",
                "A-190",
                "A-191",
                "A-192",
                "A-193",
                "A-194",
                "A-196",
                "A-197",
                "A-198",
                "A-211",
                "A-212",
                "A-213",
                "A-214",
                "A-215",
                "A-216",
                "A-227",
                "A-228",
                "A-229",
                "A-230",
                "A-231",
                "A-234",
                "A-237",
                "A-248",
                "A-246",
                "A-304",
                "A-305",
                "A-306",
                "A-307",
                "A-312",
                "A-321",
                "A-323",
                "A-330",
                "A-418",
                "A-448",
                "A-454",
                "A-463",
                "A-467",
                "A-475",
                "A-633",
                "A-195",
                "A-57",
                "A-102")).getAllLeafSubstructures();

        ValidCandidateGenerator validCandidateGeneratorGraphBased = new ValidCandidateGenerator(queryMotif.getAllLeafSubstructures(), environment);
        assertEquals(204, validCandidateGeneratorGraphBased.getCandidates().size());
    }
}