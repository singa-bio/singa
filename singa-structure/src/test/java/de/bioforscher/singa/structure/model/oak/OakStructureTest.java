package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.identifiers.UniqueAtomIdentifer;
import de.bioforscher.singa.structure.model.interfaces.*;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class OakStructureTest {

    private static OakStructure structure2N5E;
    private static OakStructure structure1C0A;
    private static OakStructure structureToModify;


    @BeforeClass
    public static void prepareData() {
        structure2N5E = (OakStructure) StructureParser.pdb().pdbIdentifier("2N5E").parse();
        structure1C0A = (OakStructure) StructureParser.pdb().pdbIdentifier("1C0A").parse();
        structureToModify = (OakStructure) StructureParser.pdb().pdbIdentifier("1BRR").parse();
    }

    @Test
    public void getPdbIdentifier() {
        String actual = structure2N5E.getPdbIdentifier();
        assertEquals("2n5e", actual);
    }

    @Test
    public void setPdbIdentifier() {
        structureToModify.setPdbIdentifier("5ING");
        String actual = structureToModify.getPdbIdentifier();
        assertEquals("5ing", actual);
    }

    @Test
    public void getTitle() {
        String actual = structure2N5E.getTitle();
        assertEquals("THE 3D SOLUTION STRUCTURE OF DISCOIDAL HIGH-DENSITY LIPOPROTEIN PARTICLES", actual);
    }

    @Test
    public void setTitle() {
        structureToModify.setTitle("Test Title");
        String actual = structureToModify.getTitle();
        assertEquals("Test Title", actual);
    }

    @Test
    public void getAllModels() {
        List<Model> allModels = structure2N5E.getAllModels();
        assertEquals(10, allModels.size());
    }

    @Test
    public void getFirstModel() {
        Model model = structure2N5E.getFirstModel();
        assertEquals(1, (int) model.getModelIdentifier());
    }

    @Test
    public void getModel() {
        Optional<Model> model = structure2N5E.getModel(2);
        if (!model.isPresent()) {
            fail("Optional model was empty.");
        }
        assertEquals(2, (int) model.get().getModelIdentifier());
    }

    @Test
    public void addModel() {
        structureToModify.addModel(new OakModel(2));
        int actualSize = structureToModify.getAllModels().size();
        assertEquals(2, actualSize);
    }

    @Test
    public void getAllChains() {
        List<Chain> allChains = structure2N5E.getAllChains();
        assertEquals(20, allChains.size());
    }

    @Test
    public void getFirstChain() {
        Chain firstChain = structure2N5E.getFirstChain();
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    public void getChain() {
        Optional<Chain> chain = structure2N5E.getChain(1, "B");
        if (!chain.isPresent()) {
            fail("Optional chain was empty.");
        }
        assertEquals("B", chain.get().getChainIdentifier());
    }

    @Test
    public void getAllLeafSubstructures() {
        List<LeafSubstructure<?>> leafSubstructures = structure2N5E.getAllLeafSubstructures();
        assertEquals(3340, leafSubstructures.size());
    }

    @Test
    public void getLeafSubstructure() {
        Optional<LeafSubstructure<?>> leafSubstructure = structure2N5E.getLeafSubstructure(new LeafIdentifier("2N5E", 5, "A", 64));
        if (!leafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = leafSubstructure.get().getIdentifier();
        assertEquals(5, identifier.getModelIdentifier());
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(64, identifier.getSerial());
    }

    @Test
    public void removeLeafSubstructure() {
        LeafIdentifier leafIdentifier = new LeafIdentifier("1BRR", 1, "A", 176);
        Optional<LeafSubstructure<?>> leafSubstructure = structureToModify.getLeafSubstructure(leafIdentifier);
        if (leafSubstructure.isPresent()) {
            structureToModify.removeLeafSubstructure(leafIdentifier);
            Optional<LeafSubstructure<?>> removed = structureToModify.getLeafSubstructure(leafIdentifier);
            if (removed.isPresent()) {
                fail("The leaf should have been removed and therefore the optional should be empty.");
            }
        }
    }

    @Test
    public void getAllAminoAcids() {
        final List<AminoAcid> aminoAcids = structure1C0A.getAllAminoAcids();
        assertEquals(585, aminoAcids.size());
    }

    @Test
    public void getAminoAcid() {
        final Optional<AminoAcid> aminoAcid = structure1C0A.getAminoAcid(new LeafIdentifier("1c0a", 1, "A", 98));
        if (!aminoAcid.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = aminoAcid.get().getIdentifier();
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(98, identifier.getSerial());
        assertEquals("Ser", aminoAcid.get().getThreeLetterCode());
    }

    @Test
    public void getAllNucleotides() {
        final List<Nucleotide> nucleotides = structure1C0A.getAllNucleotides();
        // recognizes modified nucleotides as nucleotides nevertheless
        assertEquals(77, nucleotides.size());
    }

    @Test
    public void getNucleotide() {
        final Optional<Nucleotide> nucleotide = structure1C0A.getNucleotide(new LeafIdentifier("1c0a", 1, "B", 617));
        if (!nucleotide.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = nucleotide.get().getIdentifier();
        assertEquals("B", identifier.getChainIdentifier());
        assertEquals(617, identifier.getSerial());
        assertEquals("C", nucleotide.get().getThreeLetterCode());
    }

    @Test
    public void getAllLigands() {
        final List<Ligand> ligands = structure1C0A.getAllLigands();
        assertEquals(517, ligands.size());
    }

    @Test
    public void getLigand() {
        final Optional<Ligand> nucleotide = structure1C0A.getLigand(new LeafIdentifier("1c0a", 1, "A", 831));
        if (!nucleotide.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = nucleotide.get().getIdentifier();
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(831, identifier.getSerial());
        assertEquals("AMO", nucleotide.get().getThreeLetterCode());
    }

    @Test
    public void getAllAtoms() {
        final List<Atom> atoms = structure1C0A.getAllAtoms();
        assertEquals(6820, atoms.size());
    }

    @Test
    public void getAtom() {
        final Optional<Atom> atom = structure1C0A.getAtom(15);
        if (!atom.isPresent()) {
            fail("Optional atom was empty.");
        }
        assertEquals("C8", atom.get().getAtomName());
        assertEquals(new Vector3D(46.506, 18.078, -5.649), atom.get().getPosition());
    }

    @Test
    public void getUniqueAtomEntry() {
        final Optional<Map.Entry<UniqueAtomIdentifer, Atom>> atom = structure1C0A.getUniqueAtomEntry(15);
        if (!atom.isPresent()) {
            fail("Optional atom was empty.");
        }
        UniqueAtomIdentifer identifier = atom.get().getKey();
        assertEquals("1C0A", identifier.getPdbIdentifier());
        assertEquals(1, identifier.getModelIdentifier());
        assertEquals("B", identifier.getChainIdentifier());
        assertEquals(601, identifier.getLeafSerial());
    }

    @Test
    public void addAtom() {
        final int expected = structureToModify.getAllAtoms().size() + 1;
        structureToModify.addAtom("A", "ADD", new Vector3D(1.0, 2.0, 3.0));
        final int actual = structureToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

    @Test
    public void removeAtom() {
        final int expected = structureToModify.getAllAtoms().size() - 1;
        structureToModify.removeAtom(17);
        final int actual = structureToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

    @Test
    public void getLastAddedAtomIdentifier() {
        final int lastAddedAtomIdentifier = structure1C0A.getLastAddedAtomIdentifier();
        assertEquals(6822, lastAddedAtomIdentifier);
    }

    @Test
    public void getCopy() {
        final Structure structure2N5ECopy = structure2N5E.getCopy();
        assertTrue(structure2N5E != structure2N5ECopy);
        assertTrue(structure2N5E.equals(structure2N5ECopy));
    }

}