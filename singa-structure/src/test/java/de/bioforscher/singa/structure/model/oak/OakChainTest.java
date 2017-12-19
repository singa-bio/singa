package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author cl
 */
public class OakChainTest {

    private static OakChain firstChain;
    private static OakChain chainToModify;
    private static OakChain anotherChain;

    @BeforeClass
    public static void prepareData() {
        Structure structure2N5E = StructureParser.online().pdbIdentifier("2N5E").parse();
        firstChain = (OakChain) structure2N5E.getFirstChain();
        chainToModify = (OakChain) structure2N5E.getFirstModel().getChain("B").get();
        anotherChain = (OakChain) StructureParser.online().pdbIdentifier("1BRR").parse().getFirstModel().getFirstChain();
    }

    @Test
    public void getIdentifier() {
        assertEquals("A", firstChain.getChainIdentifier());
    }

    @Test
    public void getAllLeafSubstructures() {
        final List<LeafSubstructure<?>> leafSubstructures = firstChain.getAllLeafSubstructures();
        assertEquals(167, leafSubstructures.size());
    }

    @Test
    public void getLeafSubstructure() {
        Optional<LeafSubstructure<?>> leafSubstructure = firstChain.getLeafSubstructure(new LeafIdentifier("2N5E", 1, "A", 64));
        if (!leafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = leafSubstructure.get().getIdentifier();
        assertEquals(1, identifier.getModelIdentifier());
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(64, identifier.getSerial());
    }


    @Test
    public void addLeafSubstructure() {
        final int expected = chainToModify.getAllLeafSubstructures().size() + 1;
        chainToModify.addLeafSubstructure(new OakAminoAcid(new LeafIdentifier("2N5E", 1, "A", 244), AminoAcidFamily.HISTIDINE));
        final int actual = chainToModify.getAllLeafSubstructures().size();
        assertEquals(expected, actual);
    }

    @Test
    public void addLeafSubstructureToConsecutive() {
        final int expected = chainToModify.getAllLeafSubstructures().size() + 1;
        final OakAminoAcid newAminoAcid = new OakAminoAcid(new LeafIdentifier("2N5E", 1, "B", 244), AminoAcidFamily.HISTIDINE);
        chainToModify.addLeafSubstructure(newAminoAcid, true);
        final int actual = chainToModify.getAllLeafSubstructures().size();
        assertEquals(expected, actual);
        assertTrue(chainToModify.getConsecutivePart().contains(newAminoAcid));
    }

    @Test
    public void removeLeafSubstructure() {
        final int expected = chainToModify.getAllLeafSubstructures().size() - 1;
        final boolean response = chainToModify.removeLeafSubstructure(new OakAminoAcid(new LeafIdentifier("2N5E", 1, "B", 243), AminoAcidFamily.HISTIDINE));
        if (!response) {
            fail("Response was false but should be true if any leaf substructure was removed.");
        }
        final int actual = chainToModify.getAllLeafSubstructures().size();
        assertEquals(expected, actual);
    }

    @Test
    public void getAtom() {
        // ATOM     17  OG1 THR A  56       5.624   2.561  -0.853  1.00  0.00           O
        final Optional<Atom> atom = firstChain.getAtom(17);
        if (!atom.isPresent()) {
            fail("Optional atom was empty.");
        }
        assertEquals("OG1", atom.get().getAtomName());
        assertEquals(new Vector3D(14.055, 0.621, -3.733), atom.get().getPosition());
    }

    @Test
    public void removeAtom() {
        final int expected = chainToModify.getAllAtoms().size() - 1;
        chainToModify.removeAtom(5272);
        final int actual = chainToModify.getAllAtoms().size();
        assertEquals(expected, actual);
    }

    @Test
    public void connectChainBackbone() {
        // should have happened at parsing
        final Optional<LeafSubstructure<?>> first = firstChain.getLeafSubstructure(new LeafIdentifier("2N5E", 1, "A", 108));
        final Optional<LeafSubstructure<?>> second = firstChain.getLeafSubstructure(new LeafIdentifier("2N5E", 1, "A", 109));
        if (!first.isPresent() || !second.isPresent()) {
            fail("Could not retrieve leafs to check connection");
        }
        final Optional<Atom> firstAtom = first.get().getAtomByName("C");
        final Optional<Atom> secondAtom = second.get().getAtomByName("N");
        if (!firstAtom.isPresent() || !secondAtom.isPresent()) {
            fail("Could not retrieve atoms to check connection");
        }
        final OakAtom source = (OakAtom) firstAtom.get();
        final OakAtom target = (OakAtom) secondAtom.get();
        assertTrue(source.getNeighbours().contains(target));
        assertTrue(target.getNeighbours().contains(source));
    }

    @Test
    public void getConsecutivePart() {
        final int actual = firstChain.getConsecutivePart().size();
        assertEquals(167, actual);
    }

    @Test
    public void getNonConsecutivePart() {
        final int actual = anotherChain.getNonConsecutivePart().size();
        assertEquals(5, actual);
    }

    @Test
    public void getNextLeafIdentifier() {
        final LeafIdentifier actual = anotherChain.getNextLeafIdentifier();
        assertEquals(new LeafIdentifier("1brr", 1, "A", 1004), actual);
    }

    @Test
    public void getCopy() {
        final OakChain anotherChainCopy = anotherChain.getCopy();
        assertTrue(anotherChain != anotherChainCopy);
        assertTrue(anotherChain.equals(anotherChainCopy));
    }

}