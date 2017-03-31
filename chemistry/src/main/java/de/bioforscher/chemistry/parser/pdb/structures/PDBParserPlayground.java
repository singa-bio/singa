package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.viewer.ColorScheme;
import de.bioforscher.chemistry.physical.viewer.StructureViewer;
import javafx.application.Application;

import java.io.IOException;

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
         Chain chain = new Chain(1);
         chain.setChainIdentifier("A");
         chain.addSubstructure(leaf);
         structuralModel.addSubstructure(chain);
         structure.addSubstructure(structuralModel);
        */

        // Structure motif = StructuralMotif.fromLeafs(1, structure,
        // LeafIdentifiers.of("A-36", "B-67", "B-60", "B-204")).toStructure();

        // they all have the same ligand
        Structure structure = StructureParser.online()
                .pdbIdentifier("1pqs")
                .parse();

        StructureViewer.colorScheme = ColorScheme.BY_ELEMENT;
        StructureViewer.structure = structure;
        Application.launch(StructureViewer.class);

    }

}
