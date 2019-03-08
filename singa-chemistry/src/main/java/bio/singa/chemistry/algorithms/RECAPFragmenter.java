package bio.singa.chemistry.algorithms;

import bio.singa.chemistry.features.smiles.SmilesParser;
import bio.singa.core.utility.Pair;
import bio.singa.mathematics.algorithms.graphs.DisconnectedSubgraphFinder;
import bio.singa.mathematics.graphs.model.DirectedGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import bio.singa.structure.algorithms.molecules.MoleculeIsomorphism;
import bio.singa.structure.algorithms.molecules.MoleculeIsomorphismFinder;
import bio.singa.structure.elements.ElementProvider;
import bio.singa.structure.model.molecules.MoleculeAtom;
import bio.singa.structure.model.molecules.MoleculeBond;
import bio.singa.structure.model.molecules.MoleculeGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bio.singa.structure.algorithms.molecules.MoleculeIsomorphismFinder.BondConditions.isSameType;

public class RECAPFragmenter {

    private static final Logger logger = LoggerFactory.getLogger(RECAPFragmenter.class);

    private final TreeMap<Integer, FragmentationRule> fragmentationRules = new TreeMap<>();
    private Set<MoleculeGraph> uniqueFragments = new HashSet<>();
    private MoleculeGraph molecule;
    private DirectedGraph<GenericNode<MoleculeGraph>> fragmentSpace;

    public RECAPFragmenter(MoleculeGraph molecule) {
        this.molecule = molecule;
        initRules();
        fragment();
    }

    public Set<MoleculeGraph> getUniqueFragments() {
        return uniqueFragments;
    }

    public void initRules() {
        // 1 - amide rule
        MoleculeGraph amideGraph = SmilesParser.parse("CC(=O)N(C)C");
        fragmentationRules.put(1, new FragmentationRule("amide", amideGraph, Stream.of(3).collect(Collectors.toSet()), 0, 4, 5));
        // 2 - ester rule
        MoleculeGraph esterGraph = SmilesParser.parse("CC(=O)OC");
        fragmentationRules.put(2, new FragmentationRule("ester", esterGraph, Stream.of(3).collect(Collectors.toSet()), 0, 4));
//        // 3 - amine rule
        MoleculeGraph amineGraph = SmilesParser.parse("CN(C)C");
        fragmentationRules.put(3, new FragmentationRule("amine", amineGraph, Stream.of(0, 1, 2).collect(Collectors.toSet()), 0, 2, 3));
        // 4 - urea rule
        MoleculeGraph ureaGraph = SmilesParser.parse("CN(C)C(=O)N(C)C");
        fragmentationRules.put(4, new FragmentationRule("urea", ureaGraph, Stream.of(4, 5).collect(Collectors.toSet()), 0, 2, 6, 7));
        // TODO implement remaining fragmentation rules here
    }

    private void fragment() {

        fragmentSpace = new DirectedGraph<>();

        Set<Integer> visitedNodes = new HashSet<>();
        Stack<GenericNode<MoleculeGraph>> stack = new Stack<>();
        GenericNode<MoleculeGraph> rootNode = new GenericNode<>(0, (MoleculeGraph) molecule.getCopy());
        fragmentSpace.addNode(rootNode);
        stack.add(rootNode);
        visitedNodes.add(rootNode.getIdentifier());
        // depth-first search strategy to build fragment graph
        while (!stack.isEmpty()) {

            GenericNode<MoleculeGraph> currentNode = stack.pop();

            // fragment current node with all rules
            fragmentationRules.forEach((integer, fragmentationRule) -> fragmentationRule.applyTo(currentNode));

            List<GenericNode<MoleculeGraph>> neighbours = currentNode.getNeighbours();
            for (int i = 0; i < neighbours.size(); i++) {
                GenericNode<MoleculeGraph> neighbor = neighbours.get(i);
                if (!visitedNodes.contains(neighbor.getIdentifier())) {
                    stack.add(neighbor);
                    visitedNodes.add(neighbor.getIdentifier());
                }
            }
        }
        logger.info("fragmentation yielded {} unique uniqueFragments in total", fragmentSpace.getNodes().size());
    }

    public DirectedGraph<GenericNode<MoleculeGraph>> getFragmentSpace() {
        return fragmentSpace;
    }

    private class FragmentationRule {
        private String name;
        private MoleculeGraph fragmentGraph;
        private BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition;
        private Set<Integer> bondsToCleave;

        public FragmentationRule(String name, MoleculeGraph fragmentGraph, Set<Integer> bondsToCleave, int... equivalentAtoms) {
            this.name = name;
            this.fragmentGraph = fragmentGraph;
            this.bondsToCleave = bondsToCleave;
            atomCondition = createAtomCondition(equivalentAtoms);
        }

        private BiFunction<MoleculeAtom, MoleculeAtom, Boolean> createAtomCondition(int[] equivalentAtoms) {
            Set<Integer> equivalentAtomSet = Arrays.stream(equivalentAtoms)
                    .boxed()
                    .collect(Collectors.toSet());
            return (atom1, atom2) -> {
                if (equivalentAtomSet.contains(atom1.getIdentifier()) && !atom2.getElement().equals(ElementProvider.UNKOWN)) {
                    return true;
                } else {
                    return atom1.getElement().equals(atom2.getElement());
                }
            };
        }

        public void applyTo(GenericNode<MoleculeGraph> currentNode) {

            logger.info("applying fragmentation rule {}", this);
            MoleculeGraph moleculeGraph = (MoleculeGraph) currentNode.getContent().getCopy();
            MoleculeIsomorphism moleculeIsomorphism = MoleculeIsomorphismFinder.of(fragmentGraph, moleculeGraph, atomCondition, isSameType());
            moleculeIsomorphism.reduceMatches();
            logger.info("non-isomeric matches are {}", moleculeIsomorphism.getFullMatches().size());

            if (moleculeIsomorphism.getFullMatches().isEmpty()) {
                logger.info("fragmentation rule {} does not apply to molecule {}", this, moleculeGraph);
                return;
            }

            for (MoleculeGraph fullMatch : moleculeIsomorphism.getFullMatches()) {
                for (int bondToCleave : bondsToCleave) {
                    logger.debug("cleaving bond {}", bondToCleave);
                    int sourceIdentifier = fragmentGraph.getEdge(bondToCleave).getSource().getIdentifier();
                    int targetIdentifier = fragmentGraph.getEdge(bondToCleave).getTarget().getIdentifier();
                    List<Pair<MoleculeAtom>> atomPairs = moleculeIsomorphism.getAtomPairs(fullMatch);
                    int sourceToCleave = atomPairs.stream()
                            .filter(moleculeAtomPair -> moleculeAtomPair.getFirst().getIdentifier() == sourceIdentifier)
                            .findFirst()
                            .map(moleculeAtomPair -> moleculeAtomPair.getSecond().getIdentifier())
                            .get();

                    int targetToCleave = atomPairs.stream()
                            .filter(moleculeAtomPair -> moleculeAtomPair.getFirst().getIdentifier() == targetIdentifier)
                            .findFirst()
                            .map(moleculeAtomPair -> moleculeAtomPair.getSecond().getIdentifier())
                            .get();

                    // if match is not present it might be already removed
                    Optional<MoleculeBond> edgeToRemove = moleculeGraph.getEdgeBetween(moleculeGraph.getNode(sourceToCleave), moleculeGraph.getNode(targetToCleave));
                    if (edgeToRemove.isPresent()) {

                        // TODO implement routine to store fragmentation information at atoms where bonds were cut here

                        // remove cleavage bond
                        moleculeGraph.removeEdge(edgeToRemove.get());

                        // check if new fragmentation was achieved
                        List<MoleculeGraph> disconnectedSubgraphs = DisconnectedSubgraphFinder.findDisconnectedSubgraphs(moleculeGraph);
                        if (disconnectedSubgraphs.size() > 1) {
                            for (MoleculeGraph disconnectedSubgraph : disconnectedSubgraphs) {
                                // TODO for some reason uniqueness is not guaranteed by set representation and this nasty stream check has to be done :(
                                if (uniqueFragments.stream()
                                        .noneMatch(fragment -> fragment.equals(disconnectedSubgraph))) {
                                    GenericNode<MoleculeGraph> successor = new GenericNode<>(fragmentSpace.nextNodeIdentifier(), disconnectedSubgraph);
                                    fragmentSpace.addNode(successor);
                                    fragmentSpace.addEdgeBetween(currentNode, successor);
                                    uniqueFragments.add(disconnectedSubgraph);
                                }
                            }
                            logger.info("new uniqueFragments produced: {}", disconnectedSubgraphs);
                        }
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "FragmentationRule{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
