package bio.singa.chemistry.features.smiles;

import bio.singa.structure.elements.ElementProvider;
import bio.singa.structure.model.molecules.MoleculeAtom;
import bio.singa.structure.model.molecules.MoleculeBond;
import bio.singa.structure.model.molecules.MoleculeGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static bio.singa.mathematics.algorithms.primes.Primes.getPrimeList;

/**
 * Implementation of SMILES string generation of {@link MoleculeGraph}s with the CANON algorithm as described in
 * <pre>
 *  Tutorials in Chemoinformatics, Chapter 10
 *  ISBN:9781119137962
 * </pre>
 */
public class SmilesGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SmilesGenerator.class);
    private final List<Integer> primeList;
    private MoleculeGraph moleculeGraph;
    private Map<MoleculeAtom, Integer> atomInvariants;
    private Map<MoleculeAtom, Integer> currentRanks;
    private List<MoleculeAtom> rankedAtoms;
    private Map<Integer, RingClosure> ringClosures;
    private StringBuilder smilesJoiner;

    public SmilesGenerator(MoleculeGraph moleculeGraph) {
        this.moleculeGraph = moleculeGraph;
        calculateInvariants();
        invariantToRanks();
        primeList = getPrimeList(moleculeGraph.getNodes().size());
        canon();
        buildSmiles();
    }

    public static String generate(MoleculeGraph moleculeGraph) {
        SmilesGenerator smilesGenerator = new SmilesGenerator(moleculeGraph);
        return "";
    }

    private void buildSmiles() {

        smilesJoiner = new StringBuilder();
//        detectRingClosures();


        MoleculeAtom rootAtom = rankedAtoms.get(0);
        findClosures(rootAtom, null, new HashSet<>(), new HashSet<>());
        constructSmiles(rootAtom, null, new HashSet<>(), new HashSet<>());
//        for (MoleculeAtom rankedAtom : rankedAtoms) {
//            logger.info("visiting atom {}",rankedAtom);
//            smilesJoiner.add(rankedAtom.getElement().getSymbol());
////            if (!ringClosures.isEmpty()) {
////                Optional<Integer> ringNumber = ringClosures.entrySet().stream()
////                        .filter(entry -> entry.getValue().checkForAtom(rankedAtom))
////                        .map(Map.Entry::getKey)
////                        .findFirst();
////                if(ringNumber.isPresent()){
////                    smilesJoiner.add(ringNumber.get().toString());
////                }
////            }
//        }
        System.out.println(smilesJoiner.toString());
    }

    private String constructSmiles(MoleculeAtom moleculeAtom, MoleculeAtom parent, Set<MoleculeAtom> visited, Set<MoleculeAtom> ancestors) {
        // TODO tricky part here
        visited.add(moleculeAtom);
        if (parent != null) {
            MoleculeBond bond = moleculeGraph.getEdgeBetween(moleculeAtom, parent).orElseThrow(IllegalArgumentException::new);
            smilesJoiner.append(bond.getType().getSmilesRepresentation());
        }
        smilesJoiner.append(moleculeAtom.getElement().getSymbol());
        Optional<Integer> ringNumber = ringClosures.entrySet().stream()
                .filter(ring -> ring.getValue().checkForAtom(moleculeAtom))
                .map(Map.Entry::getKey)
                .findFirst();
        if (ringNumber.isPresent()) {
            smilesJoiner.append(ringNumber.get());
        }
        for (MoleculeAtom rankedAtom : rankedAtoms) {
            if (moleculeAtom.getNeighbours().contains(rankedAtom)) {
                if (parent == null || !parent.equals(rankedAtom)) {

                    if (!visited.contains(rankedAtom)) {
                        if (moleculeAtom.getNeighbours().indexOf(rankedAtom) == moleculeAtom.getNeighbours().size() - 1) {
                            smilesJoiner.append(constructSmiles(rankedAtom, moleculeAtom, visited, ancestors));
                        }
                    } else {
                        smilesJoiner.append("(" + constructSmiles(rankedAtom, moleculeAtom, visited, ancestors) + ")");
                    }
                }
            }
        }
        return smilesJoiner.toString();
    }

    private void detectRingClosures() {

        ringClosures = new HashMap<>();

        // visit all nodes once to determine rings (DFS)
        Stack<MoleculeAtom> stack = new Stack<>();
        Set<MoleculeAtom> visitedAtoms = new HashSet<>();
        MoleculeAtom rootAtom = rankedAtoms.get(0);
        stack.add(rootAtom);
        visitedAtoms.add(rootAtom);

        while (!stack.isEmpty()) {
            MoleculeAtom currentAtom = stack.pop();
            logger.debug("visiting {}", currentAtom);
            List<MoleculeAtom> neighbours = currentAtom.getNeighbours();
            for (MoleculeAtom neighbor : neighbours) {
                if (!visitedAtoms.contains(neighbor)) {
                    stack.add(neighbor);
                } else if (stack.contains(currentAtom)) {
                    logger.info("ring between atoms {} and {}", currentAtom, neighbor);
                    stack.remove(currentAtom);
                    RingClosure ringClosure = new RingClosure(currentAtom, neighbor);
                    ringClosures.put(ringClosures.size() + 1, ringClosure);
                    break;
                }
            }
            visitedAtoms.add(currentAtom);
        }
    }

    private void findClosures(MoleculeAtom moleculeAtom, MoleculeAtom parent, Set<MoleculeAtom> visited, Set<MoleculeAtom> ancestors) {
        ringClosures = new HashMap<>();
        visited.add(moleculeAtom);
        ancestors.add(moleculeAtom);
        logger.info("visiting {}", moleculeAtom);
        for (MoleculeAtom rankedAtom : rankedAtoms) {
            if (moleculeAtom.getNeighbours().contains(rankedAtom)) {
                if (parent == null || !parent.equals(rankedAtom)) {
                    if (ancestors.contains(rankedAtom)) {
                        logger.info("closure between {} {}", rankedAtom, moleculeAtom);
                        RingClosure ringClosure = new RingClosure(rankedAtom, moleculeAtom);
                        ringClosures.put(ringClosures.size() + 1, ringClosure);
                    } else if (!visited.contains(rankedAtom)) {
                        findClosures(rankedAtom, moleculeAtom, visited, ancestors);
                    }
                }
            }
        }
        ancestors.remove(moleculeAtom);
    }

    private void canon() {
        Map<MoleculeAtom, Integer> oldRanks = new HashMap<>(currentRanks);
        for (MoleculeAtom moleculeAtom : atomInvariants.keySet()) {
            int invariantNew = 1;
            for (MoleculeAtom neighbour : moleculeAtom.getNeighbours()) {
                invariantNew *= primeList.get(currentRanks.get(neighbour));
            }
            atomInvariants.put(moleculeAtom, invariantNew);
        }
        invariantToRanks();
        // check if number of ranks changed
        if (new HashSet<>(currentRanks.values()).size() == new HashSet<>(oldRanks.values()).size()) {
            // break ties until there are no more
            while (new HashSet<>(currentRanks.values()).size() != currentRanks.size()) {
                breakTies();
            }
            logger.info("canonical ranks determined: {}", currentRanks);
            rankedAtoms = currentRanks.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } else {
            logger.info("recalculation of ranks needed");
            canon();
        }
    }

    private void breakTies() {
        logger.debug("breaking ties");
        // count occurrence of ranks
        Map<Integer, Integer> rankCount = new TreeMap<>();
        currentRanks.forEach((key, value) -> rankCount.merge(value, 1, Integer::sum));
        // identify smallest duplicate rank
        Optional<Integer> smallestDuplicateRank = rankCount.entrySet().stream()
                .filter(rankOccurrence -> rankOccurrence.getValue() > 1)
                .map(Map.Entry::getKey)
                .findFirst();
        if (smallestDuplicateRank.isPresent()) {
            int duplicateRank = smallestDuplicateRank.get();
            logger.debug("smallest duplicate rank is {}", duplicateRank);
            // double invariants of the atoms with duplicate ranks
            for (Map.Entry<MoleculeAtom, Integer> atomRank : currentRanks.entrySet()) {
                if (atomRank.getValue() == duplicateRank) {
                    int newInvariant = atomInvariants.get(atomRank.getKey()) * 2;
                    atomInvariants.put(atomRank.getKey(), newInvariant);
                }
            }
            // change invariant of arbitrary atom with the duplicate rank
            for (Map.Entry<MoleculeAtom, Integer> atomRank : currentRanks.entrySet()) {
                if (atomRank.getValue() == duplicateRank) {
                    int newInvariant = atomInvariants.get(atomRank.getKey()) - 1;
                    atomInvariants.put(atomRank.getKey(), newInvariant);
                    invariantToRanks();
                    break;
                }
            }
        }
    }

    private void calculateInvariants() {
        atomInvariants = new HashMap<>();
        logger.info("calculating invariants");
        for (MoleculeAtom atom : moleculeGraph.getNodes()) {
            int numberOfHydrogens = (int) atom.getNeighbours().stream()
                    .filter(neighborAtom -> neighborAtom.getElement().equals(ElementProvider.HYDROGEN))
                    .count();
            int i1 = atom.getDegree();
            int i2 = atom.getElement().getNumberOfPotentialBonds() - numberOfHydrogens;
            int i3 = atom.getElement().getProtonNumber();
            int i4 = atom.getElement().getCharge() < 0 ? 1 : 0;
            int i5 = atom.getElement().getCharge();
            int i6 = numberOfHydrogens;

            int invariantNumber = Integer.valueOf(String.format("%d%d%d%d%d%d", i1, i2, i3, i4, i5, i6));
            atomInvariants.put(atom, invariantNumber);
        }
    }

    private void invariantToRanks() {
        List<MoleculeAtom> sortedAtoms = new ArrayList<>(atomInvariants.keySet());
        // first iteration, sort by initial invariant
        currentRanks = new HashMap<>();
        sortedAtoms.sort(Comparator.comparing(atom -> atomInvariants.get(atom)));
        int uniqueRanks = 0;
        int previousInvariant = -1;
        for (MoleculeAtom sortedAtom : sortedAtoms) {
            if (atomInvariants.get(sortedAtom) != previousInvariant) {
                uniqueRanks++;
                previousInvariant = atomInvariants.get(sortedAtom);
            }
            currentRanks.put(sortedAtom, uniqueRanks);
        }
    }

    private class RingClosure {

        private MoleculeAtom atom1;
        private MoleculeAtom atom2;
        private MoleculeBond bond;

        public RingClosure(MoleculeAtom atom1, MoleculeAtom atom2) {
            this.atom1 = atom1;
            this.atom2 = atom2;
            bond = moleculeGraph.getEdgeBetween(atom1, atom2).orElseThrow(() -> new IllegalArgumentException("no connection between atoms to be closed"));
        }

        public boolean checkForAtom(MoleculeAtom atom) {
            if (atom1 != null && atom1.equals(atom)) {
                atom1 = null;
                return true;
            }
            if (atom2 != null && atom2.equals(atom)) {
                atom2 = null;
                return true;
            }
            return false;
        }
    }
}
