package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParserOptions;
import de.bioforscher.singa.chemistry.parser.plip.InteractionContainer;
import de.bioforscher.singa.chemistry.parser.plip.PlipParser;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import javafx.application.Application;

import java.io.IOException;
import java.io.InputStream;

import static de.bioforscher.singa.core.utility.Resources.getResourceAsStream;

/**
 * @author fk
 */
public class StructureViewerPlayground {
    public static void main(String[] args) throws IOException {

        InputStream inputStream = getResourceAsStream("1c0a.xml");
        InteractionContainer interactions = PlipParser.parse("1c0a", inputStream);

        StructureParserOptions options = new StructureParserOptions();
        options.omitHydrogens(true);

        Structure structure = StructureParser.online()
                .pdbIdentifier("1C0A")
                .everything()
                .setOptions(options)
                .parse();

        interactions.mapToPseudoAtoms(structure);

        StructureViewer.structure = structure;
        StructureViewer.colorScheme = ColorScheme.BY_ELEMENT;

        Application.launch(StructureViewer.class);
    }
}
