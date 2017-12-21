package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.structure.model.oak.OakStructure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParserOptions;
import javafx.application.Application;

/**
 * @author fk
 */
public class StructureViewerPlayground {
    public static void main(String[] args) {


        StructureParserOptions options = new StructureParserOptions();
        options.omitHydrogens(true);

        OakStructure structure = (OakStructure) StructureParser.pdb()
                .pdbIdentifier("1C0A")
                .everything()
                .setOptions(options)
                .parse();

        StructureViewer.structure = structure;
        StructureViewer.colorScheme = ColorScheme.BY_ELEMENT;

        Application.launch(StructureViewer.class);
    }
}
