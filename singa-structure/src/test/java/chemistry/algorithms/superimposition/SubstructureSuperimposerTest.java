package chemistry.algorithms.superimposition;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationSchemeFactory;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationSchemeType;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.Resources;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter.*;

/**
 * A test class for the {@link SubstructureSuperimposer} implementation.
 *
 * @author fk
 */
public class SubstructureSuperimposerTest {

    private static BranchSubstructure<?, ?> candidate;
    private static BranchSubstructure<?, ?> reference;

    @BeforeClass
    public static void setup() throws IOException {
        Structure motif1 = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("motif_HDS_01.pdb"))
                .parse();
        Structure motif2 = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("motif_HDS_02.pdb"))
                .parse();
        reference = motif1.getFirstChain();
        candidate = motif2.getFirstChain();
    }

    @Test
    public void shouldCalculateLastHeavySidechainSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(reference, candidate,
                        RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.LAST_HEAVY_SIDE_CHAIN));
        assertEquals(0.5706912104847501, superimposition.getRmsd(), 0E-9);
    }

    @Test
    public void shouldCalculateSidechainCentroidSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(reference, candidate,
                        RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.SIDE_CHAIN_CENTROID));
        assertEquals(0.05433403549113087, superimposition.getRmsd(), 0E-9);
    }

    @Test
    public void shouldCalculateCaSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(reference, candidate, isAlphaCarbon());
        List<LeafSubstructure<?, ?>> reconstructedAndMappedCandidate =
                superimposition.applyTo(candidate.getLeafSubstructures());
        assertEquals(superimposition.getMappedCandidate().stream()
                .mapToLong(subStructure -> subStructure.getAllAtoms().size())
                .sum(), 3);
        assertEquals(reconstructedAndMappedCandidate.size(), reference.getLeafSubstructures().size());
    }

    @Test
    public void shouldCalculateBackboneSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(reference, candidate, isBackbone());
        List<LeafSubstructure<?, ?>> reconstructedAndMappedCandidate =
                superimposition.applyTo(candidate.getLeafSubstructures());
        assertEquals(superimposition.getMappedCandidate().stream()
                .mapToLong(subStructure -> subStructure.getAllAtoms().size())
                .sum(), 12);
        assertEquals(reconstructedAndMappedCandidate.size(), reference.getLeafSubstructures().size());
    }

    @Test
    public void shouldCalculateSidechainSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(reference, candidate, isSideChain());
        List<LeafSubstructure<?, ?>> reconstructedAndMappedCandidate =
                superimposition.applyTo(candidate.getLeafSubstructures());
        assertEquals(12, superimposition.getMappedCandidate().stream()
                .mapToLong(subStructure -> subStructure.getAllAtoms().size())
                .sum());
        assertEquals(reconstructedAndMappedCandidate.size(), reference.getLeafSubstructures().size());
    }

    @Test
    public void shouldCalculateIdealSubStructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateIdealSubstructureSuperimposition(reference, candidate);
        assertEquals(0.6439715367058053, superimposition.getRmsd(), 0E-9);
    }

    @Test
    public void shouldCalculateMappedFullCandidates() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateIdealSubstructureSuperimposition(reference, candidate, isBackbone());
        superimposition.getMappedFullCandidate().stream().map(leaf -> leaf.getPdbLines().stream().collect(Collectors.joining("\n"))).forEach(System.out::println);
        assertEquals(24, superimposition.getMappedFullCandidate().stream()
                .map(LeafSubstructure::getAllAtoms)
                .mapToLong(Collection::size)
                .sum());
    }

    @Test
    public void shouldCorrectlyApplySubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateIdealSubstructureSuperimposition(reference, candidate);
        assertEquals(0.6439715367058053, superimposition.getRmsd(), 0E-9);
        List<LeafSubstructure<?, ?>> mappedCandidate = superimposition.applyTo(candidate.getLeafSubstructures());

        List<AminoAcid> aminoAcids = candidate.getAminoAcids();
        for (int i = 0; i < aminoAcids.size(); i++) {
            assertArrayEquals(candidate.getAminoAcids().get(i).getPosition().getElements(), mappedCandidate.get(i).getPosition().getElements(), 1E-3);
        }
    }

    @Test
    public void shouldCalculateSubstructureSuperimpositionWithMissingAtoms() {
// FIXME: this test does not work, getCopy() of Chain shows awkward behavior
//        BranchSubstructure<?> referenceWithMissingAtoms = this.reference.getCopy();
//        SubstructureSuperimposition superimposition = SubstructureSuperimposer
//                .calculateSubstructureSuperimposition(referenceWithMissingAtoms, this.candidate);
//        List<LeafSubstructure<?,?>> reconstructedAndMappedCandidate =
//                superimposition.applyTo(this.candidate.getLeafSubstructures());
//        assertEquals(superimposition.getMappedCandidate().stream()
//                .flatMap(subStructure -> subStructure.getAllAtoms().stream())
//                .count(), 3);
//        assertEquals(reconstructedAndMappedCandidate.size(),
//                referenceWithMissingAtoms.getLeafSubstructures().size());
    }
}