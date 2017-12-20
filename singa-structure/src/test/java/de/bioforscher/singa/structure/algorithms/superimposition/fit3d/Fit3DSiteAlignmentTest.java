package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;

import de.bioforscher.singa.core.utility.Resources;
import de.bioforscher.singa.mathematics.algorithms.optimization.KuhnMunkres;
import de.bioforscher.singa.mathematics.matrices.LabeledMatrix;
import de.bioforscher.singa.mathematics.matrices.LabeledRegularMatrix;
import de.bioforscher.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;
import de.bioforscher.singa.structure.model.families.MatcherFamily;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.model.oak.StructuralEntityFilter;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;
import de.bioforscher.singa.structure.model.oak.StructuralMotifs;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author fk
 */
public class Fit3DSiteAlignmentTest {

    private StructuralMotif bindingSite1;
    private StructuralMotif bindingSite2;

    @Before
    public void setUp() throws IOException {
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
    public void shouldCreateBindingSiteAlignment() throws IOException {
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
        assertEquals(0.4514194183508357, fit3d.getPsScore().getScore(), 1E-6);
        assertEquals(1.7615026091299946E-4, fit3d.getPsScore().getSignificance(), 1E-6);
    }

    @Test
    public void shouldAlignBindingSitesKuhnMunkres() {
        double[][] costValues = new double[bindingSite1.size()][bindingSite2.size()];
        for (int i = 0; i < bindingSite1.getAllLeafSubstructures().size(); i++) {
            for (int j = 0; j < bindingSite2.getAllLeafSubstructures().size(); j++) {
                LeafSubstructure<?> residue1 = bindingSite1.getAllLeafSubstructures().get(i);
                LeafSubstructure<?> residue2 = bindingSite2.getAllLeafSubstructures().get(j);
                if (residue1.getFamily() != residue2.getFamily()) {
                    costValues[i][j] = Double.MAX_VALUE;
                    continue;
                }
                costValues[i][j] = SubstitutionMatrix.MC_LACHLAN.getMatrix().getValueForLabel(residue1.getFamily(), residue2.getFamily());
            }
        }
        LabeledMatrix<LeafSubstructure<?>> costMatrix = new LabeledRegularMatrix<>(costValues);
        costMatrix.setRowLabels(bindingSite1.getAllLeafSubstructures());
        costMatrix.setColumnLabels(bindingSite2.getAllLeafSubstructures());
        System.out.println(costMatrix.getStringRepresentation());
        KuhnMunkres<LeafSubstructure<?>> optimization = new KuhnMunkres<>(costMatrix);
        optimization.getAssignedPairs().stream()
                .map(pair -> pair.getFirst().getFamily() + "-" + pair.getSecond().getFamily())
                .forEach(System.out::println);
    }

    @Test
    public void shouldCreateGutteridgeBindingSiteAlignment() {
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
        assertEquals(0.7301071679124315, fit3d.getPsScore().getScore(), 1E-6);
        assertEquals(3.558486838528552E-11, fit3d.getPsScore().getSignificance(), 1E-6);
    }
}