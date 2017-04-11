package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import javafx.application.Application;

/**
 * @author fk
 */
public class StructureViewerPlayground {
    public static void main(String[] args) {
        StructureViewer.structure = StructureParser.online()
                .pdbIdentifier("2erm")
                .parse();
        Application.launch(StructureViewer.class);
    }
}
