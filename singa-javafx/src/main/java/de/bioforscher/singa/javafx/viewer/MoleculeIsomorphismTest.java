package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.javafx.renderer.graphs.GraphDisplayApplication;
import de.bioforscher.singa.javafx.renderer.molecules.MoleculeGraphRenderer;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.model.molecules.MoleculeGraphs;
import de.bioforscher.singa.structure.model.oak.OakLeafSubstructure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import javafx.application.Application;

import java.util.Optional;

/**
 * @author fk
 */
public class MoleculeIsomorphismTest {

    public static void main(String[] args) {

//        Species alanylAMP = ChEBIParserService.parse("CHEBI:139296");
//        MoleculeGraph targetGraph = SmilesParser.parse(alanylAMP.getFeature(Smiles.class).getFeatureContent());
//
//        Species alanine = ChEBIParserService.parse("CHEBI:15570");
//        MoleculeGraph patternGraph= SmilesParser.parse(alanine.getFeature(Smiles.class).getFeatureContent());
//
//        RISubGraphFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph, Element, MoleculeBondType> finder
//                = new RISubGraphFinder<>(patternGraph, targetGraph, MoleculeAtom::getElement, MoleculeBond::getType);
//
//        GraphDisplayApplication.renderer = new MoleculeGraphRenderer();
//        GraphDisplayApplication.graph = patternGraph;
//
//        GraphDisplayApplication.renderer.setRenderAfter(graph -> {
//            for (MoleculeAtom moleculeAtom : targetGraph.getNodes()) {
//                if (finder.getFullMatches().get(0).contains(moleculeAtom)) {
//                    GraphDisplayApplication.renderer.getGraphicsContext().setStroke(Color.DARKRED);
//                    GraphDisplayApplication.renderer.circlePoint(moleculeAtom.getPosition(), 35);
//                }
//            }
//            return null;
//        });

        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1C0A")
                .parse();

        System.out.println();

        Optional<LeafSubstructure<?>> histidine = structure.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "A", 13));
        Optional<LeafSubstructure<?>> amp = structure.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "A", 800));
        Optional<LeafSubstructure<?>> guanine = structure.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "B", 601));

        GraphDisplayApplication.renderer = new MoleculeGraphRenderer();
        GraphDisplayApplication.graph = MoleculeGraphs.createMoleculeGraphFromStructure((OakLeafSubstructure<?>) guanine.get());

        Application.launch(GraphDisplayApplication.class);


    }


}
