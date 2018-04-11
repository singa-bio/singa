package de.bioforscher.singa.structure.algorithms.superimposition;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.algorithms.graphs.isomorphism.RISubgraphFinder;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.molecules.MoleculeAtom;
import de.bioforscher.singa.structure.model.molecules.MoleculeBond;
import de.bioforscher.singa.structure.model.molecules.MoleculeGraph;
import de.bioforscher.singa.structure.model.molecules.MoleculeGraphs;
import de.bioforscher.singa.structure.model.oak.OakLeafSubstructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A special version of the {@link SubstructureSuperimposer} that uses subgraph isomorphism to align a candidate
 * against a given reference fragment. This is useful if one wants to align, for example, ligands where no consistent
 * atom names are guaranteed.
 *
 * @author fk
 */
public class FragmentSubstructureSuperimposer extends SubstructureSuperimposer {

    private static final Logger logger = LoggerFactory.getLogger(FragmentSubstructureSuperimposer.class);
    private static final Function<MoleculeAtom, ?> DEFAULT_ATOM_CONDITION = MoleculeAtom::getElement;
    private static final Function<MoleculeBond, ?> DEFAULT_BOND_CONDITION = MoleculeBond::getType;
    private final Function<MoleculeAtom, ?> atomCondition;
    private final Function<MoleculeBond, ?> bondCondition;

    private FragmentSubstructureSuperimposer(List<LeafSubstructure<?>> reference, List<LeafSubstructure<?>> candidate, Function<MoleculeAtom, ?> atomCondition, Function<MoleculeBond, ?> bondCondition) {
        super(reference, candidate);
        this.atomCondition = atomCondition;
        this.bondCondition = bondCondition;
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure<?>> reference,
                                                                                   List<LeafSubstructure<?>> candidate) throws SubstructureSuperimpositionException {
        return new FragmentSubstructureSuperimposer(reference, candidate, DEFAULT_ATOM_CONDITION, DEFAULT_BOND_CONDITION).calculateSuperimposition();
    }

    public static SubstructureSuperimposition calculateSubstructureSuperimposition(List<LeafSubstructure<?>> reference,
                                                                                   List<LeafSubstructure<?>> candidate,
                                                                                   Function<MoleculeAtom, ?> atomCondition,
                                                                                   Function<MoleculeBond, ?> bondCondition) throws SubstructureSuperimpositionException {
        return new FragmentSubstructureSuperimposer(reference, candidate, atomCondition, bondCondition).calculateSuperimposition();
    }

    @Override
    protected Pair<List<Atom>> defineAtoms() {
        logger.debug("calculating subgraph of fragment alignment");
        List<Atom> referenceAtoms = new ArrayList<>();
        List<Atom> candidateAtoms = new ArrayList<>();
        for (int i = 0; i < reference.size(); i++) {
            LeafSubstructure<?> referenceLeafSubstructure = reference.get(i);
            LeafSubstructure<?> candidateLeafSubstructure = candidate.get(i);

            if (referenceLeafSubstructure instanceof OakLeafSubstructure
                    && candidateLeafSubstructure instanceof OakLeafSubstructure) {
                MoleculeGraph referenceGraph = MoleculeGraphs.createMoleculeGraphFromStructure((OakLeafSubstructure<?>) referenceLeafSubstructure);
                MoleculeGraph candidateGraph = MoleculeGraphs.createMoleculeGraphFromStructure((OakLeafSubstructure<?>) candidateLeafSubstructure);

                RISubgraphFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph, ?, ?>
                        subGraphFinder;

                // decide which substructure to use as pattern graph
                if (referenceGraph.getNodes().size() <= candidateGraph.getNodes().size()) {
                    subGraphFinder = new RISubgraphFinder<>(referenceGraph, candidateGraph, atomCondition, bondCondition);
                } else {
                    subGraphFinder = new RISubgraphFinder<>(candidateGraph, referenceGraph, atomCondition, bondCondition);
                }
                if (subGraphFinder.getFullMatches().size() > 1) {
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
