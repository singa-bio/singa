package bio.singa.structure.algorithms.superimposition.fit3d;

import bio.singa.core.utility.Resources;
import bio.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;
import bio.singa.structure.model.families.MatcherFamily;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.StructuralEntityFilter;
import bio.singa.structure.model.oak.StructuralMotif;
import bio.singa.structure.model.oak.StructuralMotifs;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fk
 */
class Fit3DSiteAlignmentTest {

    private StructuralMotif bindingSite1;
    private StructuralMotif bindingSite2;

    @BeforeEach
    void initialize() {
        Structure bindingSiteStructure1 = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("truncated_1asz_A_renum.pdb"))
                .everything()
                .parse();
        bindingSite1 = StructuralMotif.fromLeafSubstructures(bindingSiteStructure1.getAllLeafSubstructures());
        Structure bindingSiteStructure2 = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("truncated_3m4p_A_renum.pdb"))
                .everything()
                .parse();
        bindingSite2 = StructuralMotif.fromLeafSubstructures(bindingSiteStructure2.getAllLeafSubstructures());
    }

    @Test
    void shouldCreateBindingSiteAlignment() {
        Fit3D fit3d = Fit3DBuilder.create()
                .site(bindingSite1)
                .vs(bindingSite2)
                .cutoffScore(0.35)
                .substitutionMatrix(SubstitutionMatrix.MC_LACHLAN)
                .exhaustive()
                .atomFilter(StructuralEntityFilter.AtomFilter.isBackbone())
                .run();
        assertEquals(0.29745276335597537, fit3d.getMatches().get(0).getRmsd(), 1E-6);
        assertEquals(7.459982645433789, fit3d.getXieScore().getScore(), 1E-6);
        assertEquals(0.05689220664553862, fit3d.getXieScore().getNormalizedScore(), 1E-6);
        assertEquals(0.019884604882031143, fit3d.getXieScore().getSignificance(), 1E-6);
        assertEquals(0.23914485254903425, fit3d.getPsScore().getScore(), 1E-6);
        assertEquals(0.9999999997577196, fit3d.getPsScore().getSignificance(), 1E-6);
    }

    @Test
    void shouldAlignBindingSitesKuhnMunkres() {
        Fit3D fit3d = Fit3DBuilder.create()
                .site(bindingSite1)
                .vs(bindingSite2)
                .kuhnMunkres(SubstitutionMatrix.MC_LACHLAN, false)
                .run();
        assertEquals(10.367884580510683, fit3d.getMatches().get(0).getRmsd(), 1E-6);
    }

    @Test
    void shouldCreateGutteridgeBindingSiteAlignment() {
        // exchanges have only be added for one of the sites because they are transitive
        StructuralMotifs.assignComplexExchanges(bindingSite1, MatcherFamily.GUTTERIDGE);
        Fit3D fit3d = Fit3DBuilder.create()
                .site(bindingSite1)
                .vs(bindingSite2)
                .restrictToSpecifiedExchanges()
                .atomFilter(StructuralEntityFilter.AtomFilter.isBackbone())
                .run();
        assertEquals(0.5517396481341506, fit3d.getMatches().get(0).getRmsd(), 1E-6);
        assertEquals(53.99934784373183, fit3d.getXieScore().getScore(), 1E-6);
        assertEquals(0.37573008273142394, fit3d.getXieScore().getNormalizedScore(), 1E-6);
        assertEquals(0.0, fit3d.getXieScore().getSignificance(), 1E-6);
        assertEquals(0.28734633467389215, fit3d.getPsScore().getScore(), 1E-6);
        assertEquals(0.7854483831005059, fit3d.getPsScore().getSignificance(), 1E-6);
    }
}