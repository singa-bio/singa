package bio.singa.chemistry.features.structure2d;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.chemistry.model.MoleculeAtom;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.chemistry.model.elements.Element;
import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.core.utility.Pair;
import bio.singa.mathematics.vectors.Vector2D;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * according to specification http://www.daylight.com/meetings/mug05/Kappler/ctfile.pdf
 *
 * @author cl
 */
public class MolParser2D {

    private boolean ignoreUnknownElements;
    private List<String> lines;

    private String headerInformation;
    private int atomCount;
    private int bondCount;

    private List<MoleculeAtom> atoms;
    private Map<Pair<Integer>, CovalentBondType> bonds;
    private List<Integer> skippedAtoms;

    public MolParser2D(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        skippedAtoms = new ArrayList<>();
        atoms = new ArrayList<>();
        bonds = new HashMap<>();
    }

    public MolParser2D(Path molFile, boolean ignoreUnknownElements) throws IOException {
        this(Files.readAllLines(molFile));
        this.ignoreUnknownElements = ignoreUnknownElements;
    }

    public static MoleculeGraph parseAsMoleculeGraph(Path molFile) throws IOException {
        List<String> lines = Files.readAllLines(molFile);
        MolParser2D parser = new MolParser2D(lines);
        parser.parseNextStructure();
        return parser.parseNextMoleculeGraph();
    }

    public static List<MoleculeGraph> parseMultiMolFile(Path multiMolFile) throws IOException {
        List<String> lines = Files.readAllLines(multiMolFile);
        MolParser2D parser = new MolParser2D(lines);
        List<MoleculeGraph> graphs = new ArrayList<>();
        while (parser.hasAnotherStructure()) {
            graphs.add(parser.parseNextMoleculeGraph());
        }
        return graphs;
    }

    private void parseCountsLine() {
        // 0.........1.........2.........3........
        // 012345678901234567890123456789012345678
        // aaabbblllfffcccsssxxxrrrpppiiimmmvvvvvv
        String line = lines.get(3);
        atomCount = Integer.parseInt(line.substring(0, 3).trim());
        bondCount = Integer.parseInt(line.substring(3, 6).trim());
    }

    private void parseAtomBlock() {
        // 0.........1.........2.........3.........4.........5.........6........
        // 012345678901234567890123456789012345678901234567890123456789012345678
        // xxxxx.xxxxyyyyy.yyyyzzzzz.zzzz aaaddcccssshhhbbbvvvHHHrrriiimmmnnneee
        for (int blockIndex = 0; blockIndex < atomCount; blockIndex++) {
            String line = lines.get(blockIndex + 4);
            // acquire data
            final double x = Double.parseDouble(line.substring(0, 10));
            final double y = Double.parseDouble(line.substring(10, 20));
            final Optional<Element> optionalElement = ElementProvider.getElementBySymbol(line.substring(31, 34).trim());
            final int charge = Integer.parseInt(line.substring(36, 39).trim());
            // create entities
            final Element element = optionalElement.orElse(ElementProvider.UNKOWN);
            if (element == ElementProvider.UNKOWN && ignoreUnknownElements) {
                skippedAtoms.add(blockIndex + 1);
                continue;
            }
            MoleculeAtom atom = new MoleculeAtom(blockIndex - skippedAtoms.size(), new Vector2D(x, y), element.asIon(charge));
            atoms.add(atom);
        }
    }

    private void parseBondBlock() {
        // 0.........1.........2
        // 012345678901234567890
        // 111222tttsssxxxrrrccc
        for (int blockIndex = 0; blockIndex < bondCount; blockIndex++) {
            String line = lines.get(blockIndex + 4 + atomCount);
            // acquire data
            final int first = Integer.parseInt(line.substring(0, 3).trim());
            final int second = Integer.parseInt(line.substring(3, 6).trim());

            if (skippedAtoms.contains(first) || skippedAtoms.contains(second)) {
                continue;
            }

            final int typeInt = Integer.parseInt(line.substring(6, 9).trim());
            CovalentBondType type;
            switch (typeInt) {
                case 2:
                case 4:
                    type = CovalentBondType.DOUBLE_BOND;
                    break;
                case 3:
                    type = CovalentBondType.TRIPLE_BOND;
                    break;
                case 1:
                default:
                    type = CovalentBondType.SINGLE_BOND;
                    break;
            }
            // compute shift of bonds
            int delta1 = (int) skippedAtoms.stream().filter(atomIndex -> atomIndex < first).count();
            int delta2 = (int) skippedAtoms.stream().filter(atomIndex -> atomIndex < second).count();
            bonds.put(new Pair<>(first - delta1, second - delta2), type);
        }
    }

    public boolean hasAnotherStructure() {
        return !lines.isEmpty();
    }

    public MoleculeGraph parseNextMoleculeGraph() {
        parseNextStructure();
        MoleculeGraph graph = new MoleculeGraph();
        // add atoms first
        for (MoleculeAtom atom : atoms) {
            // set x and y coordinates of the graph
            graph.addNode(atom);
        }
        // then add bonds
        for (Map.Entry<Pair<Integer>, CovalentBondType> entry : bonds.entrySet()) {
            // only use bonds connecting the leaf internally
            int source = entry.getKey().getFirst() - 1;
            int target = entry.getKey().getSecond() - 1;
            MoleculeAtom sourceNode = graph.getNode(source);
            MoleculeAtom targetNode = graph.getNode(target);
            if (sourceNode != null && targetNode != null) {
                graph.addEdgeBetween(sourceNode, targetNode, entry.getValue());
            }
        }
        return graph;
    }

    private void parseNextStructure() {
        if (!hasAnotherStructure()) {
            return;
        }
        clear();
        parseCountsLine();
        parseAtomBlock();
        parseBondBlock();
        removeParsedStructure();
    }

    private void clear() {
        atomCount = 0;
        bondCount = 0;
        atoms.clear();
        bonds.clear();
    }

    private void removeParsedStructure() {
        ListIterator<String> listIterator = lines.listIterator();
        while (listIterator.hasNext()) {
            String next = listIterator.next();
            listIterator.remove();
            if (next.equals("$$$$")) {
                break;
            }
        }
    }


}
