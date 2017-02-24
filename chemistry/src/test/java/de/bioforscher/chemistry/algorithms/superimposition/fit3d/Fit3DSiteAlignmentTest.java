package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.chemistry.physical.branches.StructuralMotifs;
import de.bioforscher.chemistry.physical.families.MatcherFamily;
import de.bioforscher.chemistry.physical.families.substitution.matrices.SubstitutionMatrix;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static de.bioforscher.chemistry.physical.model.StructuralEntityFilter.AtomFilter;

/**
 * @author fk
 */
public class Fit3DSiteAlignmentTest {

    private StructuralMotif bindingSite1;
    private StructuralMotif bindingSite2;

    @Before
    public void setUp() throws IOException {
        Structure bindingSiteStructure1 = StructureParser.local()
                .fileLocation(Thread.currentThread().getContextClassLoader().getResource("Asp_1c0a.pdb").getFile())
                .everything()
                .parse();
        this.bindingSite1 = StructuralMotif.fromLeafs(1, bindingSiteStructure1.getAllLeafs());
        Structure bindingSiteStructure2 = StructureParser.local()
                .fileLocation(Thread.currentThread().getContextClassLoader().getResource("Asn_3m4p.pdb").getFile())
                .everything()
                .parse();
        this.bindingSite2 = StructuralMotif.fromLeafs(1, bindingSiteStructure2.getAllLeafs());

//        Structure bindingSiteStructure1 = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
//                .getResourceAsStream("2OCF_A.pdb"));
//        this.bindingSite1 = StructuralMotif.fromLeafs(1, bindingSiteStructure1,
//                LeafIdentifiers.of("A-346", "A-350", "A-353", "A-387", "A-394", "A-404", "A-524"));
//        Structure bindingSiteStructure2 = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
//                .getResourceAsStream("1FDW_A.pdb"));
//        this.bindingSite2 = StructuralMotif.fromLeafs(1, bindingSiteStructure2,
//                LeafIdentifiers.of("A-143", "A-149", "A-186", "A-218", "A-225", "A-259"));
    }

    @Test
    public void shouldCreateBindingSiteAlignment() throws IOException {
//
//
//        LabeledSymmetricMatrix<String> blosum = (LabeledSymmetricMatrix<String>) Matrices.readLabeledMatrixFromCSV(Paths.get("/home/fkaiser/Workspace/IdeaProjects/singa/chemistry/src/main/resources/physical/families/substitution/matrices/BLOSUM45.csv"));
//        List<String> mappedLabels = new ArrayList<>();
//        for (int i = 0; i < blosum.getRowDimension(); i++) {
//            Optional<AminoAcidFamily> label = AminoAcidFamily.getAminoAcidTypeByOneLetterCode(blosum.getRowLabel(i));
//            String stringLabel;
//            if (label.isPresent()) {
//                stringLabel = label.get().name();
//            }else
//                stringLabel = blosum.getRowLabel(i);
//            blosum.setRowLabel(stringLabel,i);
//        }
//        System.out.println(blosum.getStringRepresentation());
//        LabeledSymmetricMatrix<String> blosum2 = (LabeledSymmetricMatrix<String>) Matrices.readLabeledMatrixFromCSV(Paths.get("/home/fkaiser/Workspace/IdeaProjects/singa/chemistry/src/main/resources/physical/families/substitution/matrices/BLOSUM45.csv"));
//
//        blosum2.setRowLabels(mappedLabels);
//        blosum.setColumnLabels(mappedLabels);
//
//        System.out.println(blosum.getStringRepresentation());
//
        Fit3D fit3d = Fit3DBuilder.create()
                .site(this.bindingSite1)
                .vs(this.bindingSite2)
                .cutoffScore(0.5)
                .substitutionMatrix(SubstitutionMatrix.BLOSUM_45)
                .finishConfiguration()
                .exhaustive()
                .atomFilter(AtomFilter.isBackbone())
                .run();
    }

    @Test
    public void shouldCreateGutteridgeBindingSiteAlignment() {
        // exchanges have only be added for one of the sites because they are transitive
        StructuralMotifs.assignExchanges(this.bindingSite1, MatcherFamily.GUTTERIDGE);
        Fit3D fit3d = Fit3DBuilder.create()
                .site(this.bindingSite1)
                .vs(this.bindingSite2)
                .restrictToSpecifiedExchanges()
                .atomFilter(AtomFilter.isBackbone())
                .run();
    }

    @Test
    public void shouldCalculateXieScore() {


    }
}