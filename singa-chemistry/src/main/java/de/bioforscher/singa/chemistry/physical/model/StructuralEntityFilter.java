package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.AtomName;
import de.bioforscher.singa.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.branches.StructuralModel;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.singa.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.leafes.Nucleotide;
import de.bioforscher.singa.core.utility.Range;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * This static class bundles filters for {@link LeafSubstructure}s and {@link BranchSubstructure}s that can be concatenated by using the {@link Predicate}
 * interface.
 *
 * @author cl
 * @see Predicate
 */
public class StructuralEntityFilter {

    /**
     * Filters for {@link BranchSubstructure}s.
     */
    public static final class BranchFilter {

        public static Predicate<BranchSubstructure<?>> hasId(int id) {
            return branchSubstructure -> branchSubstructure.getIdentifier() == id;
        }

        public static Predicate<BranchSubstructure<?>> isChain() {
            return branch -> branch instanceof Chain;
        }

        public static Predicate<BranchSubstructure<?>> isModel() {
            return branch -> branch instanceof StructuralModel;
        }

        public static Predicate<BranchSubstructure<?>> isStructuralMotif() {
            return branch -> branch instanceof StructuralMotif;
        }

    }

    /**
     * Filters for {@link Chain}s.
     */
    public static final class ChainFilter {
        public static Predicate<Chain> isInChain(String chainIdentifier) {
            return chain -> chainIdentifier.equals(chain.getChainIdentifier());
        }
    }

    /**
     * Filters for {@link LeafSubstructure}s.
     */
    public static final class LeafFilter {

        public static Predicate<LeafSubstructure<?, ?>> hasId(int id) {
            return leafSubstructure -> leafSubstructure.getIdentifier() == id;
        }

        public static Predicate<LeafSubstructure<?, ?>> isAminoAcid() {
            return leaf -> leaf instanceof AminoAcid;
        }

        public static Predicate<LeafSubstructure<?, ?>> isAtomContainer() {
            return leaf -> leaf instanceof AtomContainer;
        }

        public static Predicate<LeafSubstructure<?, ?>> isNucleotide() {
            return leaf -> leaf instanceof Nucleotide;
        }

        public static Predicate<LeafSubstructure<?, ?>> isWithinRange(int startId, int endId) {
            Range<Integer> range = new Range<>(startId, endId);
            return leaf -> range.isInRange(leaf.getIdentifier());
        }
    }

    /**
     * Filters for {@link Atom}s.
     */
    public static final class AtomFilter {

        public static Predicate<Atom> hasAtomName(String atomName) {
            return atom -> Objects.equals(atom.getAtomNameString(), atomName);
        }

        public static Predicate<Atom> hasAtomName(AtomName atomName) {
            return atom -> Objects.equals(atom.getAtomNameString(), atomName.getName());
        }

        public static Predicate<Atom> hasIdentifier(int identifier) {
            return atom -> atom.getIdentifier() == identifier;
        }

        public static Predicate<Atom> isAlphaCarbon() {
            return atom -> Objects.equals(atom.getAtomNameString(), AtomName.CA.getName());
        }

        public static Predicate<Atom> isArbitrary() {
            return atom -> true;
        }

        public static Predicate<Atom> isBackbone() {
            return atom -> Objects.equals(atom.getAtomNameString(), AtomName.N.getName()) ||
                    Objects.equals(atom.getAtomNameString(), AtomName.CA.getName()) ||
                    Objects.equals(atom.getAtomNameString(), AtomName.C.getName()) ||
                    Objects.equals(atom.getAtomNameString(), AtomName.O.getName());
        }

        public static Predicate<Atom> isBackboneCarbon() {
            return isBackbone().and(isCarbon()).and(isAlphaCarbon().negate());
        }

        public static Predicate<Atom> isBackboneNitrogen() {
            return isBackbone().and(isNitrogen());
        }

        public static Predicate<Atom> isBackboneOxygen() {
            return isBackbone().and(isOxygen());
        }

        public static Predicate<Atom> isBetaCarbon() {
            return atom -> Objects.equals(atom.getAtomNameString(), AtomName.CB.getName());
        }

        public static Predicate<Atom> isCarbon() {
            return atom -> atom.getElement().equals(ElementProvider.CARBON);
        }

        public static Predicate<Atom> isHydrogen() {
            return atom -> atom.getElement().equals(ElementProvider.HYDROGEN);
        }

        public static Predicate<Atom> isNitrogen() {
            return atom -> atom.getElement().equals(ElementProvider.NITROGEN);
        }

        public static Predicate<Atom> isOxygen() {
            return atom -> atom.getElement().equals(ElementProvider.OXYGEN);
        }

        public static Predicate<Atom> isPhosphorus() {
            return atom -> Objects.equals(atom.getAtomNameString(), AtomName.P.getName());
        }

        public static Predicate<Atom> isSidechain() {
            return isBackbone().negate();
        }

        public static Predicate<Atom> isWithinRange(int startId, int endId) {
            Range<Integer> range = new Range<>(startId, endId);
            return atom -> range.isInRange(atom.getIdentifier());
        }
    }
}
