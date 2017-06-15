package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParserOptions;
import javafx.application.Application;

/**
 * @author fk
 */
public class StructureViewerPlayground {
    public static void main(String[] args) {

        StructureParserOptions options = new StructureParserOptions();
        options.omitHydrogens(true);

        StructureViewer.structure = StructureParser.online()
                .pdbIdentifier("1pqs")
                .chainIdentifier("A")
                .setOptions(options)
                .parse();

        Application.launch(StructureViewer.class);
    }
}
