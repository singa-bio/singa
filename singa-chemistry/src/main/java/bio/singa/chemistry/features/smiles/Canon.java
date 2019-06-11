package bio.singa.chemistry.features.smiles;

import bio.singa.structure.model.molecules.MoleculeAtom;
import bio.singa.structure.model.molecules.MoleculeBond;
import bio.singa.structure.model.molecules.MoleculeGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static bio.singa.mathematics.algorithms.primes.Primes.getPrimeList;

public class Canon {

    public static final Comparator<MoleculeAtom> ATOM_COMPARATOR = Comparator.comparing(MoleculeAtom::getIdentifier);
    private static final Logger logger = LoggerFactory.getLogger(Canon.class);
    private final List<Integer> primeList;

    private MoleculeGraph moleculeGraph;
    private int rankCount;

    public Canon(MoleculeGraph moleculeGraph) {
        this.moleculeGraph = moleculeGraph;
        primeList = getPrimeList(moleculeGraph.getNodes().size());
        canon();
    }

    private void canon() {
        Map<MoleculeAtom, Integer> initialInvariants = calculateInvariants();

        // initialize first all 1 ranks
        Map<MoleculeAtom, Integer> ranks = new TreeMap<>(ATOM_COMPARATOR);
        Map<MoleculeAtom, Integer> finalRanks = ranks;
        moleculeGraph.getNodes().forEach(moleculeAtom -> finalRanks.putIfAbsent(moleculeAtom, 1));

        ranks = invariantsToRanks(initialInvariants, ranks);
        while (rankCount < ranks.size()) {
            Map<MoleculeAtom, Integer> newRanks = canonIterate(ranks);
            if (rankCount < ranks.size()) {
                newRanks = breakTies(ranks);
            }
            ranks = newRanks;
        }
        return;
    }

    private Map<MoleculeAtom, Integer> breakTies(Map<MoleculeAtom, Integer> ranks) {
        // count occurrence of ranks
        Map<Integer, Integer> rankCount = new TreeMap<>();
        ranks.forEach((key, value) -> rankCount.merge(value, 1, Integer::sum));

        // identify smallest duplicate rank
        int smallestDuplicateRank = rankCount.entrySet().stream()
                .filter(rankOccurrence -> rankOccurrence.getValue() > 1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Map<MoleculeAtom, Integer> invariants = new TreeMap<>(ATOM_COMPARATOR);
        for (MoleculeAtom atom : moleculeGraph.getNodes()) {
            invariants.put(atom, 2 * ranks.get(atom));
        }

        for (MoleculeAtom atom : moleculeGraph.getNodes()) {
            if (ranks.get(atom) == smallestDuplicateRank) {
                invariants.put(atom, invariants.get(atom) - 1);
                break;
            }
        }

        return invariantsToRanks(invariants, ranks);
    }

    private Map<MoleculeAtom, Integer> canonIterate(Map<MoleculeAtom, Integer> ranks) {
        int oldRankCount = 0;
        while (oldRankCount < rankCount) {
            oldRankCount = rankCount;
            ranks = newRanks(ranks);
        }
        return ranks;
    }

    private Map<MoleculeAtom, Integer> newRanks(Map<MoleculeAtom, Integer> ranks) {
        Map<MoleculeAtom, Integer> invariants = new TreeMap<>(ATOM_COMPARATOR);
        for (MoleculeAtom atom : moleculeGraph.getNodes()) {
            int product = 1;
            for (MoleculeAtom neighbour : atom.getNeighbours()) {
                product *= primeList.get(ranks.get(neighbour) - 1);
            }
            invariants.put(atom, product);
        }
        return invariantsToRanks(invariants, ranks);
    }

    private Map<MoleculeAtom, Integer> invariantsToRanks(Map<MoleculeAtom, Integer> invariants, Map<MoleculeAtom, Integer> ranks) {
        List<MoleculeAtom> sortedAtoms = moleculeGraph.getNodes().stream()
                .sorted(Comparator.comparing(ranks::get).thenComparing(invariants::get))
                .collect(Collectors.toList());
        rankCount = 0;
        int previous = -1;
        for (MoleculeAtom sortedAtom : sortedAtoms) {
            if (invariants.get(sortedAtom) != previous) {
                rankCount++;
                previous = invariants.get(sortedAtom);
            }
            ranks.put(sortedAtom, rankCount);
        }
        return ranks;
    }


    /**
     * Calculates the invariant numbers.
     *
     * @return
     */
    private TreeMap<MoleculeAtom, Integer> calculateInvariants() {
        TreeMap<MoleculeAtom, Integer> invariants = new TreeMap<>(Comparator.comparing(MoleculeAtom::getIdentifier));
        logger.info("calculating invariants");
        for (MoleculeAtom atom : moleculeGraph.getNodes()) {
            // the bonds formed
            int numberOfBonds = 0;
            for (MoleculeAtom neighbour : atom.getNeighbours()) {
                MoleculeBond bond = moleculeGraph.getEdgeBetween(atom, neighbour).orElseThrow(IllegalStateException::new);
                numberOfBonds += bond.getType().getBondOrder();
            }
            int i1 = atom.getDegree();
            int i2 = numberOfBonds;
            int i3 = atom.getElement().getProtonNumber();
            int i4 = atom.getElement().getCharge() < 0 ? 1 : 0;
            int i5 = Math.abs(atom.getElement().getCharge());
            int i6 = atom.getElement().getNumberOfPotentialBonds() - numberOfBonds;

            int invariantNumber = Integer.valueOf(String.format("%d%d%d%d%d%d", i1, i2, i3, i4, i5, i6));
            invariants.put(atom, invariantNumber);
        }
        return invariants;
    }
}
