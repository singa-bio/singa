package bio.singa.structure.algorithms.superimposition;


import bio.singa.core.utility.Resources;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeFactory;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import bio.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;
import bio.singa.structure.model.oak.LeafIdentifiers;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Chain;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.PdbLeafIdentifier;
import bio.singa.structure.model.oak.StructuralEntityFilter;
import bio.singa.structure.model.oak.StructuralMotif;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * A test class for the {@link SubstructureSuperimposer} implementation.
 *
 * @author fk
 */
class SubstructureSuperimposerTest {

    private static Chain candidate;
    private static Chain reference;

    @BeforeAll
    static void initialize() {
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
    void shouldCalculateLastHeavySidechainSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(reference, candidate,
                        RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.LAST_HEAVY_SIDE_CHAIN));
        assertEquals(0.5706912104847501, superimposition.getRmsd(), 1E-9);
    }

    @Test
    void shouldCalculateSidechainCentroidSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(reference, candidate,
                        RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.SIDE_CHAIN_CENTROID));
        assertEquals(0.05433403549113087, superimposition.getRmsd(), 1E-9);
    }

    @Test
    void shouldCalculateCaSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(reference, candidate, StructuralEntityFilter.AtomFilter.isAlphaCarbon());
        List<LeafSubstructure> reconstructedAndMappedCandidate =
                superimposition.applyTo(new ArrayList<>(candidate.getAllLeafSubstructures()));
        assertEquals(superimposition.getMappedCandidate().stream()
                .mapToLong(subStructure -> subStructure.getAllAtoms().size())
                .sum(), 3);
        assertEquals(reconstructedAndMappedCandidate.size(), reference.getNumberOfLeafSubstructures());
    }

    @Test
    void shouldCalculateBackboneSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(reference, candidate, StructuralEntityFilter.AtomFilter.isBackbone());
        List<LeafSubstructure> reconstructedAndMappedCandidate =
                superimposition.applyTo(new ArrayList<>(candidate.getAllLeafSubstructures()));
        assertEquals(superimposition.getMappedCandidate().stream()
                .mapToLong(subStructure -> subStructure.getAllAtoms().size())
                .sum(), 12);
        assertEquals(reconstructedAndMappedCandidate.size(), reference.getNumberOfLeafSubstructures());
    }

    @Test
    void shouldCalculateSideChainSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateSubstructureSuperimposition(reference, candidate, StructuralEntityFilter.AtomFilter.isSideChain());
        List<LeafSubstructure> reconstructedAndMappedCandidate =
                superimposition.applyTo(new ArrayList<>(candidate.getAllLeafSubstructures()));
        assertEquals(12, superimposition.getMappedCandidate().stream()
                .mapToLong(subStructure -> subStructure.getAllAtoms().size())
                .sum());
        assertEquals(reconstructedAndMappedCandidate.size(), reference.getNumberOfLeafSubstructures());
    }

    @Test
    void shouldCalculateIdealSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateIdealSubstructureSuperimposition(reference, candidate);
        assertEquals(0.6439715367058053, superimposition.getRmsd(), 1E-9);
    }

    @Test
    void shouldCalculateKuhnMunkresSubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateKuhnMunkresSubstructureSuperimposition(reference, candidate, SubstitutionMatrix.BLOSUM_45);
        assertEquals(0.6439715367058053, superimposition.getRmsd(), 1E-9);
    }

    @Test
    void shouldCalculateMappedFullCandidates() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateIdealSubstructureSuperimposition(reference, candidate, StructuralEntityFilter.AtomFilter.isBackbone());
        assertEquals(24, superimposition.getMappedFullCandidate().stream()
                .map(LeafSubstructure::getAllAtoms)
                .mapToLong(Collection::size)
                .sum());
    }

    @Test
    void shouldCorrectlyApplySubstructureSuperimposition() {
        SubstructureSuperimposition superimposition = SubstructureSuperimposer
                .calculateIdealSubstructureSuperimposition(reference, candidate);
        assertEquals(0.6439715367058053, superimposition.getRmsd(), 1E-9);
        List<LeafSubstructure> mappedCandidate = superimposition.applyTo(new ArrayList<>(candidate.getAllLeafSubstructures()));

        List<AminoAcid> aminoAcids = candidate.getAllAminoAcids();
        for (int i = 0; i < aminoAcids.size(); i++) {
            assertArrayEquals(candidate.getAllAminoAcids().get(i).getPosition().getElements(), mappedCandidate.get(i).getPosition().getElements(), 1E-3);
        }
    }

    @Test
    void shouldCorrectlyAlignWithMMTF() {
        Structure first = StructureParser.mmtf()
                .pdbIdentifier("1cd9")
                .parse();
        List<PdbLeafIdentifier> firstIdentifiers = LeafIdentifiers.of("C-68", "C-70");
        StructuralMotif firstMotif = StructuralMotif.fromLeafIdentifiers(first, firstIdentifiers);

        Structure second = StructureParser.mmtf()
                .pdbIdentifier("1cn4")
                .parse();
        List<PdbLeafIdentifier> secondIdentifiers = LeafIdentifiers.of("A-58", "A-59");
        StructuralMotif secondMotif = StructuralMotif.fromLeafIdentifiers(second, secondIdentifiers);

        SubstructureSuperimposition mmtfSuperimposition = SubstructureSuperimposer.calculateSubstructureSuperimposition(firstMotif, secondMotif, StructuralEntityFilter.AtomFilter.isArbitrary());

        first = StructureParser.pdb()
                .pdbIdentifier("1cd9")
                .parse();
        firstMotif = StructuralMotif.fromLeafIdentifiers(first, firstIdentifiers);

        second = StructureParser.pdb()
                .pdbIdentifier("1cn4")
                .parse();
        secondMotif = StructuralMotif.fromLeafIdentifiers(second, secondIdentifiers);

        SubstructureSuperimposition pdbSuperimposition = SubstructureSuperimposer.calculateSubstructureSuperimposition(firstMotif, secondMotif, StructuralEntityFilter.AtomFilter.isArbitrary());

        assertEquals(mmtfSuperimposition.getRmsd(), pdbSuperimposition.getRmsd(), 1E-6);
    }
}