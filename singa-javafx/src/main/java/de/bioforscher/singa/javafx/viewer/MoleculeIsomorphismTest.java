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
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.structure.elements.Element;
import javafx.application.Application;
import javafx.scene.paint.Color;

/**
 * @author fk
 */
public class MoleculeIsomorphismTest {

    public static void main(String[] args) {

        Species alanylAMP = ChEBIParserService.parse("CHEBI:139296");
        MoleculeGraph targetGraph = SmilesParser.parse(alanylAMP.getFeature(Smiles.class).getFeatureContent());

        Species alanine = ChEBIParserService.parse("CHEBI:15570");
        MoleculeGraph patternGraph= SmilesParser.parse(alanine.getFeature(Smiles.class).getFeatureContent());

        RISubGraphFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph, Element, MoleculeBondType> finder
                = new RISubGraphFinder<>(patternGraph, targetGraph, MoleculeAtom::getElement, MoleculeBond::getType);

        GraphDisplayApplication.renderer = new MoleculeGraphRenderer();
        GraphDisplayApplication.graph = patternGraph;

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
