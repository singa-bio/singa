package bio.singa.structure.io.pdb.structures;


import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureSelector;
import bio.singa.structure.model.interfaces.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author fk
 */
class StructureSelectorTest {


    @Test
    void shouldSelectModelFromStructure() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("4CHA")
                .parse();
        Model structuralModel = StructureSelector.selectFrom(structure)
                .model(1)
                .selectModel();
        assertNotNull(structuralModel);
    }

    @Test
    void shouldSelectChainFromStructure() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("4CHA")
                .parse();
        Chain chain = StructureSelector.selectFrom(structure)
                .model(1)
                .chain("A")
                .selectChain();
        assertNotNull(chain);
    }

    @Test
    void shouldSelectAminoAcidFromStructure() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("4CHA")
                .parse();
        AminoAcid aminoAcid = StructureSelector.selectFrom(structure)
                .model(1)
                .chain("B")
                .aminoAcid(102)
                .selectAminoAcid();
        assertNotNull(aminoAcid);
    }

    @Test
    void shouldSelectNucleotideFromStructure() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1c0a")
                .parse();
        Nucleotide nucleotide = StructureSelector.selectFrom(structure)
                .model(1)
                .chain("B")
                .nucleotide(643)
                .selectNucleotide();
        assertNotNull(nucleotide);
    }

    @Test
    void shouldSelectAtomFromStructure() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1c0a")
                .parse();
        Atom atom = StructureSelector.selectFrom(structure)
                .model(1)
                .chain("B")
                .nucleotide(643)
                .atom(944)
                .selectAtom();
        assertNotNull(atom);
    }
}