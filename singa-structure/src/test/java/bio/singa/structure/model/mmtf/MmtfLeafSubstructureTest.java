package bio.singa.structure.model.mmtf;

import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.PdbLeafIdentifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rcsb.mmtf.decoder.ReaderUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class MmtfLeafSubstructureTest {

    private static Structure structure1C0A;
    private static LeafSubstructure leaf162;
    private static LeafSubstructure leaf154;
    private static LeafSubstructure leaf620A;
    private static LeafSubstructure leafToModify;

    @BeforeAll
    static void prepareData() throws IOException {
        structure1C0A = new MmtfStructure(ReaderUtils.getByteArrayFromUrl("1C0A"));
        // ATOM   2967  N   THR A 162      44.461  51.348  -6.215  1.00 13.02           N
        // ...
        // ATOM   2973  CG2 THR A 162      44.646  50.871  -9.169  1.00 11.44           C
        leaf162 = structure1C0A.getLeafSubstructure(new PdbLeafIdentifier("1C0A", 1, "A", 162)).get();
        leaf154 = structure1C0A.getLeafSubstructure(new PdbLeafIdentifier("1C0A", 1, "A", 154)).get();
        leaf620A = structure1C0A.getLeafSubstructure(new PdbLeafIdentifier("1C0A", 1, "B", 620, 'A')).get();
        leafToModify = structure1C0A.getLeafSubstructure(new PdbLeafIdentifier("1C0A", 1, "A", 163)).get();
    }

    @Test
    void getIdentifier() {
        assertEquals(new PdbLeafIdentifier("1C0A", 1, "A", 162), leaf162.getIdentifier());
        assertEquals(new PdbLeafIdentifier("1C0A", 1, "B", 620, 'A'), leaf620A.getIdentifier());
    }

    @Test
    void getThreeLetterCode() {
        assertEquals("Thr", leaf162.getThreeLetterCode());
        assertEquals("H2U", leaf620A.getThreeLetterCode());
    }

    @Test
    void getAllAtoms() {
        final List<Atom> allAtoms = leaf162.getAllAtoms();
        assertEquals(7, allAtoms.size());
    }

    @Test
    void getAtom() {
        final int atomIdentifier = 437;
        final Optional<Atom> optionalAtom = leaf620A.getAtom(atomIdentifier);
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        // one offset to regular pdb file
        final Atom atom = optionalAtom.get();
        assertEquals(atom.getAtomIdentifier().intValue(), atomIdentifier);
        assertEquals("OP2", atom.getAtomName());
        assertEquals(new Vector3D(63.941001892089844, -2.0239999294281006, 30.308000564575195), atom.getPosition());
    }

    @Test
    void removeAtom() {
        // ATOM   3208 HH22 ARG B  83      37.797  27.994 -88.269  1.00  0.00           H
        final int atomIdentifier = 2974;
        Optional<Atom> optionalAtom = leafToModify.getAtom(atomIdentifier);
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        final Atom atom = optionalAtom.get();
        assertEquals(atom.getAtomIdentifier().intValue(), atomIdentifier);
        leafToModify.removeAtom(atomIdentifier);
        // check if it is present in the leaf
        optionalAtom = leafToModify.getAtom(atomIdentifier);
        assertTrue(!optionalAtom.isPresent());
        // check if it is present in the structure
        optionalAtom = structure1C0A.getModel(1).get().getAtom(atomIdentifier);
        assertTrue(!optionalAtom.isPresent());
    }

    @Test
    void getAtomByName() {
        // HETATM  444  C1' H2U B 620A     64.290   3.199  32.742  1.00 78.93           C
        final Optional<Atom> optionalAtom = leaf620A.getAtomByName("C1'");
        if (!optionalAtom.isPresent()) {
            fail("Optional atom was empty.");
        }
        // one offset to regular pdb file
        final Atom atom = optionalAtom.get();
        assertEquals(atom.getAtomIdentifier().intValue(), 444);
        assertEquals("C1'", atom.getAtomName());
        assertEquals(new Vector3D(64.29000091552734, 3.1989998817443848, 32.742000579833984), atom.getPosition());
    }

    @Test
    void assignSecondaryStructure() {
        MmtfSecondaryStructure secondaryStructure = ((MmtfAminoAcid) leaf154).getSecondaryStructure();
        assertEquals(MmtfSecondaryStructure.ALPHA_HELIX, secondaryStructure);
        secondaryStructure = ((MmtfAminoAcid) leaf162).getSecondaryStructure();
        assertEquals(MmtfSecondaryStructure.COIL, secondaryStructure);
        assertTrue(structure1C0A.getAllAminoAcids().stream()
                .map(MmtfAminoAcid.class::cast)
                .noneMatch(aminoAcid -> aminoAcid.getSecondaryStructure() == MmtfSecondaryStructure.UNDEFINED));
    }
}