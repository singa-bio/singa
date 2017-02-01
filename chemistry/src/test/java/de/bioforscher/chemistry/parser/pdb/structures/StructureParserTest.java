package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.parser.pdb.structures.StructureParser.LocalPDB;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Test;

import java.io.IOException;

import static de.bioforscher.chemistry.parser.pdb.structures.StructureSources.PDB_ONLINE;

public class StructureParserTest {

    @Test
    public void shouldParseUncomplicatedStructure() throws IOException {
        // "normal" structure
        Structure structure = StructureParser
                .from(PDB_ONLINE)
                .identifier("1BUW")
                .everything()
                .parse();
    }

    @Test
    public void shouldParseModel() throws IOException {
        // parse one model of multi model structure
        Structure structure = StructureParser
                .from(PDB_ONLINE)
                .identifier("1PQS")
                .model(2)
                .allChains()
                .parse();
    }

    @Test
    public void shouldParseChain() throws IOException {
        // parse one chain of multi chain structure
        Structure structure = StructureParser
                .from(PDB_ONLINE)
                .identifier("1BRR")
                .chain("A")
                .parse();
    }

    @Test
    public void shouldParseModelAndChain() throws IOException {
        // parse one model of multi model structure and only a specific chain
        Structure structure = StructureParser
                .from(PDB_ONLINE)
                .identifier("2N5E")
                .model(3)
                .chain("B")
                .parse();
    }

    @Test
    public void shouldParseChainOfMultiModel() throws IOException {
        // parse only a specific chain of all models in a structure
        Structure structure = StructureParser
                .from(PDB_ONLINE)
                .identifier("2N5E")
                .chain("B")
                .parse();
    }


    // structure with modified amino acids
    @Test
    public void shouldParseResiduesWithModifiedAminoAcids() throws IOException {
        Structure structure = StructureParser
                .from(PDB_ONLINE)
                .identifier("1DW9")
                .everything()
                .parse();
    }

    // structure with dna or rna
    @Test
    public void shouldParseStructureWithNucleotides() throws IOException {
        Structure structure = StructureParser
                .from(PDB_ONLINE)
                .identifier("5T3L")
                .everything()
                .parse();
    }

    // structure with modified nucleotides
    @Test
    public void shouldParseResiduesWithModifiedNucleotides() throws IOException {
        // TODO some strange bonds between 620 and 621 issue #41
        Structure structure = StructureParser
                .from(PDB_ONLINE)
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
    public void shouldParseFromLocalPDB() throws IOException {
        LocalPDB localPdb = new LocalPDB("/srv/pdb");
        Structure structure = StructureParser.from(localPdb)
                .identifier("1C0A")
                .everything()
                .parse();
    }
}