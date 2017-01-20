package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.branches.StructuralModel;
import de.bioforscher.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.chemistry.physical.leafes.Nucleotide;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author fk
 */
public class StructureSelectorTest {


    @Test
    public void shouldSelectModelFromStructure() throws IOException {
        Structure structure = PDBParserService.parseProteinById("4CHA");
        StructuralModel structuralModel = StructureSelector.selectFrom(structure)
                .model(0)
                .selectModel();
        assertNotNull(structuralModel);
    }

    @Test
    public void shouldSelectChainFromStructure() throws IOException {
        Structure structure = PDBParserService.parseProteinById("4CHA");
        Chain chain = StructureSelector.selectFrom(structure)
                .model(0)
                .chain(0)
                .selectChain();
        assertNotNull(chain);
    }

    @Test
    public void shouldSelectAminoAcidFromStructure() throws IOException {
        Structure structure = PDBParserService.parseProteinById("4CHA");
        AminoAcid aminoAcid = StructureSelector.selectFrom(structure)
                .model(0)
                .chain(1)
                .aminoAcid(102)
                .selectAminoAcid();
        assertNotNull(aminoAcid);
    }

    @Test
    public void shouldSelectNucleotideFromStructure() throws IOException {
        Structure structure = PDBParserService.parseProteinById("1C0A");
        Nucleotide nucleotide = StructureSelector.selectFrom(structure)
                .model(0)
                .chain(1)
                .nucleotide(643)
                .selectNucleotide();
        assertNotNull(nucleotide);
    }

    @Test
    public void shouldSelectAtomFromStructure() throws IOException {
        Structure structure = PDBParserService.parseProteinById("1C0A");
        Atom atom = StructureSelector.selectFrom(structure)
                .model(0)
                .chain(1)
                .nucleotide(643)
                .atom(944)
                .selectAtom();
        assertNotNull(atom);
    }
}