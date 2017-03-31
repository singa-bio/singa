package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.chemistry.parser.pdb.structures.StructureSources;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationSchemeFactory;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationSchemeType;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static de.bioforscher.chemistry.physical.model.StructuralEntityFilter.AtomFilter.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * A test class for the {@link SubStructureSuperimposer} implementation.
 *
 * @author fk
 */
public class SubstructureSuperimposerTest {

    private BranchSubstructure<?> candidate;
    private BranchSubstructure<?> reference;

    @Before
    public void setUp() throws IOException {
        Structure motif1 = StructureParser.local()
                .fileLocation(Thread.currentThread().getContextClassLoader().getResource("motif_HDS_01.pdb").getFile())
                .parse();
        Structure motif2 = StructureParser.local()
                .fileLocation(Thread.currentThread().getContextClassLoader().getResource("motif_HDS_02.pdb").getFile())
                .parse();
        this.reference = motif1.getAllChains().get(0);
        this.candidate = motif2.getAllChains().get(0);
    }

    @Test
    public void shouldCalculateLastHeavySidechainSuperimposition() {
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate,
                        RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.LAST_HEAVY_SIDECHAIN));
        assertEquals(0.5706912104847501, superimposition.getRmsd(), 0E-9);
    }

    @Test
    public void shouldCalculateSidechainCentroidSuperimposition() {
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate,
                        RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.SIDECHAIN_CENTROID));
        assertEquals(0.05433403549113087, superimposition.getRmsd(), 0E-9);
    }

    @Test
    public void shouldCalculateCaSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
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
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
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
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate, isSidechain());
        List<LeafSubstructure<?, ?>> reconstructedAndMappedCandidate =
                superimposition.applyTo(this.candidate.getLeafSubstructures());
        assertEquals(superimposition.getMappedCandidate().stream()
                .mapToLong(subStructure -> subStructure.getAllAtoms().size())
                .sum(), 12);
        assertEquals(reconstructedAndMappedCandidate.size(), this.reference.getLeafSubstructures().size());
    }

    @Test
    public void shouldCalculateIdealSubStructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateIdealSubstructureSuperimposition(this.reference, this.candidate);
        assertEquals(0.6439715367058053, superimposition.getRmsd(), 0E-9);
    }

    @Test
    public void shouldCalculateMappedFullCandidates() {
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateIdealSubstructureSuperimposition(this.reference, this.candidate, isBackbone());
        superimposition.getMappedFullCandidate().stream().map(leaf -> leaf.getPdbLines().stream().collect(Collectors.joining("\n"))).forEach(System.out::println);
        assertEquals(24, superimposition.getMappedFullCandidate().stream()
                .map(LeafSubstructure::getAllAtoms)
                .mapToLong(Collection::size)
                .sum());
    }

    @Test
    public void shouldCorrectlyApplySubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
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
//        SubstructureSuperimposition superimposition = SubStructureSuperimposer
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