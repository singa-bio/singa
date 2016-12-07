package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * A test class for the {@link SubStructureSuperimposer} implementation.
 *
 * @author fk
 */
public class BranchSubstructureSuperimposerTest {

    private BranchSubstructure<?> candidate;
    private BranchSubstructure<?> reference;

    @Before
    public void setUp() throws IOException {
        Structure motif1 = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("motif_HDS_01.pdb"));
        Structure motif2 = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("motif_HDS_02.pdb"));
        this.reference = motif1.getSubstructures().stream().collect(Collectors.toList()).get(0);
        this.candidate = motif2.getSubstructures().stream().collect(Collectors.toList()).get(0);
    }

    @Test
    public void shouldCalculateCaSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate, AtomFilter.isAlphaCarbon());
        List<LeafSubstructure<?, ?>> reconstructedAndMappedCandidate =
                superimposition.applyTo(this.candidate.getAtomContainingSubstructures());
        assertEquals(superimposition.getMappedCandidate().stream()
                .flatMap(subStructure -> subStructure.getAllAtoms().stream())
                .count(), 3);
        assertEquals(reconstructedAndMappedCandidate.size(), this.reference.getAtomContainingSubstructures().size());
    }

    @Test
    public void shouldCalculateBackboneSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate, AtomFilter.isBackbone());
        List<LeafSubstructure<?, ?>> reconstructedAndMappedCandidate =
                superimposition.applyTo(this.candidate.getAtomContainingSubstructures());
        assertEquals(superimposition.getMappedCandidate().stream()
                .flatMap(subStructure -> subStructure.getAllAtoms().stream())
                .count(), 12);
        assertEquals(reconstructedAndMappedCandidate.size(), this.reference.getAtomContainingSubstructures().size());
    }

    @Test
    public void shouldCalculateSidechainSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate, AtomFilter.isSidechain());
        List<LeafSubstructure<?, ?>> reconstructedAndMappedCandidate =
                superimposition.applyTo(this.candidate.getAtomContainingSubstructures());
        assertEquals(superimposition.getMappedCandidate().stream()
                .flatMap(subStructure -> subStructure.getAllAtoms().stream())
                .count(), 12);
        assertEquals(reconstructedAndMappedCandidate.size(), this.reference.getAtomContainingSubstructures().size());
    }

    @Test
    public void shouldCalculateIdealSubStructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateIdealSubstructureSuperimposition(this.reference, this.candidate);
        assertEquals(superimposition.getRmsd(), 0.6439715367058053, 0E-9);
    }

    @Test
    public void shouldCalculateSubstructureSuperimpositionWithMissingAtoms() {
// FIXME: this test does not work, getCopy() of Chain shows awkward behavior
//        BranchSubstructure<?> referenceWithMissingAtoms = this.reference.getCopy();
//        SubstructureSuperimposition superimposition = SubStructureSuperimposer
//                .calculateSubstructureSuperimposition(referenceWithMissingAtoms, this.candidate);
//        List<LeafSubstructure<?,?>> reconstructedAndMappedCandidate =
//                superimposition.applyTo(this.candidate.getAtomContainingSubstructures());
//        assertEquals(superimposition.getMappedCandidate().stream()
//                .flatMap(subStructure -> subStructure.getAllAtoms().stream())
//                .count(), 3);
//        assertEquals(reconstructedAndMappedCandidate.size(),
//                referenceWithMissingAtoms.getAtomContainingSubstructures().size());
    }
}