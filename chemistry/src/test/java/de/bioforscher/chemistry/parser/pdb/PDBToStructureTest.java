package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.physical.Structure;
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
        // PDBToStructure.parseAminoAcidAtoms(atomLines);
        Structure structure = PDBToStructure.parseResidues(atomLines);

        System.out.println("Residues and atoms");
        structure.getResidues().forEach((integer, residue) -> {
                    System.out.println(residue);
                    residue.getNodes().forEach(atom -> System.out.println(" "+atom));
                }
        );
        System.out.println();
        structure.connectBackbone();
        System.out.println("Connected backbone elements");
        structure.getEdges().forEach(System.out::println);

    }



}