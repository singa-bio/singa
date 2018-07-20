package bio.singa.javafx.viewer;

import bio.singa.structure.model.oak.OakStructure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.StructureParserOptions;
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
