package de.bioforscher.chemistry.aars;

import de.bioforscher.chemistry.parser.pdb.structures.PDBParserService;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.leafes.Nucleotide;
import de.bioforscher.chemistry.physical.model.Structure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by leberech on 19/12/16.
 */
public class T01_LigandIdentifer {

    private static String class2WithTRna = "/home/leberech/workspace/aars/class_2_with_trna.txt";

    public static void main(String[] args) throws IOException {
        List<Structure> structures = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(class2WithTRna))) {
            stream.forEach((pdbId) -> {
                try {
                    structures.add(PDBParserService.parseProteinById(pdbId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Structure structure : structures) {
            for (Chain chain : structure.getAllChains()) {
                for (Nucleotide nucleotide: chain.getNucleotides()) {
                    System.out.println(structure.getPdbID()+"\t"+chain.getName()+"\t"+nucleotide.getIdentifier()+"\t"+nucleotide.getName());
                }
            }
        }

    }


}
