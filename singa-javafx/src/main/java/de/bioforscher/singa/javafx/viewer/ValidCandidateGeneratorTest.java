package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.javafx.renderer.graphs.GraphDisplayApplication;
import de.bioforscher.singa.javafx.renderer.graphs.GraphRenderer;
import de.bioforscher.singa.mathematics.graphs.model.DirectedGraph;
import de.bioforscher.singa.mathematics.graphs.model.GenericNode;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.ValidCandidateGenerator;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifiers;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import javafx.application.Application;

import java.util.List;

/**
 * @author fk
 */
public class ValidCandidateGeneratorTest {

    public static void main(String[] args) {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1a0j")
                .parse();

        StructuralMotif queryMotif = StructuralMotif.fromLeafIdentifiers(structure, LeafIdentifiers.of(
                "A-57",
                "A-102",
                "A-195"));

//        queryMotif.addExchangeableFamily(LeafIdentifier.fromSimpleString("A-57"),AminoAcidFamily.METHIONINE);
//        queryMotif.addExchangeableFamily(LeafIdentifier.fromSimpleString("A-57"),AminoAcidFamily.VALINE);
//        queryMotif.addExchangeableFamily(LeafIdentifier.fromSimpleString("A-102"),AminoAcidFamily.VALINE);


        List<LeafSubstructure<?>> environment = StructuralMotif.fromLeafIdentifiers(structure, LeafIdentifiers.of(
                "A-104",
                "A-138",
                "A-139",
                "A-140",
                "A-141",
                "A-142",
                "A-143",
                "A-177",
                "A-179",
                "A-180",
                "A-190",
                "A-191",
                "A-192",
                "A-193",
                "A-194",
                "A-196",
                "A-197",
                "A-198",
                "A-211",
                "A-212",
                "A-213",
                "A-214",
                "A-215",
                "A-216",
                "A-227",
                "A-228",
                "A-229",
                "A-230",
                "A-231",
                "A-234",
                "A-237",
                "A-248",
                "A-246",
                "A-304",
                "A-305",
                "A-306",
                "A-307",
                "A-312",
                "A-321",
                "A-323",
                "A-330",
                "A-418",
                "A-448",
                "A-454",
                "A-463",
                "A-467",
                "A-475",
                "A-633",
                "A-195",
                "A-57",
                "A-102")).getAllLeafSubstructures();

        ValidCandidateGenerator validCandidateGeneratorGraphBased = new ValidCandidateGenerator(queryMotif.getAllLeafSubstructures(), environment);
        DirectedGraph<GenericNode<LeafSubstructure<?>>> searchSpace = validCandidateGeneratorGraphBased.getSearchSpace();


        GraphDisplayApplication.renderer = new GraphRenderer();
        GraphDisplayApplication.renderer.getRenderingOptions().setDisplayingIdentifierText(false);
        GraphDisplayApplication.graph = searchSpace;

        GraphDisplayApplication.renderer.setRenderAfter(graph -> {
            for (GenericNode<LeafSubstructure<?>> node : searchSpace.getNodes()) {
                LeafSubstructure<?> content = node.getContent();
                String label;
                if (content != null) {
                    label = content.getIdentifier().toSimpleString() + "-" + content.getFamily().getThreeLetterCode();
                } else {
                    label = "root";
                }
                GraphDisplayApplication.renderer.drawTextCenteredOnPoint(label, node.getPosition());
            }
            return null;
        });

        Application.launch(GraphDisplayApplication.class);
    }
}
