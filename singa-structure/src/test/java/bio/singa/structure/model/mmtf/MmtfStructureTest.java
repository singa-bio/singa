package bio.singa.structure.model.mmtf;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.oak.PdbLeafIdentifier;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rcsb.mmtf.decoder.ReaderUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author cl
 */
class MmtfStructureTest {

    private static Structure structure2N5E;
    private static Structure structure1C0A;

    @BeforeAll
    static void initialize() throws IOException {
        structure2N5E = new MmtfStructure(ReaderUtils.getByteArrayFromUrl("2N5E"));
        structure1C0A = new MmtfStructure(ReaderUtils.getByteArrayFromUrl("1C0A"));
    }

    @Test
    void shouldIgnoreAlternativePosition() throws IOException {
        final Structure mmtfStructure = new MmtfStructure(ReaderUtils.getByteArrayFromUrl("1dlf"));
        final Structure oakStructure = StructureParser.pdb().pdbIdentifier("1dlf").parse();
        final LeafIdentifier leafIdentifier = new PdbLeafIdentifier("1dlf", 1, "H", 70);

        LeafSubstructure mmtfLeaf = mmtfStructure.getLeafSubstructure(leafIdentifier).get();
        LeafSubstructure oakLeaf = oakStructure.getLeafSubstructure(leafIdentifier).get();
        mmtfLeaf.getAllAtoms();
        assertEquals(oakLeaf.getAllAtoms().size(), mmtfLeaf.getAllAtoms().size());

    }

    @Test
    void getPdbIdentifier() {
        String actual = structure2N5E.getPdbIdentifier();
        assertEquals("2n5e", actual);
    }

    @Test
    void getTitle() {
        String actual = structure2N5E.getTitle();
        assertEquals("The 3D solution structure of discoidal high-density lipoprotein particles", actual);
    }

    @Test
    void getAllModels() {
        List<Model> allModels = structure2N5E.getAllModels();
        assertEquals(10, allModels.size());
    }

    @Test
    void getFirstModel() {
        Model model = structure2N5E.getFirstModel();
        assertEquals(1, (int) model.getModelIdentifier());
    }

    @Test
    void getModel() {
        Optional<Model> model = structure2N5E.getModel(2);
        if (!model.isPresent()) {
            fail("Optional model was empty.");
        }
        assertEquals(2, (int) model.get().getModelIdentifier());
    }

    @Test
    void getAllChains() {
        List<Chain> allChains = structure2N5E.getAllChains();
        assertEquals(20, allChains.size());
    }

    @Test
    void getFirstChain() {
        Chain firstChain = structure2N5E.getFirstChain();
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    void getChain() {
        Optional<Chain> chain = structure2N5E.getChain(1, "B");
        if (!chain.isPresent()) {
            fail("Optional chain was empty.");
        }
        assertEquals("B", chain.get().getChainIdentifier());
    }

    @Test
    void getAllLeafSubstructures() {
        List<LeafSubstructure> leafSubstructures = structure2N5E.getAllLeafSubstructures();
        assertEquals(3340, leafSubstructures.size());
    }

    @Test
    void getLeafSubstructure() {
        Optional<LeafSubstructure> leafSubstructure = structure2N5E.getLeafSubstructure(new PdbLeafIdentifier("2N5E", 5, "A", 64));
        if (!leafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = leafSubstructure.get().getIdentifier();
        assertEquals(5, identifier.getModelIdentifier());
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(64, identifier.getSerial());
    }

    @Test
    void getAllAminoAcids() {
        final List<AminoAcid> aminoAcids = structure1C0A.getAllAminoAcids();
        assertEquals(585, aminoAcids.size());
    }

    @Test
    void getAminoAcid() {
        final Optional<AminoAcid> aminoAcid = structure1C0A.getAminoAcid(new PdbLeafIdentifier("1c0a", 1, "A", 98));
        if (!aminoAcid.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = aminoAcid.get().getIdentifier();
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(98, identifier.getSerial());
        assertEquals("Ser", aminoAcid.get().getThreeLetterCode());
    }

    @Test
    void getAllNucleotides() {
        final List<Nucleotide> nucleotides = structure1C0A.getAllNucleotides();
        assertEquals(68, nucleotides.size());
    }

    @Test
    void getNucleotide() {
        final Optional<Nucleotide> nucleotide = structure1C0A.getNucleotide(new PdbLeafIdentifier("1c0a", 1, "B", 617));
        if (!nucleotide.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = nucleotide.get().getIdentifier();
        assertEquals("B", identifier.getChainIdentifier());
        assertEquals(617, identifier.getSerial());
        assertEquals("C", nucleotide.get().getThreeLetterCode());
    }

    @Test
    void getAllLigands() {
        final List<Ligand> ligands = structure1C0A.getAllLigands();
        assertEquals(526, ligands.size());
    }

    @Test
    void getLigand() {
        final Optional<Ligand> nucleotide = structure1C0A.getLigand(new PdbLeafIdentifier("1c0a", 1, "A", 831));
        if (!nucleotide.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = nucleotide.get().getIdentifier();
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(831, identifier.getSerial());
        assertEquals("AMO", nucleotide.get().getThreeLetterCode());
    }

    @Test
    void getAllAtoms() {
        final List<Atom> atoms = structure1C0A.getAllAtoms();
        assertEquals(6820, atoms.size());
    }

    @Test
    void getAtom() {
        final Optional<Atom> atom = structure1C0A.getAtom(15);
        if (!atom.isPresent()) {
            fail("Optional atom was empty.");
        }
        assertEquals("C8", atom.get().getAtomName());
        assertEquals(new Vector3D(46.50600051879883, 18.077999114990234, -5.64900016784668), atom.get().getPosition());
    }

}