package de.bioforscher.chemistry.descriptive.molecules;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.estimations.MoleculePathFinder;
import de.bioforscher.chemistry.parser.smiles.SmilesParser;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.mathematics.vectors.Vectors;


import java.util.*;
import java.util.function.Predicate;

import static de.bioforscher.chemistry.descriptive.elements.ElementProvider.*;

/**
 * Created by Christoph on 21/11/2016.
 */
public class MoleculeGraph extends AbstractGraph<MoleculeAtom, MoleculeBond, Vector2D> {

    // TODO add a reference of this to Species

    public static Predicate<MoleculeAtom> isElement(Element element) {
        return atom -> atom.getElement().getProtonNumber() == element.getProtonNumber();
    }

    public static Predicate<MoleculeAtom> isOneOfElements(final Set<Element> elements) {
        return atom -> elements.contains(atom.getElement());
    }

    public int addNextAtom(char elementSymbol) {
        return addNextAtom(String.valueOf(elementSymbol));
    }

    public int addNextAtom(String elementSymbol) {
        return addNextAtom(getElementBySymbol(elementSymbol).orElseThrow(() -> new IllegalArgumentException("The symbol " + elementSymbol + " represents no valid element.")));
    }

    public int addNextAtom(Element element) {
        MoleculeAtom atom = new MoleculeAtom(nextNodeIdentifier(),
                Vectors.generateRandomVectorInRectangle(new Rectangle(100, 100)), element);
        addNode(atom);
        return atom.getIdentifier();
    }

    public int addNextAtom(Element element, int charge) {
        Element ion = element.asIon(charge);
        return addNextAtom(ion);
    }

    public int addNextAtom(Element element, int charge, int numberOfNeutrons) {
        Element ion = element.asIon(charge);
        ion.asIsotope(numberOfNeutrons);
        return addNextAtom(ion);
    }

    @Override
    public int addEdgeBetween(int identifier, MoleculeAtom source, MoleculeAtom target) {
        return addEdgeBetween(new MoleculeBond(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(MoleculeAtom source, MoleculeAtom target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    public int addEdgeBetween(MoleculeAtom source, MoleculeAtom target, MoleculeBondType bondType) {
        MoleculeBond bond = new MoleculeBond(nextEdgeIdentifier());
        bond.setType(bondType);
        return addEdgeBetween(bond, source, target);
    }

    public MoleculeBond getEdgeBetween(MoleculeAtom source, MoleculeAtom target) {
        return this.getEdges().stream()
                .filter(bond -> bond.containsNode(source) && bond.containsNode(target))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find any edge connecting "+source+" and "+target+"."));
    }

    public int countAtomsOfElement(Element element) {
        return (int) this.getNodes().stream()
                .filter(isElement(element))
                .count();
    }

    public List<LinkedList<MoleculeAtom>> findPathOfElements(LinkedList<Element> path) {
        return MoleculePathFinder.findPathInMolecule(this, path);
    }

    public List<LinkedList<MoleculeAtom>> findMultiPathOfElements(LinkedList<Set<Element>> path) {
        return MoleculePathFinder.findMultiPathInMolecule(this, path);
    }

    public static void main(String[] args) {

        String smilesString = "Nc1ncnc2n(cnc12)[C@@H]1O[C@H](COP(O)(=O)OP(O)(=O)OP(O)(O)=O)[C@@H](O)[C@H]1O";

        // path
        HashSet<Element> elements = new HashSet<>(Arrays.asList(NITROGEN, OXYGEN));
        LinkedList<Set<Element>> path = new LinkedList<>();
        path.add(new HashSet<>(Arrays.asList(CARBON)));
        path.add(elements);
        System.out.println(path);

        MoleculeGraph moleculeGraph = SmilesParser.parse(smilesString);
        List<LinkedList<MoleculeAtom>> pathOfElements = moleculeGraph.findMultiPathOfElements(path);

        System.out.println(pathOfElements);

    }


}
