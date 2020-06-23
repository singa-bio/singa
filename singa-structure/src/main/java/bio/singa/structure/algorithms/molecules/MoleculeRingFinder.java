package bio.singa.structure.algorithms.molecules;

import bio.singa.chemistry.model.MoleculeAtom;
import bio.singa.chemistry.model.MoleculeBond;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.mathematics.algorithms.graphs.SmallestSetOfSmallestRingsFinder;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.List;
import java.util.Set;

/**
 * Identifies rings in {@link MoleculeGraph}s. Operates based on {@link SmallestSetOfSmallestRingsFinder}.
 */
public class MoleculeRingFinder {

    private final MoleculeGraph moleculeGraph;
    private SmallestSetOfSmallestRingsFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph> ringFinder;

    private MoleculeRingFinder(MoleculeGraph moleculeGraph) {
        this.moleculeGraph = moleculeGraph;
        findRings();
    }

    public static List<Set<MoleculeAtom>> of(MoleculeGraph moleculeGraph) {
        MoleculeRingFinder moleculeRingFinder = new MoleculeRingFinder(moleculeGraph);
        return moleculeRingFinder.getRings();
    }

    private void findRings() {
        ringFinder = new SmallestSetOfSmallestRingsFinder<>(moleculeGraph);
    }

    private List<Set<MoleculeAtom>> getRings() {
        return ringFinder.getRings();
    }
}
