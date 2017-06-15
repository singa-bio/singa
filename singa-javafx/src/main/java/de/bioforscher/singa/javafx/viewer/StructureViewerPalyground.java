package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import javafx.application.Application;

/**
 * @author cl
 */
public class StructureViewerPalyground {

    public static void main(String[] args) {
        Structure structure = StructureParser.online().pdbIdentifier("1pqs").parse();
        StructureViewer.structure = structure;
        Application.launch(StructureViewer.class);
    }

}
