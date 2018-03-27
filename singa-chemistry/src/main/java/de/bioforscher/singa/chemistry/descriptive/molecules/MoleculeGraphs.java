package de.bioforscher.singa.chemistry.descriptive.molecules;

import de.bioforscher.singa.chemistry.descriptive.estimations.MoleculePathFinder;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import de.bioforscher.singa.structure.elements.Element;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.oak.OakBond;
import de.bioforscher.singa.structure.model.oak.OakLeafSubstructure;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author cl
 */
public class MoleculeGraphs {

    public static Rectangle defaultBoundingBox = new Rectangle(100, 100);

    public static MoleculeGraph createMoleculeGraphFromStructure(OakLeafSubstructure<?> leafSubstructure) {
        // crate empty graph
        MoleculeGraph graph = new MoleculeGraph();
        // add atoms first
        for (Atom atom : leafSubstructure.getAllAtoms()) {
            graph.addNode(new MoleculeAtom(atom.getAtomIdentifier(), Vectors.generateRandom2DVector(defaultBoundingBox), atom.getElement()));
        }
        // then add bonds
        for (OakBond bond : leafSubstructure.getBonds()) {
            // only use bonds connecting the leaf internally
            if (graph.getNode(bond.getSource().getAtomIdentifier()) != null && graph.getNode(bond.getTarget().getAtomIdentifier()) != null) {
                graph.addEdgeBetween(bond.getIdentifier(), bond.getSource().getAtomIdentifier(), bond.getTarget().getAtomIdentifier(), bond.getBondType());
            }
        }
        return graph;
    }

    public static int countAtomsOfElement(MoleculeGraph graph, Element element) {
        return (int) graph.getNodes().stream()
                .filter(isElement(element))
                .count();
    }

    public static List<LinkedList<MoleculeAtom>> findPathOfElements(MoleculeGraph graph, LinkedList<Element> path) {
        return MoleculePathFinder.findPathInMolecule(graph, path);
    }

    public static List<LinkedList<MoleculeAtom>> findMultiPathOfElements(MoleculeGraph graph, LinkedList<Set<Element>> path) {
        return MoleculePathFinder.findMultiPathInMolecule(graph, path);
    }

    public static void replaceAromaticsWithDoubleBonds(MoleculeGraph graph) {
        // get all aromatic paths in the molecule
        List<LinkedList<MoleculeBond>> aromaticPaths = MoleculePathFinder.findAromaticPath(graph);
        // replace every second bond with a double bond
        for (List<MoleculeBond> path : aromaticPaths) {
            for (int i = 0; i < path.size(); i++) {
                if (i % 2 == 0) {
                    path.get(i).setType(MoleculeBondType.DOUBLE_BOND);
                } else {
                    path.get(i).setType(MoleculeBondType.SINGLE_BOND);
                }
            }
        }
    }

    public static Predicate<MoleculeAtom> isElement(Element element) {
        return atom -> atom.getElement().getProtonNumber() == element.getProtonNumber();
    }

    public static Predicate<MoleculeAtom> isOneOfElements(final Set<Element> elements) {
        return atom -> elements.contains(atom.getElement());
    }

}
