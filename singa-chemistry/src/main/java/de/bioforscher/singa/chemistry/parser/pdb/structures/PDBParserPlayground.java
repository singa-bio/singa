package de.bioforscher.singa.chemistry.parser.pdb.structures;

import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.model.Structure;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author cl
 */
public class PDBParserPlayground {

    public static void main(String[] args) throws IOException {

        // DNA: 5T3L
        // RNA: 5E54
        // aaRS with RNA: 1F7V
        // NMR: 2N3Y

        // LeafFactory.setToOmitHydrogens(true);

        /*
         LeafSubstructure<?,?> leaf = AminoAcidFamily.ARGININE.getPrototype();
         Structure structure = new Structure();
         StructuralModel structuralModel = new StructuralModel(0);
         Chain chainIdentifier = new Chain(1);
         chainIdentifier.setChainIdentifier("A");
         chainIdentifier.addSubstructure(leaf);
         structuralModel.addSubstructure(chainIdentifier);
         structure.addSubstructure(structuralModel);
        */

        // Structure motif = StructuralMotif.fromLeaves(1, structure,
        // LeafIdentifiers.of("A-36", "B-67", "B-60", "B-204")).toStructure();

        // they all have the same ligand
        Structure structure = StructureParser.online()
                .pdbIdentifier("2odr")
                .chainIdentifier("")
                .parse();

        Chain first = structure.getAllChains().iterator().next();

        StructureWriter.writeLeafSubstructures(new ArrayList<>(first.getConsecutivePart()), Paths.get("/home/leberech/test.pdb"));

        first.getConsecutivePart().forEach(System.out::println);
        System.out.println();
        first.getNonConsecutivePart().forEach(System.out::println);


    }

}
