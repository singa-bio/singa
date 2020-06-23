package bio.singa.structure.model.molecules;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.chemistry.model.MoleculeAtom;
import bio.singa.chemistry.model.MoleculeBond;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.chemistry.model.elements.Element;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors2D;
import bio.singa.structure.algorithms.molecules.MoleculePathFinder;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.oak.OakAtom;
import bio.singa.structure.model.oak.OakBond;
import bio.singa.structure.model.oak.OakLeafSubstructure;

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
            // set x and y coordinates of the graph
            Vector2D position;
            if (atom.getPosition() != null) {
                position = new Vector2D(atom.getPosition().getX(), atom.getPosition().getY()).multiply(100);
            } else {
                position = Vectors2D.generateRandom2DVector(defaultBoundingBox);
            }
            MoleculeAtom moleculeAtom = new MoleculeAtom(atom.getAtomIdentifier(), position, atom.getElement());
            moleculeAtom.setSpatialPosition(atom.getPosition());
            graph.addNode(moleculeAtom);
        }
        // then add bonds
        for (OakBond bond : leafSubstructure.getBonds()) {
            // only use bonds connecting the leaf internally
            OakAtom source = bond.getSource();
            OakAtom target = bond.getTarget();
            if (graph.getNode(source.getAtomIdentifier()) != null && graph.getNode(target.getAtomIdentifier()) != null) {
                graph.addEdgeBetween(bond.getIdentifier(), source.getAtomIdentifier(), target.getAtomIdentifier(), bond.getBondType());
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
                    path.get(i).setType(CovalentBondType.DOUBLE_BOND);
                } else {
                    path.get(i).setType(CovalentBondType.SINGLE_BOND);
                }
            }
        }
    }

    public static Predicate<MoleculeAtom> isElement(final Element element) {
        return atom -> atom.getElement().getProtonNumber() == element.getProtonNumber();
    }

    public static Predicate<MoleculeAtom> isOneOfElements(final Set<Element> elements) {
        return atom -> elements.contains(atom.getElement());
    }

}
