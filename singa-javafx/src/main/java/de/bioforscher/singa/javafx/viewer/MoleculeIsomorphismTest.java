package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.SmilesParser;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeAtom;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeBond;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeBondType;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeGraph;
import de.bioforscher.singa.javafx.renderer.graphs.GraphDisplayApplication;
import de.bioforscher.singa.javafx.renderer.molecules.MoleculeGraphRenderer;
import de.bioforscher.singa.mathematics.algorithms.graphs.isomorphism.RISubGraphFinder;
import de.bioforscher.singa.mathematics.graphs.model.DirectedEdge;
import de.bioforscher.singa.mathematics.graphs.model.DirectedGraph;
import de.bioforscher.singa.mathematics.graphs.model.GenericNode;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.structure.elements.Element;
import javafx.application.Application;
import javafx.scene.paint.Color;

/**
 * @author fk
 */
public class MoleculeIsomorphismTest {

    private static DirectedGraph<GenericNode<String>> createPatternGraph() {
        DirectedGraph<GenericNode<String>> patternGraph = new DirectedGraph<>();
        GenericNode<String> patternNode0 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "0");
        patternGraph.addNode(patternNode0);
        GenericNode<String> patternNode3 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "3");
        patternGraph.addNode(patternNode3);
        GenericNode<String> patternNode4 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "4");
        patternGraph.addNode(patternNode4);
        GenericNode<String> patternNode6 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "6");
        patternGraph.addNode(patternNode6);
        GenericNode<String> patternNode7 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "7");
        patternGraph.addNode(patternNode7);


        patternGraph.addEdgeBetween(patternNode0, patternNode3);
        patternGraph.addEdgeBetween(patternNode3, patternNode0);

        patternGraph.addEdgeBetween(patternNode3, patternNode6);
        patternGraph.addEdgeBetween(patternNode6, patternNode3);

        patternGraph.addEdgeBetween(patternNode0, patternNode4);
        patternGraph.addEdgeBetween(patternNode4, patternNode0);

        patternGraph.addEdgeBetween(patternNode4, patternNode6);
        patternGraph.addEdgeBetween(patternNode6, patternNode4);

        patternGraph.addEdgeBetween(patternNode6, patternNode7);
        patternGraph.addEdgeBetween(patternNode7, patternNode6);

        return patternGraph;
    }

    private static DirectedGraph<GenericNode<String>> createTargetGraph() {
        DirectedGraph<GenericNode<String>> targetGraph = new DirectedGraph<>();
        GenericNode<String> targetNode0 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "0");
        targetGraph.addNode(targetNode0);
        GenericNode<String> targetNode1 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "1");
        targetGraph.addNode(targetNode1);
        GenericNode<String> targetNode2 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "2");
        targetGraph.addNode(targetNode2);
        GenericNode<String> targetNode3 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "3");
        targetGraph.addNode(targetNode3);
        GenericNode<String> targetNode4 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "4");
        targetGraph.addNode(targetNode4);
        GenericNode<String> targetNode5 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "5");
        targetGraph.addNode(targetNode5);
        GenericNode<String> targetNode6 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "6");
        targetGraph.addNode(targetNode6);
        GenericNode<String> targetNode7 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "7");
        targetGraph.addNode(targetNode7);
        GenericNode<String> targetNode8 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "8");
        targetGraph.addNode(targetNode8);
        GenericNode<String> targetNode9 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "9");
        targetGraph.addNode(targetNode9);
        GenericNode<String> targetNode10 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "10");
        targetGraph.addNode(targetNode10);


        targetGraph.addEdgeBetween(targetNode0, targetNode1);
        targetGraph.addEdgeBetween(targetNode1, targetNode0);

        targetGraph.addEdgeBetween(targetNode1, targetNode2);
        targetGraph.addEdgeBetween(targetNode2, targetNode1);

        targetGraph.addEdgeBetween(targetNode0, targetNode3);
        targetGraph.addEdgeBetween(targetNode3, targetNode0);

        targetGraph.addEdgeBetween(targetNode3, targetNode6);
        targetGraph.addEdgeBetween(targetNode6, targetNode3);

        targetGraph.addEdgeBetween(targetNode6, targetNode7);
        targetGraph.addEdgeBetween(targetNode7, targetNode6);

        targetGraph.addEdgeBetween(targetNode7, targetNode8);
        targetGraph.addEdgeBetween(targetNode8, targetNode7);

        targetGraph.addEdgeBetween(targetNode5, targetNode8);
        targetGraph.addEdgeBetween(targetNode8, targetNode5);

        targetGraph.addEdgeBetween(targetNode2, targetNode5);
        targetGraph.addEdgeBetween(targetNode5, targetNode2);

        targetGraph.addEdgeBetween(targetNode0, targetNode4);
        targetGraph.addEdgeBetween(targetNode4, targetNode0);

        targetGraph.addEdgeBetween(targetNode4, targetNode6);
        targetGraph.addEdgeBetween(targetNode6, targetNode4);

        targetGraph.addEdgeBetween(targetNode1, targetNode4);
        targetGraph.addEdgeBetween(targetNode4, targetNode1);

        targetGraph.addEdgeBetween(targetNode4, targetNode7);
        targetGraph.addEdgeBetween(targetNode7, targetNode4);

        targetGraph.addEdgeBetween(targetNode5, targetNode4);
        targetGraph.addEdgeBetween(targetNode4, targetNode5);

        targetGraph.addEdgeBetween(targetNode1, targetNode5);
        targetGraph.addEdgeBetween(targetNode5, targetNode1);

        targetGraph.addEdgeBetween(targetNode5, targetNode7);
        targetGraph.addEdgeBetween(targetNode7, targetNode5);

        targetGraph.addEdgeBetween(targetNode0, targetNode9);
        targetGraph.addEdgeBetween(targetNode9, targetNode0);

        targetGraph.addEdgeBetween(targetNode9, targetNode6);
        targetGraph.addEdgeBetween(targetNode6, targetNode9);

        targetGraph.addEdgeBetween(targetNode10, targetNode9);
        targetGraph.addEdgeBetween(targetNode9, targetNode10);
        return targetGraph;
    }

    public static void main(String[] args) {

        Species alanylAMP = ChEBIParserService.parse("CHEBI:139296");
        MoleculeGraph targetGraph = SmilesParser.parse(alanylAMP.getFeature(Smiles.class).getFeatureContent());

        Species alanine = ChEBIParserService.parse("CHEBI:15570");
        MoleculeGraph patternGraph= SmilesParser.parse(alanine.getFeature(Smiles.class).getFeatureContent());

        RISubGraphFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph, Element, MoleculeBondType> finder
                = new RISubGraphFinder<>(patternGraph, targetGraph, MoleculeAtom::getElement, MoleculeBond::getType);

        GraphDisplayApplication.renderer = new MoleculeGraphRenderer();
        GraphDisplayApplication.graph = targetGraph;

        GraphDisplayApplication.renderer.setRenderAfter(graph -> {
            for (MoleculeAtom moleculeAtom : targetGraph.getNodes()) {
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
