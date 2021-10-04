package bio.singa.structure.algorithms.superimposition;

import bio.singa.chemistry.model.MoleculeAtom;
import bio.singa.chemistry.model.MoleculeBond;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.core.utility.Pair;
import bio.singa.mathematics.algorithms.graphs.MaximumCommonSubgraphFinder;
import bio.singa.mathematics.graphs.model.GenericNode;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.molecules.MoleculeGraphs;
import bio.singa.structure.model.pdb.PdbLeafSubstructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * The most flexible {@link SubstructureSuperimposer} that uses maximum subgraph isomorphism detection to align two
 * structures. As long as there is a shared maximum common subgraph of at least three atoms a meaningful alignment will
 * be produced.
 *
 * @author fk
 */
public class MaximumCommonSubgraphSuperimposer extends SubstructureSuperimposer {

    private static final Logger logger = LoggerFactory.getLogger(MaximumCommonSubgraphSuperimposer.class);

    // TODO abstract subgraph-based superimpositions (see FragmentsSubstructureSuperimposer)
    private static final BiFunction<MoleculeAtom, MoleculeAtom, Boolean> DEFAULT_ATOM_CONDITION = (a, b) -> a.getElement().equals(b.getElement());
    private static final BiFunction<MoleculeBond, MoleculeBond, Boolean> DEFAULT_BOND_CONDITION = (a, b) -> a.getType() == b.getType();
    private final BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition;
    private final BiFunction<MoleculeBond, MoleculeBond, Boolean> bondCondition;

    private MaximumCommonSubgraphSuperimposer(List<LeafSubstructure> reference, List<LeafSubstructure> candidate, BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition, BiFunction<MoleculeBond, MoleculeBond, Boolean> bondCondition) {
        super(reference, candidate);
        this.atomCondition = atomCondition;
        this.bondCondition = bondCondition;
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                   List<LeafSubstructure> candidate) throws SubstructureSuperimpositionException {
        return new MaximumCommonSubgraphSuperimposer(reference, candidate, DEFAULT_ATOM_CONDITION, DEFAULT_BOND_CONDITION).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                   List<LeafSubstructure> candidate,
                                                                                   BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition,
                                                                                   BiFunction<MoleculeBond, MoleculeBond, Boolean> bondCondition) throws SubstructureSuperimpositionException {
        return new MaximumCommonSubgraphSuperimposer(reference, candidate, atomCondition, bondCondition).calculateSuperimposition();
    }

    @Override
    protected Pair<List<Atom>> defineAtoms() {
        List<Atom> referenceAtoms = new ArrayList<>();
        List<Atom> candidateAtoms = new ArrayList<>();
        for (int i = 0; i < reference.size(); i++) {
            LeafSubstructure referenceLeafSubstructure = reference.get(i);
            LeafSubstructure candidateLeafSubstructure = candidate.get(i);

            if (referenceLeafSubstructure instanceof PdbLeafSubstructure
                    && candidateLeafSubstructure instanceof PdbLeafSubstructure) {
                MoleculeGraph referenceGraph = MoleculeGraphs.createMoleculeGraphFromStructure((PdbLeafSubstructure) referenceLeafSubstructure);
                MoleculeGraph candidateGraph = MoleculeGraphs.createMoleculeGraphFromStructure((PdbLeafSubstructure) candidateLeafSubstructure);

                MaximumCommonSubgraphFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph> mcs = new MaximumCommonSubgraphFinder<>(referenceGraph, candidateGraph, atomCondition, bondCondition);
                List<Set<GenericNode<Pair<MoleculeAtom>>>> maximumCliques = mcs.getMaximumCliques();

                if (maximumCliques.isEmpty()) {
                    logger.warn("no maximum common subgraph for {}-{}", referenceLeafSubstructure, candidateLeafSubstructure);
                    continue;
                }

                if (maximumCliques.size() > 1) {
                    // TODO add option to consider all isomorphisms
                    logger.warn("ambiguous solution found, choosing first one");
                }

                Set<GenericNode<Pair<MoleculeAtom>>> maximumClique = maximumCliques.get(0);
                for (GenericNode<Pair<MoleculeAtom>> nodePair : maximumClique) {
                    referenceAtoms.add(referenceLeafSubstructure.getAtom(nodePair.getContent().getFirst().getIdentifier()).orElseThrow(() -> new SubstructureSuperimpositionException("failed to get atoms")));
                    candidateAtoms.add(candidateLeafSubstructure.getAtom(nodePair.getContent().getSecond().getIdentifier()).orElseThrow(() -> new SubstructureSuperimpositionException("failed to get atoms")));
                }
            }
        }
        return new Pair<>(referenceAtoms, candidateAtoms);
    }
}
