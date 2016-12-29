package de.bioforscher.chemistry.parser.pdb.structures;

import de.bioforscher.chemistry.physical.families.LeafFactory;
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

        LeafFactory.setToOmitHydrogens(true);

        // serine protease catalytic triad
        Structure structure = PDBParserService.parseProteinById("1f7u");

        // Structure motif = StructuralMotif.fromLeafs(1, structure,
        // LeafIdentifers.of("A-36", "B-67", "B-60", "B-204")).toStructure();

        StructureViewer.colorScheme = ColorScheme.BY_CHAIN;
        StructureViewer.structure = structure;
        Application.launch(StructureViewer.class);

    }

}
