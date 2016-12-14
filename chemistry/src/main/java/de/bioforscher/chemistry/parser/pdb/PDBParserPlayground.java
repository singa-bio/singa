package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.parser.pdb.tokens.AtomToken;
import de.bioforscher.chemistry.parser.pdb.tokens.PDBToken;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.viewer.StructureViewer;
import javafx.application.Application;

import java.io.IOException;
import java.util.Collection;

/**
 * @author cl
 */
public class PDBParserPlayground {

    public static void main(String[] args) throws IOException {

        // DNA: 5T3L
        // RNA: 5E54
        // aaRS with RNA: 1F7V

//        Structure structure = PDBParserService.parseProteinById("1BRR");
        Structure structure = PDBParserService.parseProteinById("1ELS", "A");
        // StructureViewer.structure = structure;
        // Application.launch(StructureViewer.class);

        structure.getAllLeafs().stream().map(LeafSubstructure::getPDBLines).flatMap(Collection::stream).forEach(System.out::println);
        // AtomToken.assemblePDBLine(structure.getAllLeafs().get(0)).forEach(System.out::println);

    }

}
