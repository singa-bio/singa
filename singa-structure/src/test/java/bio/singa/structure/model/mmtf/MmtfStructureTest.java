package bio.singa.structure.model.mmtf;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author cl
 */
class MmtfStructureTest {

    private static Structure structure2n5e;
    private static Structure structure1c0a;

    @BeforeAll
    static void initialize() {
        structure2n5e = StructureParser.mmtf()
                .pdbIdentifier("2n5e")
                .everything().parse();
        structure1c0a = StructureParser.mmtf()
                .pdbIdentifier("1c0a")
                .everything().parse();
    }

    @Test
    void shouldIgnoreAlternativePosition() {
        final Structure mmtfStructure = StructureParser.mmtf()
                .pdbIdentifier("1dlf")
                .everything().parse();
        final Structure oakStructure = StructureParser.pdb().pdbIdentifier("1dlf").parse();
        final LeafIdentifier leafIdentifier = new PdbLeafIdentifier("1dlf", 1, "H", 70);

        LeafSubstructure mmtfLeaf = mmtfStructure.getLeafSubstructure(leafIdentifier).get();
        LeafSubstructure oakLeaf = oakStructure.getLeafSubstructure(leafIdentifier).get();
        mmtfLeaf.getAllAtoms();
        assertEquals(oakLeaf.getAllAtoms().size(), mmtfLeaf.getAllAtoms().size());

    }

    @Test
    void getPdbIdentifier() {
        String actual = structure2n5e.getStructureIdentifier();
        assertEquals("2n5e", actual);
    }

    @Test
    void getTitle() {
        String actual = structure2n5e.getTitle();
        assertEquals("The 3D solution structure of discoidal high-density lipoprotein particles", actual);
    }

    @Test
    void getAllModels() {
        Collection<? extends Model> allModels = structure2n5e.getAllModels();
        assertEquals(10, allModels.size());
    }

    @Test
    void getFirstModel() {
        Model model = structure2n5e.getFirstModel();
        assertEquals(1, model.getModelIdentifier());
    }

    @Test
    void getModel() {
        Optional<? extends Model> model = structure2n5e.getModel(2);
        if (!model.isPresent()) {
            fail("Optional model was empty.");
        }
        assertEquals(2, model.get().getModelIdentifier());
    }

    @Test
    void getAllChains() {
        Collection<? extends Chain> allChains = structure2n5e.getAllChains();
        assertEquals(20, allChains.size());
    }

    @Test
    void getFirstChain() {
        Chain firstChain = structure2n5e.getFirstChain();
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    void getChain() {
        Optional<? extends Chain> chain = structure2n5e.getChain(1, "B");
        if (!chain.isPresent()) {
            fail("Optional chain was empty.");
        }
        assertEquals("B", chain.get().getChainIdentifier());
    }

    @Test
    void getAllLeafSubstructures() {
        Collection<? extends LeafSubstructure> leafSubstructures = structure2n5e.getAllLeafSubstructures();
        assertEquals(3340, leafSubstructures.size());
    }

    @Test
    void getLeafSubstructure() {
        Optional<? extends LeafSubstructure> leafSubstructure = structure2n5e.getLeafSubstructure(new PdbLeafIdentifier("2n5e", 5, "A", 64));
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
        final List<AminoAcid> aminoAcids = structure1c0a.getAllAminoAcids();
        assertEquals(585, aminoAcids.size());
    }

    @Test
    void getAminoAcid() {
        final Optional<AminoAcid> aminoAcid = structure1c0a.getAminoAcid(new PdbLeafIdentifier("1c0a", 1, "A", 98));
        if (!aminoAcid.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = aminoAcid.get().getIdentifier();
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(98, identifier.getSerial());
        assertEquals("SER", aminoAcid.get().getThreeLetterCode());
    }

    @Test
    void getAllNucleotides() {
        final List<Nucleotide> nucleotides = structure1c0a.getAllNucleotides();
        assertEquals(68, nucleotides.size());
    }

    @Test
    void getNucleotide() {
        final Optional<Nucleotide> nucleotide = structure1c0a.getNucleotide(new PdbLeafIdentifier("1c0a", 1, "B", 617));
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
        final List<Ligand> ligands = structure1c0a.getAllLigands();
        assertEquals(526, ligands.size());
    }

    @Test
    void getLigand() {
        final Optional<Ligand> nucleotide = structure1c0a.getLigand(new PdbLeafIdentifier("1c0a", 1, "A", 831));
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
        final List<Atom> atoms = structure1c0a.getAllAtoms();
        assertEquals(6820, atoms.size());
    }

    @Test
    void getAtom() {
        final Optional<? extends Atom> atom = structure1c0a.getAtom(15);
        if (!atom.isPresent()) {
            fail("Optional atom was empty.");
        }
        assertEquals("C8", atom.get().getAtomName());
        assertEquals(new Vector3D(46.50600051879883, 18.077999114990234, -5.64900016784668), atom.get().getPosition());
    }

}