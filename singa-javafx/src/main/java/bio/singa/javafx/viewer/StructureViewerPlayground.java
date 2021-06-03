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

        OakStructure structure = (OakStructure) StructureParser.local()
                .fileLocation("/sata/customers/internal/2019_ZIM-VIDA/molecular_dynamics/nfc/peptide_structures/P03_1P03-1-B-1_query.pdb")
                .settings(StructureParserOptions.Setting.ENFORCE_CONNECTIONS)
                .everything()
                .parse();

        StructureViewer.structure = structure;
        StructureViewer.colorScheme = ColorScheme.BY_CHAIN;

        Application.launch(StructureViewer.class);
    }
}
