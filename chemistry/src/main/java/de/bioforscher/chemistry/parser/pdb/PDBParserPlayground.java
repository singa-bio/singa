package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.chemistry.physical.families.LeafFactory;
import de.bioforscher.chemistry.physical.model.LeafIdentifers;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.chemistry.physical.viewer.ColorScheme;
import de.bioforscher.chemistry.physical.viewer.StructureViewer;
import javafx.application.Application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class PDBParserPlayground {

    public static void main(String[] args) throws IOException {

        // DNA: 5T3L
        // RNA: 5E54
        // aaRS with RNA: 1F7V
        // NMR: 2N3Y

        LeafFactory.setToOmitHydrogens(true);
        Structure structure = PDBParserService.parseProteinById("1F7V");

        // Structure motif = StructuralMotif.fromLeafsInStructure(1, structure,
        //        LeafIdentifers.from("A-36", "B-67", "B-60", "B-204")).toStructure();

        StructureViewer.colorScheme = ColorScheme.BY_CHAIN;
        StructureViewer.structure = structure;
        Application.launch(StructureViewer.class);

    }

}
