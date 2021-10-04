package bio.singa.structure.algorithms.superimposition;

import bio.singa.chemistry.model.MoleculeAtom;
import bio.singa.chemistry.model.MoleculeBond;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.core.utility.Pair;
import bio.singa.mathematics.algorithms.graphs.isomorphism.RISubgraphFinder;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.molecules.MoleculeGraphs;
import bio.singa.structure.model.pdb.PdbLeafSubstructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A special version of the {@link SubstructureSuperimposer} that uses subgraph isomorphism to align a candidate
 * against a given reference fragment. This is useful if one wants to align, for example, ligands where no consistent
 * atom names are guaranteed.
 *
 * @author fk
 */
public class FragmentSubstructureSuperimposer extends SubstructureSuperimposer {

    private static final Logger logger = LoggerFactory.getLogger(FragmentSubstructureSuperimposer.class);
    private static final BiFunction<MoleculeAtom, MoleculeAtom, Boolean> DEFAULT_ATOM_CONDITION = (a, b) -> a.getElement().equals(b.getElement());
    private static final BiFunction<MoleculeBond, MoleculeBond, Boolean> DEFAULT_BOND_CONDITION = (a, b) -> a.getType() == b.getType();
    private final BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition;
    private final BiFunction<MoleculeBond, MoleculeBond, Boolean> bondCondition;

    private FragmentSubstructureSuperimposer(List<LeafSubstructure> reference, List<LeafSubstructure> candidate, BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition, BiFunction<MoleculeBond, MoleculeBond, Boolean> bondCondition) {
        super(reference, candidate);
        this.atomCondition = atomCondition;
        this.bondCondition = bondCondition;
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                   List<LeafSubstructure> candidate) throws SubstructureSuperimpositionException {
        return new FragmentSubstructureSuperimposer(reference, candidate, DEFAULT_ATOM_CONDITION, DEFAULT_BOND_CONDITION).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure> reference,
                                                                                   List<LeafSubstructure> candidate,
                                                                                   BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition,
                                                                                   BiFunction<MoleculeBond, MoleculeBond, Boolean> bondCondition) throws SubstructureSuperimpositionException {
        return new FragmentSubstructureSuperimposer(reference, candidate, atomCondition, bondCondition).calculateSuperimposition();
    }

    @Override
    protected Pair<List<Atom>> defineAtoms() {
        logger.debug("calculating subgraph of fragment alignment");
        List<Atom> referenceAtoms = new ArrayList<>();
        List<Atom> candidateAtoms = new ArrayList<>();
        for (int i = 0; i < reference.size(); i++) {
            LeafSubstructure referenceLeafSubstructure = reference.get(i);
            LeafSubstructure candidateLeafSubstructure = candidate.get(i);

            if (referenceLeafSubstructure instanceof PdbLeafSubstructure
                    && candidateLeafSubstructure instanceof PdbLeafSubstructure) {
                MoleculeGraph referenceGraph = MoleculeGraphs.createMoleculeGraphFromStructure((PdbLeafSubstructure) referenceLeafSubstructure);
                MoleculeGraph candidateGraph = MoleculeGraphs.createMoleculeGraphFromStructure((PdbLeafSubstructure) candidateLeafSubstructure);

                RISubgraphFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph> subGraphFinder;

                // decide which substructure to use as pattern graph
                if (referenceGraph.getNodes().size() <= candidateGraph.getNodes().size()) {
                    subGraphFinder = new RISubgraphFinder<>(referenceGraph, candidateGraph, atomCondition, bondCondition);
                } else {
                    subGraphFinder = new RISubgraphFinder<>(candidateGraph, referenceGraph, atomCondition, bondCondition);
                }
                if (subGraphFinder.getFullMatches().size() > 1) {
                    // TODO add option to consider all isomorphisms
                    logger.warn("ambiguous solution found, choosing first one");
                } else if (subGraphFinder.getFullMatches().isEmpty()) {
                    logger.error("reference {} against candidate {} has no common subgraph", reference, candidate);
                    throw new SubstructureSuperimpositionException("failed to define atoms for alignment, no common subgraph");
                }
                for (Pair<MoleculeAtom> moleculeAtomPair : subGraphFinder.getFullMatchPairs().get(0)) {
                    referenceAtoms.add(referenceLeafSubstructure.getAtom(moleculeAtomPair.getFirst().getIdentifier()).orElseThrow(() -> new SubstructureSuperimpositionException("failed to get atoms")));
                    candidateAtoms.add(candidateLeafSubstructure.getAtom(moleculeAtomPair.getSecond().getIdentifier()).orElseThrow(() -> new SubstructureSuperimpositionException("failed to get atoms")));
                }
            }
        }
        return new Pair<>(referenceAtoms, candidateAtoms);
    }
}
