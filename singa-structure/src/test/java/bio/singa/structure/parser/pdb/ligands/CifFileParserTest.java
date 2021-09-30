package bio.singa.structure.parser.pdb.ligands;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.core.utility.Pair;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.OakBond;
import bio.singa.structure.model.oak.OakLigand;
import bio.singa.structure.model.oak.PdbLeafIdentifier;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.model.general.LeafSkeleton;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class CifFileParserTest {

    @Test
    void shouldParseSingleBondLigandSkeleton() {
        LeafSkeleton leafSkeleton = LigandParserService.parseLeafSkeleton("OH");
        Map<Pair<String>, CovalentBondType> bonds = leafSkeleton.getBonds();
        assertEquals(1, bonds.size());
    }

    @Test
    void shouldParseSingleAtomLigandSkeleton() {
        LeafSkeleton leafSkeleton = LigandParserService.parseLeafSkeleton("CL");
        Map<Pair<String>, CovalentBondType> bonds = leafSkeleton.getBonds();
        assertEquals(0, bonds.size());

        LeafSkeleton znSkeleton = LigandParserService.parseLeafSkeleton("ZN");
        Map<Pair<String>, CovalentBondType> znBonds = znSkeleton.getBonds();
        assertEquals(0, znBonds.size());
    }

    @Test
    void shouldParseSingleAtomLigandStructure() {
        OakLigand leafSubstructure = null;
        try {
            leafSubstructure = ((OakLigand) LigandParserService.parseLeafSubstructureById("MG"));
        } catch (IOException e) {
            fail("Unable tp parse ligand");
        }
        Collection<OakBond> bonds = leafSubstructure.getBonds();
        List<Atom> atoms = leafSubstructure.getAllAtoms();
        assertEquals(0, bonds.size());
        assertEquals(1, atoms.size());
    }

    @Test
    void shouldParseSugarLigandStructure() {
        OakLigand leafSubstructure = null;
        try {
            leafSubstructure = ((OakLigand) LigandParserService.parseLeafSubstructureById("LAT"));
        } catch (IOException e) {
            fail("Unable tp parse ligand");
        }
        Collection<OakBond> bonds = leafSubstructure.getBonds();
        List<Atom> atoms = leafSubstructure.getAllAtoms();
    }

    @Test
    void shouldIgnoreQuestionMark() {
        OakLigand leafSubstructure = null;
        try {
            leafSubstructure = ((OakLigand) LigandParserService.parseLeafSubstructureById("HGB"));
        } catch (IOException e) {
            fail("Unable tp parse ligand");
        }
        Collection<OakBond> bonds = leafSubstructure.getBonds();
        List<Atom> atoms = leafSubstructure.getAllAtoms();
    }

    @Test
    void shouldParseMultilineInChi() {
        // ;
        LeafSkeleton fad = LigandParserService.parseLeafSkeleton("FAD");
        assertEquals("FLAVIN-ADENINE DINUCLEOTIDE", fad.getName());
        assertEquals("InChI=1S/C27H33N9O15P2/c1-10-3-12-13(4-11(10)2)35(24-18(32-12)25(42)34-27(43)33-24)5-14(37)19(39)15(38)6-48-52(44,45)51-53(46,47)49-7-16-20(40)21(41)26(50-16)36-9-31-17-22(28)29-8-30-23(17)36/h3-4,8-9,14-16,19-21,26,37-41H,5-7H2,1-2H3,(H,44,45)(H,46,47)(H2,28,29,30)(H,34,42,43)/t14-,15+,16+,19-,20+,21+,26+/m0/s1", fad.getInchi());
        assertEquals(fad.getBonds().size(), 91);
        // "
        LeafSkeleton lop = LigandParserService.parseLeafSkeleton("LOP");
        assertEquals("(1R)-2-{[(R)-(2-AMINOETHOXY)(HYDROXY)PHOSPHORYL]OXY}-1-[(DODECANOYLOXY)METHYL]ETHYL (9Z)-OCTADEC-9-ENOATE", lop.getName());
        assertEquals("InChI=1S/C35H68NO8P/c1-3-5-7-9-11-13-14-15-16-17-18-20-22-24-26-28-35(38)44-33(32-43-45(39,40)42-30-29-36)31-41-34(37)27-25-23-21-19-12-10-8-6-4-2/h15-16,33H,3-14,17-32,36H2,1-2H3,(H,39,40)/b16-15-/t33-/m1/s1", lop.getInchi());
        // trailing ;
        LeafSkeleton mnh = LigandParserService.parseLeafSkeleton("MNH");
        assertEquals("MANGANESE PROTOPORPHYRIN IX", mnh.getName());
        assertEquals("InChI=1S/C34H34N4O4.Mn/c1-7-21-17(3)25-13-26-19(5)23(9-11-33(39)40)31(37-26)16-32-24(10-12-34(41)42)20(6)28(38-32)15-30-22(8-2)18(4)27(36-30)14-29(21)35-25;/h7-8,13-16H,1-2,9-12H2,3-6H3,(H4,35,36,37,38,39,40,41,42);/q;+6/p-2/b25-13-,26-13-,27-14-,28-15-,29-14-,30-15-,31-16-,32-16-;", mnh.getInchi());
    }

    @Test
    void shouldParseNamesCorretly() {
        LeafSkeleton fiveMu = LigandParserService.parseLeafSkeleton("5MU");
        assertEquals("5-METHYLURIDINE 5'-MONOPHOSPHATE", fiveMu.getName());
    }

    @Test
    void shouldParseSAHLigand() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("3cjt")
                .parse();
        Optional<LeafSubstructure> substructureOptional = structure.getLeafSubstructure(PdbLeafIdentifier.fromString("3cjt-1-I-259"));
        assertTrue(substructureOptional.isPresent());
        assertFalse(StructuralFamilies.Nucleotides.isNucleotide(substructureOptional.get().getFamily()));
        assertFalse(StructuralFamilies.AminoAcids.isAminoAcid(substructureOptional.get().getFamily()));
    }

}