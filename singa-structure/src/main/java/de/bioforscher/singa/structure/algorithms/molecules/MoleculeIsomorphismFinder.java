package de.bioforscher.singa.structure.algorithms.molecules;

import de.bioforscher.singa.mathematics.algorithms.graphs.isomorphism.RISubgraphFinder;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.structure.model.molecules.MoleculeAtom;
import de.bioforscher.singa.structure.model.molecules.MoleculeBond;
import de.bioforscher.singa.structure.model.molecules.MoleculeGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * @author fk
 */
public class MoleculeIsomorphismFinder {

    private static final Logger logger = LoggerFactory.getLogger(MoleculeIsomorphismFinder.class);

    private static final Function<MoleculeAtom, ?> DEFAULT_ATOM_CONDITION = MoleculeAtom::getElement;
    private static final Function<MoleculeBond, ?> DEFAULT_BOND_CONDITION = MoleculeBond::getType;

    private final Function<MoleculeAtom, ?> atomCondition;
    private final Function<MoleculeBond, ?> bondCondition;
    private MoleculeGraph pattern;
    private MoleculeGraph target;

    private MoleculeIsomorphismFinder(MoleculeGraph pattern, MoleculeGraph target, Function<MoleculeAtom, ?> atomCondition, Function<MoleculeBond, ?> bondCondition) {
        this.pattern = pattern;
        this.target = target;
        this.atomCondition = atomCondition;
        this.bondCondition = bondCondition;
    }

    public static MoleculeIsomorphism of(MoleculeGraph pattern, MoleculeGraph target) {
        return new MoleculeIsomorphismFinder(pattern, target, DEFAULT_ATOM_CONDITION, DEFAULT_BOND_CONDITION).findIsomorphism();
    }

    public static MoleculeIsomorphism of(MoleculeGraph pattern, MoleculeGraph target, Function<MoleculeAtom, ?> atomCondition, Function<MoleculeBond, ?> bondCondition) {
        return new MoleculeIsomorphismFinder(pattern, target, atomCondition, bondCondition).findIsomorphism();
    }

    private MoleculeIsomorphism findIsomorphism() {
        RISubgraphFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph, ?, ?> finder =
                new RISubgraphFinder<>(pattern, target, atomCondition, bondCondition);
        return new MoleculeIsomorphism(finder);
    }
}
