package de.bioforscher.chemistry.descriptive.molecules;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.chemistry.parser.smiles.SmilesParser;
import de.bioforscher.javafx.renderer.graphs.GraphDisplayApplication;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.mathematics.vectors.Vectors;
import javafx.application.Application;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.bioforscher.chemistry.descriptive.elements.ElementProvider.*;

/**
 * Created by Christoph on 21/11/2016.
 */
public class MoleculeGraph extends AbstractGraph<MoleculeAtom, MoleculeBond, Vector2D> {

    private static Predicate<MoleculeAtom> isElement(Element element) {
        return atom ->  atom.getElement().getProtonNumber() == element.getProtonNumber();
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

    public int countAtomsOfElement(Element element) {
        return (int) this.getNodes().stream()
                .filter(isElement(element))
                .count();
    }

    public List<LinkedList<MoleculeAtom>> findPathOfElements(LinkedList<Element> path) {

        // find starting points
        List<MoleculeAtom> startingPoints = this.getNodes().stream()
                .filter(isElement(path.getFirst()))
                .collect(Collectors.toList());

        List<LinkedList<MoleculeAtom>> candidates = new LinkedList<>();
        for (MoleculeAtom startingPoint : startingPoints) {
            LinkedList<MoleculeAtom> candidate = new LinkedList<>();
            candidate.add(startingPoint);
            candidates.add(candidate);
        }

        Iterator<Element> pathIterator = path.iterator();
        pathIterator.next();
        while (pathIterator.hasNext()) {
            Element nextElement = pathIterator.next();
            ListIterator<LinkedList<MoleculeAtom>> candidateIterator = candidates.listIterator();
            while (candidateIterator.hasNext()) {
                LinkedList<MoleculeAtom> next = candidateIterator.next();
                // vorletztes atom holen
                next.getLast();
                for (MoleculeAtom neighbour: next.getLast().getNeighbours()) {
                    // if (neighbour.getIdentifier() != candidateIterator.)
                    // TODO Start here
                }
            }

        }

        System.out.println(startingPoints);

        return null;
    }

    public static void main(String[] args) {

        String smilesString = "NC1=NC=NC2=C1N=CN2[C@@H]1O[C@H](COP(O)(=O)OP(O)(=O)OP(O)(O)=O)[C@@H](O)[C@H]1O";

        LinkedList<Element> path = new LinkedList<>(Arrays.asList(CARBON, NITROGEN));

        MoleculeGraph moleculeGraph = SmilesParser.parse(smilesString);
        moleculeGraph.findPathOfElements(path);

        // GraphDisplayApplication.renderer = new MoleculeGraphRenderer();
        // Application.launch(GraphDisplayApplication.class);

    }



}
