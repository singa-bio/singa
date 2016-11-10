package de.bioforscher.chemistry.physical;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class StructureAssemblyTest {

    // "normal" structure
    @Test
    public void shouldParseUncomplicatedStructure() throws IOException {
        Structure structure = PDBParserService.parseProteinById("4HHB");
        int actualChains = structure.getAllChains().size();
        int actualAtoms = structure.getAllAtoms().size();
        assertEquals(4, actualChains);
        assertEquals(4387, actualAtoms);
    }

    // structure with models
    @Test
    public void shouldParseStructureWithModels() {

    }

    // structure with atoms that have multiple positions
    @Test
    public void shouldParseStructureWithUncertainAtoms() {

    }

    // structure with atoms that have undefined names
    @Test
    public void shouldParseAtomsWithUnconventionalAtomNames() {

    }

    // structure with unconventional residues
    @Test
    public void shouldParseResiduesWithUnconventionalResidues() {

    }

    // structure with ligands
    @Test
    public void shouldParseStructureWithLigands() {

    }

    // structure with dna or rna
    @Test
    public void shouldParseStructureWithNucleotides() {

    }

}