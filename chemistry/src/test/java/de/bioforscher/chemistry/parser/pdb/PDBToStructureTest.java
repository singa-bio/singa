package de.bioforscher.chemistry.parser.pdb;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by fkaiser on 27.07.16.
 */
public class PDBToStructureTest {
    @Test
    public void parseAminoAcidAtoms() throws Exception {

        List<String> atomLines = Files.readAllLines(
                new File(Thread.currentThread().getContextClassLoader().getResource("pdb_atoms.txt").getFile())
                        .toPath());
        PDBToStructure.parseAminoAcidAtoms(atomLines);
    }
}