package de.bioforscher.chemistry.algorithms.superimposition;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.model.SubStructure;
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
public class SubStructureSuperimposerTest {
    private SubStructure candidate;
    private SubStructure reference;

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
        SubStructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate, AtomFilter.isAlphaCarbon());
        List<SubStructure> reconstructedAndMappedCandidate =
                superimposition.applyTo(this.candidate.getAtomContainingSubstructures());
        assertEquals(superimposition.getMappedCandidate().stream()
                .flatMap(subStructure -> subStructure.getAllAtoms().stream())
                .count(), 3);
        assertEquals(reconstructedAndMappedCandidate.size(), this.reference.getAtomContainingSubstructures().size());
    }

    @Test
    public void shouldCalculateBackboneSubstructureSuperimposition() {
        SubStructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate, AtomFilter.isBackbone());
        List<SubStructure> reconstructedAndMappedCandidate =
                superimposition.applyTo(this.candidate.getAtomContainingSubstructures());
        assertEquals(superimposition.getMappedCandidate().stream()
                .flatMap(subStructure -> subStructure.getAllAtoms().stream())
                .count(), 12);
        assertEquals(reconstructedAndMappedCandidate.size(), this.reference.getAtomContainingSubstructures().size());
    }

    @Test
    public void shouldCalculateSidechainSubstructureSuperimposition() {
        SubStructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateSubstructureSuperimposition(this.reference, this.candidate, AtomFilter.isSidechain());
        List<SubStructure> reconstructedAndMappedCandidate =
                superimposition.applyTo(this.candidate.getAtomContainingSubstructures());
        assertEquals(superimposition.getMappedCandidate().stream()
                .flatMap(subStructure -> subStructure.getAllAtoms().stream())
                .count(), 12);
        assertEquals(reconstructedAndMappedCandidate.size(), this.reference.getAtomContainingSubstructures().size());
    }

    @Test
    public void shouldCalculateIdealSubStructureSuperimposition() {
        SubStructureSuperimposition superimposition = SubStructureSuperimposer
                .calculateIdealSubstructureSuperimposition(this.reference, this.candidate);
        assertEquals(superimposition.getRmsd(), 0.6439715367058053, 0E-9);
    }

    // TODO this does not work because getCopy() of Chain currently produces a NPE
//    @Test
//    public void shouldCalculateSubstructureSuperimpositionWithMissingAtoms() {
//        SubStructure referenceWithMissingAtoms = this.reference.getCopy();
//        SubStructureSuperimposition superimposition = SubStructureSuperimposer
//                .calculateSubstructureSuperimposition(referenceWithMissingAtoms, this.candidate);
//        List<SubStructure> reconstructedAndMappedCandidate =
//                superimposition.applyTo(this.candidate.getAtomContainingSubstructures());
//        assertEquals(superimposition.getMappedCandidate().stream()
//                .flatMap(subStructure -> subStructure.getAllAtoms().stream())
//                .count(), 3);
//        assertEquals(reconstructedAndMappedCandidate.size(),
//                referenceWithMissingAtoms.getAtomContainingSubstructures().size());
//    }
}