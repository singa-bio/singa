package bio.singa.structure.model.pdb;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.HISTIDINE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class PdbChainTest {

    private static PdbChain firstChain;
    private static PdbChain chainToModify;
    private static PdbChain anotherChain;

    @BeforeAll
    static void initialize() {
        PdbStructure structure2n5e = ((PdbStructure) StructureParser.pdb().pdbIdentifier("2n5e").parse());
        firstChain = structure2n5e.getFirstChain();
        chainToModify = structure2n5e.getFirstModel().getChain("B").get();
        anotherChain = ((PdbStructure) StructureParser.pdb().pdbIdentifier("1BRR").parse()).getFirstModel().getFirstChain();
    }

    @Test
    void getIdentifier() {
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    void getAllLeafSubstructures() {
        final Collection<PdbLeafSubstructure> leafSubstructures = firstChain.getAllLeafSubstructures();
        assertEquals(167, leafSubstructures.size());
    }

    @Test
    void getLeafSubstructure() {
        Optional<PdbLeafSubstructure> leafSubstructure = firstChain.getLeafSubstructure(new PdbLeafIdentifier("2n5e", 1, "A", 64));
        if (!leafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        PdbLeafIdentifier identifier = ((PdbLeafSubstructure) leafSubstructure.get()).getIdentifier();
        assertEquals(1, identifier.getModelIdentifier());
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(64, identifier.getSerial());
    }


    @Test
    void addLeafSubstructure() {
        final int expected = chainToModify.getNumberOfLeafSubstructures() + 1;
        chainToModify.addLeafSubstructure(new PdbAminoAcid(new PdbLeafIdentifier("2n5e", 1, "A", 244), HISTIDINE));
        final int actual = chainToModify.getNumberOfLeafSubstructures();
        assertEquals(expected, actual);
    }

    @Test
    void addLeafSubstructureToConsecutive() {
        final int expected = chainToModify.getNumberOfLeafSubstructures() + 1;
        final PdbAminoAcid newAminoAcid = new PdbAminoAcid(new PdbLeafIdentifier("2n5e", 1, "B", 244), HISTIDINE);
        chainToModify.addLeafSubstructure(newAminoAcid, true);
        final int actual = chainToModify.getNumberOfLeafSubstructures();
        assertEquals(expected, actual);
        assertTrue(chainToModify.getConsecutivePart().contains(newAminoAcid));
    }

    @Test
    void removeLeafSubstructure() {
        final int expected = chainToModify.getNumberOfLeafSubstructures() - 1;
        final boolean response = chainToModify.removeLeafSubstructure(new PdbAminoAcid(new PdbLeafIdentifier("2n5e", 1, "B", 243), HISTIDINE));
        if (!response) {
            fail("Response was false but should be true if any leaf substructure was removed.");
        }
        final int actual = chainToModify.getNumberOfLeafSubstructures();
        assertEquals(expected, actual);
    }

    @Test
    void getAtom() {
        // ATOM     17  OG1 THR A  56       5.624   2.561  -0.853  1.00  0.00           O
        final Optional<PdbAtom> atom = firstChain.getAtom(17);
        if (!atom.isPresent()) {
            fail("Optional atom was empty.");
        }
        assertEquals("OG1", atom.get().getAtomName());
        assertEquals(new Vector3D(14.055, 0.621, -3.733), atom.get().getPosition());
    }

    @Test
    void removeAtom() {
        final int expected = chainToModify.getAllAtoms().size() - 1;
        chainToModify.removeAtom(5272);
        final int actual = chainToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

    @Test
    void connectChainBackbone() {
        // should have happened at parsing
        final Optional<PdbLeafSubstructure> first = firstChain.getLeafSubstructure(new PdbLeafIdentifier("2n5e", 1, "A", 108));
        final Optional<PdbLeafSubstructure> second = firstChain.getLeafSubstructure(new PdbLeafIdentifier("2n5e", 1, "A", 109));
        if (!first.isPresent() || !second.isPresent()) {
            fail("Could not retrieve leafs to check connection");
        }
        final Optional<Atom> firstAtom = first.get().getAtomByName("C");
        final Optional<Atom> secondAtom = second.get().getAtomByName("N");
        if (!firstAtom.isPresent() || !secondAtom.isPresent()) {
            fail("Could not retrieve atoms to check connection");
        }
        final PdbAtom source = (PdbAtom) firstAtom.get();
        final PdbAtom target = (PdbAtom) secondAtom.get();
        assertTrue(source.getNeighbours().contains(target));
        assertTrue(target.getNeighbours().contains(source));
    }

    @Test
    void getConsecutivePart() {
        final int actual = firstChain.getConsecutivePart().size();
        assertEquals(167, actual);
    }

    @Test
    void getNonConsecutivePart() {
        final int actual = anotherChain.getNonConsecutivePart().size();
        assertEquals(5, actual);
    }

    @Test
    void getNextLeafIdentifier() {
        final PdbLeafIdentifier actual = anotherChain.getNextLeafIdentifier();
        assertEquals(new PdbLeafIdentifier("1brr", 1, "A", 1004), actual);
    }

    @Test
    void getCopy() {
        final PdbChain anotherChainCopy = anotherChain.getCopy();
        assertNotSame(anotherChain, anotherChainCopy);
        assertEquals(anotherChain, anotherChainCopy);
    }

}