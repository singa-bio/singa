package bio.singa.chemistry.features.smiles;

import bio.singa.structure.elements.ElementProvider;
import bio.singa.structure.model.molecules.MoleculeAtom;
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

    public SmilesGenerator(MoleculeGraph moleculeGraph) {
        this.moleculeGraph = moleculeGraph;
        calculateInvariants();
        invariantToRanks();
        primeList = getPrimeList(moleculeGraph.getNodes().size());
        canon();
    }

    public static String generate(MoleculeGraph moleculeGraph) {
        SmilesGenerator smilesGenerator = new SmilesGenerator(moleculeGraph);
        return "";
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
            // TODO check for ties
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
}
