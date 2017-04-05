package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.parser.pdb.structures.StructureParser.LocalPDB;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.viewer.StructureViewer;
import javafx.application.Application;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static de.bioforscher.chemistry.parser.pdb.structures.StructureSources.PDB_ONLINE;

public class StructureParserTest {

    @Test
    public void shouldParseUncomplicatedStructure() {
        // "normal" structure
        Structure structure = StructureParser.online()
                .identifier("1BUW")
                .parse();
    }

    @Test
    public void shouldParseModel() {
        // parse one model of multi model structure
        Structure structure = StructureParser.online()
                .identifier("1PQS")
                .model(2)
                .allChains()
                .parse();
    }

    @Test
    public void shouldParseChain() {
        // parse one chain of multi chain structure
        Structure structure = StructureParser.online()
                .identifier("1BRR")
                .chain("A")
                .parse();
    }

    @Test
    public void shouldParseModelAndChain() {
        // parse one model of multi model structure and only a specific chain
        Structure structure = StructureParser.online()
                .identifier("2N5E")
                .model(3)
                .chain("B")
                .parse();
    }

    @Test
    public void shouldParseChainOfMultiModel() {
        // parse only a specific chain of all models in a structure
        Structure structure = StructureParser.online()
                .identifier("2N5E")
                .chain("B")
                .parse();
    }


    // structure with modified amino acids
    @Test
    public void shouldParseResiduesWithModifiedAminoAcids() {
        Structure structure = StructureParser.online()
                .identifier("1DW9")
                .everything()
                .parse();
    }

    // structure with dna or rna
    @Test
    public void shouldParseStructureWithNucleotides() {
        Structure structure = StructureParser.online()
                .identifier("5T3L")
                .everything()
                .parse();
    }

    // structure with modified nucleotides
    @Test
    public void shouldParseResiduesWithModifiedNucleotides() {
        // TODO some strange bonds between 620 and 621 issue #41
        Structure structure = StructureParser.online()
                .identifier("1C0A")
                .everything()
                .parse();
    }

    // structure with insertion codes
    @Test
    public void shouldParseStructureWithInsertionCodes() {
        // TODO issue #35
    }

    @Test
    public void shouldParseFromLocalPDB() {
        LocalPDB localPdb = new LocalPDB("/srv/pdb");
        Structure structure = StructureParser.local()
                .localPDB(localPdb, "1C0A")
                .parse();
    }

    @Test
    public void shouldParseMultipleStructures() {
        // all have the ligand SO4
        List<Structure> structures = StructureParser.online()
                .identifiers(Arrays.asList("5F3P", "5G5T", "5J6Q", "5MAT"))
                .parse();
    }




}