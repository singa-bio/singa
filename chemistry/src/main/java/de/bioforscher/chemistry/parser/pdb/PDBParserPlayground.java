package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.physical.model.Structure;
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

        Structure structure = PDBParserService.parseProteinById("1F7V");
        StructureViewer.structure = structure;
        Application.launch(StructureViewer.class);

    }

}
