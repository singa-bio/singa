package bio.singa.structure.model.pdb;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.general.UniqueAtomIdentifier;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PdbStructureTest {

    private static PdbStructure structure2n5e;
    private static PdbStructure structure1c0a;
    private static PdbStructure structureToModify;

    @BeforeAll
    static void initialize() {
        structure2n5e = (PdbStructure) StructureParser.pdb().pdbIdentifier("2n5e").parse();
        structure1c0a = (PdbStructure) StructureParser.pdb().pdbIdentifier("1c0a").parse();
        structureToModify = (PdbStructure) StructureParser.pdb().pdbIdentifier("1BRR").parse();
    }

    @Test
    void getPdbIdentifier() {
        String actual = structure2n5e.getStructureIdentifier();
        assertEquals("2n5e", actual);
    }

    @Test
    void setPdbIdentifier() {
        structureToModify.setPdbIdentifier("5ING");
        String actual = structureToModify.getStructureIdentifier();
        assertEquals("5ing", actual);
    }

    @Test
    void getTitle() {
        String actual = structure2n5e.getTitle();
        assertEquals("THE 3D SOLUTION STRUCTURE OF DISCOIDAL HIGH-DENSITY LIPOPROTEIN PARTICLES", actual);
    }

    @Test
    void setTitle() {
        structureToModify.setTitle("Test Title");
        String actual = structureToModify.getTitle();
        assertEquals("Test Title", actual);
    }

    @Test
    void getAllModels() {
        Collection<PdbModel> allModels = structure2n5e.getAllModels();
        assertEquals(10, allModels.size());
    }

    @Test
    void getFirstModel() {
        Model model = structure2n5e.getFirstModel();
        assertEquals(1, (int) model.getModelIdentifier());
    }

    @Test
    void getModel() {
        Optional<PdbModel> model = structure2n5e.getModel(2);
        if (!model.isPresent()) {
            fail("Optional model was empty.");
        }
        assertEquals(2, (int) model.get().getModelIdentifier());
    }

    @Test
    void addModel() {
        structureToModify.addModel(new PdbModel(2));
        int actualSize = structureToModify.getAllModels().size();
        assertEquals(2, actualSize);
    }

    @Test
    void getAllChains() {
        List<PdbChain> allChains = structure2n5e.getAllChains();
        assertEquals(20, allChains.size());
    }

    @Test
    void getFirstChain() {
        Chain firstChain = structure2n5e.getFirstChain();
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    void getChain() {
        Optional<PdbChain> chain = structure2n5e.getChain(1, "B");
        if (!chain.isPresent()) {
            fail("Optional chain was empty.");
        }
        assertEquals("B", chain.get().getChainIdentifier());
    }

    @Test
    void getAllLeafSubstructures() {
        List<PdbLeafSubstructure> leafSubstructures = structure2n5e.getAllLeafSubstructures();
        assertEquals(3340, leafSubstructures.size());
    }

    @Test
    void getLeafSubstructure() {
        Optional<PdbLeafSubstructure> leafSubstructure = structure2n5e.getLeafSubstructure(new PdbLeafIdentifier("2n5e", 5, "A", 64));
        if (!leafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = leafSubstructure.get().getIdentifier();
        assertEquals(5, identifier.getModelIdentifier());
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(64, identifier.getSerial());
    }

    @Test
    void removeLeafSubstructure() {
        LeafIdentifier leafIdentifier = new PdbLeafIdentifier("1BRR", 1, "A", 176);
        Optional<PdbLeafSubstructure> leafSubstructureOptional = structureToModify.getLeafSubstructure(leafIdentifier);
        leafSubstructureOptional.ifPresent(leafSubstructure -> {
            structureToModify.removeLeafSubstructure(leafIdentifier);
            Optional<PdbLeafSubstructure> removedOptional = structureToModify.getLeafSubstructure(leafIdentifier);
            removedOptional.ifPresent(leafSubstructureRemoved -> fail("The leaf should have been removed and therefore the optional should be empty."));
        });
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
        // recognizes modified nucleotides as nucleotides nevertheless
        assertEquals(77, nucleotides.size());
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
        assertEquals(517, ligands.size());
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
        final Optional<PdbAtom> atom = structure1c0a.getAtom(15);
        if (!atom.isPresent()) {
            fail("Optional atom was empty.");
        }
        assertEquals("C8", atom.get().getAtomName());
        assertEquals(new Vector3D(46.506, 18.078, -5.649), atom.get().getPosition());
    }

    @Test
    void getUniqueAtomEntry() {
        final Optional<Map.Entry<UniqueAtomIdentifier, PdbAtom>> atom = structure1c0a.getUniqueAtomEntry(15);
        if (!atom.isPresent()) {
            fail("Optional atom was empty.");
        }
        UniqueAtomIdentifier identifier = atom.get().getKey();
        assertEquals("1c0a", identifier.getLeafIdentifier().getStructureIdentifier());
        assertEquals(1, identifier.getLeafIdentifier().getModelIdentifier());
        assertEquals("B", identifier.getLeafIdentifier().getChainIdentifier());
        assertEquals(601, identifier.getLeafIdentifier().getSerial());
    }

    @Test
    void addAtom() {
        final int expected = structureToModify.getAllAtoms().size() + 1;
        structureToModify.addAtom("A", "ADD", new Vector3D(1.0, 2.0, 3.0));
        final int actual = structureToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

    @Test
    void removeAtom() {
        final int expected = structureToModify.getAllAtoms().size() - 1;
        structureToModify.removeAtom(17);
        final int actual = structureToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

    @Test
    void getLastAddedAtomIdentifier() {
        final int lastAddedAtomIdentifier = structure1c0a.getLastAddedAtomIdentifier();
        assertEquals(6822, lastAddedAtomIdentifier);
    }

    @Test
    void getCopy() {
        final Structure structure2n5eCopy = structure2n5e.getCopy();
        assertNotSame(structure2n5e, structure2n5eCopy);
        assertEquals(structure2n5e, structure2n5eCopy);
    }

}