package de.bioforscher.singa.structure.parser.pdb.structures;


import de.bioforscher.singa.structure.model.interfaces.*;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author fk
 */
public class StructureSelectorTest {


    @Test
    public void shouldSelectModelFromStructure() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("4CHA")
                .parse();
        Model structuralModel = StructureSelector.selectFrom(structure)
                .model(1)
                .selectModel();
        assertNotNull(structuralModel);
    }

    @Test
    public void shouldSelectChainFromStructure() {
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
    public void shouldSelectAminoAcidFromStructure() {
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
    public void shouldSelectNucleotideFromStructure() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1C0A")
                .parse();
        Nucleotide nucleotide = StructureSelector.selectFrom(structure)
                .model(1)
                .chain("B")
                .nucleotide(643)
                .selectNucleotide();
        assertNotNull(nucleotide);
    }

    @Test
    public void shouldSelectAtomFromStructure() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1C0A")
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