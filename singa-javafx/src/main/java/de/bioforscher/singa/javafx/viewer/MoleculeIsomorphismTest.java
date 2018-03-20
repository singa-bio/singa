package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.SmilesParser;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeAtom;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeBond;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeGraph;
import de.bioforscher.singa.javafx.renderer.graphs.GraphDisplayApplication;
import de.bioforscher.singa.javafx.renderer.molecules.MoleculeGraphRenderer;
import de.bioforscher.singa.mathematics.algorithms.graphs.isomorphism.RISubGraphFinder;
import de.bioforscher.singa.mathematics.graphs.model.DirectedGraph;
import de.bioforscher.singa.mathematics.graphs.model.GenericNode;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.structure.elements.Element;
import javafx.application.Application;
import javafx.scene.paint.Color;

/**
 * @author fk
 */
public class MoleculeIsomorphismTest {

    public static void main(String[] args) {


        Species amp1 = ChEBIParserService.parse("CHEBI:16027");
        System.out.println(amp1.getFeature(Smiles.class).getFeatureContent());
        Species amp2 = ChEBIParserService.parse("CHEBI:16027");
        MoleculeGraph amp1Graph = SmilesParser.parse(amp1.getFeature(Smiles.class).getFeatureContent());
//        amp1Graph.replaceAromaticsWithDoubleBonds();
//        MoleculeGraph amp2Graph = SmilesParser.parse(amp2.getFeature(Smiles.class).getFeatureContent());
//        amp2Graph.replaceAromaticsWithDoubleBonds();


//        MoleculeGraph cyclohexan = SmilesParser.parse("C1CCCC=C1");
        MoleculeGraph pyrimidine = SmilesParser.parse("Nc1ncnc2n(cnc12)[C@@H][C@@H](O)[C@H]1O");
//        MoleculeGraph methylBromocyclohexan = SmilesParser.parse("CC1CC(Br)=CCC1");

        RISubGraphFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph, Element, Boolean> finder
                = new RISubGraphFinder<>(pyrimidine, amp1Graph, MoleculeAtom::getElement, bond -> true);

        System.out.println(finder.getFullMatches());

        GraphDisplayApplication.renderer = new MoleculeGraphRenderer();
        GraphDisplayApplication.graph = amp1Graph;

        DirectedGraph<GenericNode<MoleculeAtom>> searchSpace = finder.getSearchSpace();
//        GraphDisplayApplication.graph = searchSpace;

//        GraphPath<GenericNode<MoleculeAtom>, DirectedEdge<GenericNode<MoleculeAtom>>> path =
//                ShortestPathFinder.findBasedOnPredicate(searchSpace, searchSpace.getNode(25), node -> node.getIdentifier().equals(0));
//


        GraphDisplayApplication.renderer.setRenderAfter(graph -> {

            for (MoleculeAtom moleculeAtom : amp1Graph.getNodes()) {
                if (finder.getFullMatches().get(0).contains(moleculeAtom)) {
                    GraphDisplayApplication.renderer.getGraphicsContext().setStroke(Color.DARKRED);
                    GraphDisplayApplication.renderer.circlePoint(moleculeAtom.getPosition(), 35);
                }
            }

            return null;
        });

        Application.launch(GraphDisplayApplication.class);


    }

}
