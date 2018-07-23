package bio.singa.structure.algorithms.molecules;

import bio.singa.mathematics.algorithms.graphs.isomorphism.RISubgraphFinder;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.structure.model.molecules.MoleculeAtom;
import bio.singa.structure.model.molecules.MoleculeBond;
import bio.singa.structure.model.molecules.MoleculeGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;

/**
 * A molecule isomorphism finder based on {@link RISubgraphFinder}.
 *
 * @author fk
 */
public class MoleculeIsomorphismFinder {

    private static final Logger logger = LoggerFactory.getLogger(MoleculeIsomorphismFinder.class);

    private static final BiFunction<MoleculeAtom, MoleculeAtom, Boolean> DEFAULT_ATOM_CONDITION = AtomConditions.isSameElement();
    private static final BiFunction<MoleculeBond, MoleculeBond, Boolean> DEFAULT_BOND_CONDITION = BondConditions.isSameType();

    private final BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition;
    private final BiFunction<MoleculeBond, MoleculeBond, Boolean> bondCondition;
    private MoleculeGraph pattern;
    private MoleculeGraph target;

    private MoleculeIsomorphismFinder(MoleculeGraph pattern, MoleculeGraph target, BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition, BiFunction<MoleculeBond, MoleculeBond, Boolean> bondCondition) {
        logger.info("molecule isomorphism finder initialized with pattern {} against target {}", pattern, target);
        this.pattern = pattern;
        this.target = target;
        this.atomCondition = atomCondition;
        this.bondCondition = bondCondition;
    }

    public static MoleculeIsomorphism of(MoleculeGraph pattern, MoleculeGraph target) {
        return new MoleculeIsomorphismFinder(pattern, target, DEFAULT_ATOM_CONDITION, DEFAULT_BOND_CONDITION).findIsomorphism();
    }

    public static MoleculeIsomorphism of(MoleculeGraph pattern, MoleculeGraph target, BiFunction<MoleculeAtom, MoleculeAtom, Boolean> atomCondition, BiFunction<MoleculeBond, MoleculeBond, Boolean> bondCondition) {
        return new MoleculeIsomorphismFinder(pattern, target, atomCondition, bondCondition).findIsomorphism();
    }

    private MoleculeIsomorphism findIsomorphism() {
        RISubgraphFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph> finder =
                new RISubgraphFinder<>(pattern, target, atomCondition, bondCondition);
        return new MoleculeIsomorphism(finder);
    }

    /**
     * Predefined atom conditions for isomorphism testing.
     */
    public final static class AtomConditions {

        public static BiFunction<MoleculeAtom, MoleculeAtom, Boolean> isArbitrary() {
            return (patternAtom, targetAtom) -> true;
        }

        public static BiFunction<MoleculeAtom, MoleculeAtom, Boolean> isSameElement() {
            return (patternAtom, targetAtom) -> patternAtom.getElement().equals(targetAtom.getElement());
        }
    }

    /**
     * Predefined bond conditions for isomorphism testing.
     */
    public static final class BondConditions {

        public static BiFunction<MoleculeBond, MoleculeBond, Boolean> isArbitrary() {
            return (patternBond, targetBond) -> true;
        }

        public static BiFunction<MoleculeBond, MoleculeBond, Boolean> isSameType() {
            return (patternBond, targetBond) -> patternBond.getType() == targetBond.getType();
        }
    }
}
