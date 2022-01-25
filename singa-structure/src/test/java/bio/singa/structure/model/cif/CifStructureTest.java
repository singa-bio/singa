package bio.singa.structure.model.cif;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.model.interfaces.*;
import bio.singa.structure.model.mmtf.MmtfStructure;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class CifStructureTest {

    private static Structure structure2n5e;
    private static Structure structure1c0a;
    private static CifStructure structure7l7y;

    @BeforeAll
    static void initialize() {
        structure2n5e = StructureParser.cif()
                .pdbIdentifier("2n5e")
                .parse();
        structure1c0a = StructureParser.cif()
                .pdbIdentifier("1c0a")
                .parse();
        // only available as cif
        structure7l7y = ((CifStructure) StructureParser.cif()
                .pdbIdentifier("7l7y")
                .parse());
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
        assertTrue("2n5e".equalsIgnoreCase(actual));
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
        Optional<? extends LeafSubstructure> leafSubstructure = structure2n5e.getLeafSubstructure(new CifLeafIdentifier("2n5e", 5, "A", 10));
        if (!leafSubstructure.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = leafSubstructure.get().getIdentifier();
        assertEquals(5, identifier.getModelIdentifier());
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(10, identifier.getSerial());
    }

    @Test
    void getAllAminoAcids() {
        final List<AminoAcid> aminoAcids = structure1c0a.getAllAminoAcids();
        assertEquals(585, aminoAcids.size());
    }

    @Test
    void getAminoAcid() {
        final Optional<AminoAcid> aminoAcid = structure1c0a.getAminoAcid(new CifLeafIdentifier("1c0a", 1, "B", 58));
        if (!aminoAcid.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = aminoAcid.get().getIdentifier();
        assertEquals("B", identifier.getChainIdentifier());
        assertEquals(58, identifier.getSerial());
        assertEquals("LYS", aminoAcid.get().getThreeLetterCode());
    }

    @Test
    void getAllNucleotides() {
        final List<Nucleotide> nucleotides = structure1c0a.getAllNucleotides();
        assertEquals(77, nucleotides.size());
    }

    @Test
    void getNucleotide() {
        final Optional<Nucleotide> nucleotide = structure1c0a.getNucleotide(new CifLeafIdentifier("1c0a", 1, "A", 10));
        if (!nucleotide.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = nucleotide.get().getIdentifier();
        assertEquals("A", identifier.getChainIdentifier());
        assertEquals(10, identifier.getSerial());
        assertEquals("G", nucleotide.get().getThreeLetterCode());
    }

    @Test
    void getAllLigands() {
        final List<Ligand> ligands = structure7l7y.getAllLigands();
        assertEquals(5, ligands.size());
    }

    @Test
    void getNonPolymerEntities() {
        Collection<CifChain> nonPolymerEntities = structure7l7y.getAllEntitiesFrom(structure7l7y.getFirstModel(), CifEntityType.NON_POLYMER);
        assertEquals(5, nonPolymerEntities.size());
        Optional<CifLeafSubstructure> udp = nonPolymerEntities.stream()
                .flatMap(entity -> entity.getAllLeafSubstructures().stream())
                .filter(leaf -> leaf.getThreeLetterCode().equals("UDP"))
                .findAny();
        assertTrue(udp.isPresent());
    }

    @Test
    void useCorrectConformer() {
        Optional<CifLeafSubstructure> udpOptional = structure7l7y.getAllEntitiesFrom(structure7l7y.getFirstModel(), CifEntityType.NON_POLYMER).stream()
                .flatMap(entity -> entity.getAllLeafSubstructures().stream())
                .filter(leaf -> leaf.getThreeLetterCode().equals("UDP"))
                .findAny();
        if (!udpOptional.isPresent()) {
            fail("unable to find leaf with required three letter code");
        }
        CifLeafSubstructure udp = udpOptional.get();
        Optional<CifConformation> conformationA = udp.getConformation("A");
        assertTrue(conformationA.isPresent());
        Optional<CifConformation> conformationB = udp.getConformation("B");
        assertTrue(conformationB.isPresent());
        assertEquals(conformationA.get().getAllAtoms().size(), 25);
        assertEquals(conformationB.get().getAllAtoms().size(), 25);

    }

    @Test
    void getLigand() {
        final Optional<Ligand> nucleotide = structure1c0a.getLigand(new CifLeafIdentifier("1c0a", 1, "D", 0));
        if (!nucleotide.isPresent()) {
            fail("Optional leaf substructure was empty.");
        }
        final LeafIdentifier identifier = nucleotide.get().getIdentifier();
        assertEquals("D", identifier.getChainIdentifier());
        assertEquals(0, identifier.getSerial());
        assertEquals("AMP", nucleotide.get().getThreeLetterCode());
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
        assertEquals(new Vector3D(46.506, 18.078, -5.649), atom.get().getPosition());
    }

    @Test
    @DisplayName("cif parsing - correctly parse assemblies")
    void shouldParseAssemblies() {
        // we want connections but cannot guarantee unique atom names
        Structure structure = StructureParser.cif()
                .pdbIdentifier("6dm8")
                .parse();
        Map<String, List<String>> assemblies = ((CifStructure) structure).getBiologicalAssemblies();
        assertEquals(8, assemblies.size());
    }

    @Test
    void parseMutations() {
        CifStructure structure6p6w = ((CifStructure) StructureParser.cif()
                .pdbIdentifier("6p6w")
                .parse());
        assertTrue(structure6p6w.isMutated());
        assertFalse(((CifStructure) structure1c0a).isMutated());
    }

    @Test
    void parseSimpleSubstructreAsModification() {
        String structureId = "5l9d";
        Structure structure = StructureParser.cif()
                .pdbIdentifier(structureId)
                .parse();
        // ASN B-26 connected to NAG K-0
        CifLeafIdentifier leafIdentifier = LeafIdentifier.cif()
                .model(1)
                .chain("B")
                .serial(26);
        Optional<? extends LeafSubstructure> optionalLeaf = structure.getLeafSubstructure(leafIdentifier);
        if (!optionalLeaf.isPresent()) {
            fail("unable to get leaf with leaf identifier");
        }
        CifAminoAcid leaf = ((CifAminoAcid) optionalLeaf.get());
        // check modification was assigned
        String modificationChain = leaf.getModifications().get("N-Glycosylation");
        assertNotNull(modificationChain);
        assertEquals("K", modificationChain);
        // check connected atoms
        Pair<String> connectedAtoms = leaf.getConnectedLeafs().keySet().iterator().next();
        assertEquals("ND2", connectedAtoms.getFirst());
        assertEquals("C1", connectedAtoms.getSecond());
    }


    @Test
    void parseComplexSubstructresAsModification() {
        // branched sugars ASN A-30
        // branched sugars ASN A-47
        // branched sugars ASN A-119
        String structureId1 = "1l8j";
        Structure structure1 = StructureParser.cif()
                .pdbIdentifier(structureId1)
                .parse();
        // mixture of simple and complex modifications: 6rus
        // branched oligosacchride as PTM
        // simple sugars TRP A-56, 59
        // branched sugars THR A-65, 124, 82
        // simple sugars TPR A-112, 115, 118
        String structureId2 = "6rus";
        Structure structure2 = StructureParser.cif()
                .pdbIdentifier(structureId2)
                .parse();
        // sars cov spike: 6xr8
        String structureId3 = "6xr8";
        Structure structure3 = StructureParser.cif()
                .pdbIdentifier(structureId3)
                .parse();
    }

    @Test
    void parseMaltose() {
        String structureId1 = "1anf";
        Structure structure1 = StructureParser.cif()
                .pdbIdentifier(structureId1)
                .parse();
        CifStructure cifStructure = (CifStructure) structure1;
        Optional<CifEntity> entity = cifStructure.getEntity(2);
        System.out.println(entity.get().getName());
    }


}