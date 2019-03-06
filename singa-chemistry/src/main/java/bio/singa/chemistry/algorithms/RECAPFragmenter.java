package bio.singa.chemistry.algorithms;

import bio.singa.chemistry.features.smiles.SmilesParser;
import bio.singa.core.utility.Pair;
import bio.singa.mathematics.algorithms.graphs.DisconnectedSubgraphFinder;
import bio.singa.mathematics.graphs.model.DirectedGraph;
import bio.singa.mathematics.graphs.model.GenericNode;
import bio.singa.structure.algorithms.molecules.MoleculeIsomorphism;
import bio.singa.structure.algorithms.molecules.MoleculeIsomorphismFinder;
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
    private Deque<MoleculeGraph> queue = new ArrayDeque<>();

    public RECAPFragmenter(Set<MoleculeGraph> molecules) {
        initRules();
        molecules.forEach(this::fragment);
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
    }

    private void fragment(MoleculeGraph molecule) {

        queue.add(molecule);
        while (!queue.isEmpty()) {
            // apply fragmentation rules
            MoleculeGraph currentGraph = queue.poll();
            logger.info("processing {}", currentGraph);
            fragmentationRules.forEach((id, rule) -> {
                rule.applyTo(currentGraph);
            });
        }
    }

    public DirectedGraph<GenericNode<MoleculeGraph>> getFragmentGraph() {
        return null;
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
                if (equivalentAtomSet.contains(atom1.getIdentifier())) {
                    return true;
                } else {
                    return atom1.getElement().equals(atom2.getElement());
                }
            };
        }

        public void applyTo(MoleculeGraph moleculeGraph) {
            logger.info("applying fragmentation rule {}", this);
//            MoleculeGraph copyOfOther = (MoleculeGraph) other.getCopy();
            MoleculeIsomorphism moleculeIsomorphism = MoleculeIsomorphismFinder.of(fragmentGraph, moleculeGraph, atomCondition, isSameType());
            moleculeIsomorphism.reduceMatches();

            if (moleculeIsomorphism.getFullMatches().isEmpty()) {
                logger.info("fragmentation rule {} does not apply to molecule {}", this, moleculeGraph);
                return;
            }

            for (int bondToCleave : bondsToCleave) {
                logger.debug("cleaving bond {}", bondToCleave);
                int sourceIdentifier = fragmentGraph.getEdge(bondToCleave).getSource().getIdentifier();
                int targetIdentifier = fragmentGraph.getEdge(bondToCleave).getTarget().getIdentifier();
                for (MoleculeGraph fullMatch : moleculeIsomorphism.getFullMatches()) {
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
                        moleculeGraph.removeEdge(edgeToRemove.get());
                        // check if new fragmentation was achieved
                        List<MoleculeGraph> disconnectedSubgraphs = DisconnectedSubgraphFinder.findDisconnectedSubgraphs(moleculeGraph);
                        if (disconnectedSubgraphs.size() > 1) {
                            queue.addAll(disconnectedSubgraphs);
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
