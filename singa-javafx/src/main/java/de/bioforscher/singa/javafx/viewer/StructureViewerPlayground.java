package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.structure.model.oak.OakStructure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParserOptions;
import javafx.application.Application;

import java.io.IOException;

/**
 * @author fk
 */
public class StructureViewerPlayground {
    public static void main(String[] args) throws IOException {


        StructureParserOptions options = new StructureParserOptions();
        options.omitHydrogens(true);

        OakStructure structure = (OakStructure) StructureParser.online()
                .pdbIdentifier("1C0A")
                .everything()
                .setOptions(options)
                .parse();

        StructureViewer.structure = structure;
        StructureViewer.colorScheme = ColorScheme.BY_ELEMENT;

        Application.launch(StructureViewer.class);
    }
}
