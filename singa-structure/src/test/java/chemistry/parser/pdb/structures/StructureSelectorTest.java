package chemistry.parser.pdb.structures;


import de.bioforscher.singa.structure.model.interfaces.*;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureSelector;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author fk
 */
public class StructureSelectorTest {


    @Test
    public void shouldSelectModelFromStructure() {
        Structure structure = StructureParser.online()
                .pdbIdentifier("4CHA")
                .parse();
        Model structuralModel = StructureSelector.selectFrom(structure)
                .model(0)
                .selectModel();
        assertNotNull(structuralModel);
    }

    @Test
    public void shouldSelectChainFromStructure() {
        Structure structure = StructureParser.online()
                .pdbIdentifier("4CHA")
                .parse();
        Chain chain = StructureSelector.selectFrom(structure)
                .model(0)
                .chain("A")
                .selectChain();
        assertNotNull(chain);
    }

    @Test
    public void shouldSelectAminoAcidFromStructure() {
        Structure structure = StructureParser.online()
                .pdbIdentifier("4CHA")
                .parse();
        AminoAcid aminoAcid = StructureSelector.selectFrom(structure)
                .model(0)
                .chain("B")
                .aminoAcid(102)
                .selectAminoAcid();
        assertNotNull(aminoAcid);
    }

    @Test
    public void shouldSelectNucleotideFromStructure() {
        Structure structure = StructureParser.online()
                .pdbIdentifier("1C0A")
                .parse();
        Nucleotide nucleotide = StructureSelector.selectFrom(structure)
                .model(0)
                .chain("B")
                .nucleotide(643)
                .selectNucleotide();
        assertNotNull(nucleotide);
    }

    @Test
    public void shouldSelectAtomFromStructure() {
        Structure structure = StructureParser.online()
                .pdbIdentifier("1C0A")
                .parse();
        Atom atom = StructureSelector.selectFrom(structure)
                .model(0)
                .chain("B")
                .nucleotide(643)
                .atom(944)
                .selectAtom();
        assertNotNull(atom);
    }
}