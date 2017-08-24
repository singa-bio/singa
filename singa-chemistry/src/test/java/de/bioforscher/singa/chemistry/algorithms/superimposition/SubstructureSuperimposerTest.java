package de.bioforscher.singa.chemistry.algorithms.superimposition;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationSchemeFactory;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationSchemeType;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.Resources;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * A test class for the {@link SubstructureSuperimposer} implementation.
 *
 * @author fk
 */
public class SubstructureSuperimposerTest {

    private BranchSubstructure<?, ?> candidate;
    private BranchSubstructure<?, ?> reference;

    @Before
    public void setUp() throws IOException {
        Structure motif1 = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("motif_HDS_01.pdb"))
                .parse();
        Structure motif2 = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("motif_HDS_02.pdb"))
                .parse();
        this.reference = motif1.getAllChains().get(0);
        this.candidate = motif2.getAllChains().get(0);
    }

    @Test
    public void shouldCalculateLastHeavySidechainSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate,
                        RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.LAST_HEAVY_SIDE_CHAIN));
        assertEquals(0.5706912104847501, superimposition.getRmsd(), 0E-9);
    }

    @Test
    public void shouldCalculateSidechainCentroidSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate,
                        RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.SIDE_CHAIN_CENTROID));
        assertEquals(0.05433403549113087, superimposition.getRmsd(), 0E-9);
    }

    @Test
    public void shouldCalculateCaSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate, isAlphaCarbon());
        List<LeafSubstructure<?, ?>> reconstructedAndMappedCandidate =
                superimposition.applyTo(this.candidate.getLeafSubstructures());
        assertEquals(superimposition.getMappedCandidate().stream()
                .mapToLong(subStructure -> subStructure.getAllAtoms().size())
                .sum(), 3);
        assertEquals(reconstructedAndMappedCandidate.size(), this.reference.getLeafSubstructures().size());
    }

    @Test
    public void shouldCalculateBackboneSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate, isBackbone());
        List<LeafSubstructure<?, ?>> reconstructedAndMappedCandidate =
                superimposition.applyTo(this.candidate.getLeafSubstructures());
        assertEquals(superimposition.getMappedCandidate().stream()
                .mapToLong(subStructure -> subStructure.getAllAtoms().size())
                .sum(), 12);
        assertEquals(reconstructedAndMappedCandidate.size(), this.reference.getLeafSubstructures().size());
    }

    @Test
    public void shouldCalculateSidechainSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate, isSideChain());
        List<LeafSubstructure<?, ?>> reconstructedAndMappedCandidate =
                superimposition.applyTo(this.candidate.getLeafSubstructures());
        assertEquals(12, superimposition.getMappedCandidate().stream()
                .mapToLong(subStructure -> subStructure.getAllAtoms().size())
                .sum());
        assertEquals(reconstructedAndMappedCandidate.size(), this.reference.getLeafSubstructures().size());
    }

    @Test
    public void shouldCalculateIdealSubStructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateIdealSubstructureSuperimposition(this.reference, this.candidate);
        assertEquals(0.6439715367058053, superimposition.getRmsd(), 0E-9);
    }

    @Test
    public void shouldCalculateMappedFullCandidates() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateIdealSubstructureSuperimposition(this.reference, this.candidate, isBackbone());
        superimposition.getMappedFullCandidate().stream().map(leaf -> leaf.getPdbLines().stream().collect(Collectors.joining("\n"))).forEach(System.out::println);
        assertEquals(24, superimposition.getMappedFullCandidate().stream()
                .map(LeafSubstructure::getAllAtoms)
                .mapToLong(Collection::size)
                .sum());
    }

    @Test
    public void shouldCorrectlyApplySubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateIdealSubstructureSuperimposition(this.reference, this.candidate);
        assertEquals(0.6439715367058053, superimposition.getRmsd(), 0E-9);
        List<LeafSubstructure<?, ?>> mappedCandidate = superimposition.applyTo(this.candidate.getLeafSubstructures());

        List<AminoAcid> aminoAcids = this.candidate.getAminoAcids();
        for (int i = 0; i < aminoAcids.size(); i++) {
            assertArrayEquals(this.candidate.getAminoAcids().get(i).getPosition().getElements(), mappedCandidate.get(i).getPosition().getElements(), 1E-3);
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