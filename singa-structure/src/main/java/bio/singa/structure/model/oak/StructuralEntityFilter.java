package bio.singa.structure.model.oak;

import bio.singa.core.utility.Range;
import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.structure.model.interfaces.*;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * This static class bundles filters for {@link LeafSubstructure}s and {@link Atom}s that can be concatenated by using
 * the {@link Predicate} interface.
 *
 * @author cl
 * @see Predicate
 */
public class StructuralEntityFilter {

    /**
     * Simple {@link LeafFilter} representation as functional Enum class.
     */
    public enum LeafFilterType {

        AMINO_ACID(LeafFilter.isAminoAcid()),
        ATOM_CONTAINER(LeafFilter.isLigand()),
        NUCLEOTIDE(LeafFilter.isNucleotide());

        private final Predicate<LeafSubstructure> filter;

        LeafFilterType(Predicate<LeafSubstructure> filter) {
            this.filter = filter;
        }

        public Predicate<LeafSubstructure> getFilter() {
            return filter;
        }
    }

    /**
     * Simple {@link AtomFilter} representation as functional Enum class.
     */
    public enum AtomFilterType {
        ALPHA_CARBON(AtomFilter.isAlphaCarbon(), "alpha carbon"),
        ARBITRARY(AtomFilter.isArbitrary(), "all-atom"),
        BACKBONE(AtomFilter.isBackbone(), "backbone"),
        BACKBONE_CARBON(AtomFilter.isBackboneCarbon(), "backbone carbon"),
        BACKBONE_NITROGEN(AtomFilter.isBackboneNitrogen(), "backbone nitrogen"),
        BACKBONE_OXYGEN(AtomFilter.isBackboneOxygen(), "backbone oxygen"),
        BETA_CARBON(AtomFilter.isBetaCarbon(), "beta carbon"),
        CARBON(AtomFilter.isCarbon(), "any carbon atom"),
        HYDROGEN(AtomFilter.isHydrogen(), "any hydrogen atom"),
        NITROGEN(AtomFilter.isNitrogen(), "any nitrogen atom"),
        OXYGEN(AtomFilter.isOxygen(), "any oxygen atom"),
        PHOSPHORUS(AtomFilter.isPhosphorus(), "any phosphorus atom"),
        SIDE_CHAIN(AtomFilter.isSideChain(), "side chain");

        private final Predicate<Atom> filter;
        private final String description;

        AtomFilterType(Predicate<Atom> filter, String description) {
            this.filter = filter;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public Predicate<Atom> getFilter() {
            return filter;
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

        public static Predicate<LeafSubstructure> hasIdentifier(Object identifier) {
            return leafSubstructure -> leafSubstructure.getIdentifier().equals(identifier);
        }

        public static Predicate<LeafSubstructure> isAminoAcid() {
            return leaf -> leaf instanceof AminoAcid;
        }

        public static Predicate<LeafSubstructure> isLigand() {
            return leaf -> leaf instanceof Ligand;
        }

        public static Predicate<LeafSubstructure> isNucleotide() {
            return leaf -> leaf instanceof Nucleotide;
        }

        public static Predicate<LeafSubstructure> isWithinRange(int startIdentifier, int endIdentifier) {
            Range<Integer> range = new Range<>(startIdentifier, endIdentifier);
            return leaf -> range.isInRange(leaf.getIdentifier().getSerial());
        }
    }

    /**
     * Filters for {@link Atom}s.
     */
    public static final class AtomFilter {

        public static Predicate<Atom> hasAtomName(String atomName) {
            return atom -> Objects.equals(atom.getAtomName(), atomName);
        }

        public static Predicate<Atom> hasAtomName(AtomName atomName) {
            return atom -> Objects.equals(atom.getAtomName(), atomName.getName());
        }

        public static Predicate<Atom> hasAtomNames(String... atomNames) {
            Predicate<Atom> predicate = atom -> false;
            for (String atomName : atomNames) {
                predicate = predicate.or(hasAtomName(atomName));
            }
            return predicate;
        }

        public static Predicate<Atom> hasIdentifier(int identifier) {
            return atom -> atom.getAtomIdentifier() == identifier;
        }

        public static Predicate<Atom> isAlphaCarbon() {
            return atom -> Objects.equals(atom.getAtomName(), AtomName.CA.getName());
        }

        public static Predicate<Atom> isArbitrary() {
            return atom -> true;
        }

        public static Predicate<Atom> isBackbone() {
            return atom -> Objects.equals(atom.getAtomName(), AtomName.N.getName()) ||
                    Objects.equals(atom.getAtomName(), AtomName.CA.getName()) ||
                    Objects.equals(atom.getAtomName(), AtomName.C.getName()) ||
                    Objects.equals(atom.getAtomName(), AtomName.O.getName());
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
            return atom -> Objects.equals(atom.getAtomName(), AtomName.CB.getName());
        }

        public static Predicate<Atom> isCarbon() {
            return atom -> atom.getElement().equals(ElementProvider.CARBON);
        }

        public static Predicate<Atom> isHydrogen() {
            return atom -> atom.getElement().getProtonNumber() == 1;
        }

        public static Predicate<Atom> isNitrogen() {
            return atom -> atom.getElement().equals(ElementProvider.NITROGEN);
        }

        public static Predicate<Atom> isOxygen() {
            return atom -> atom.getElement().equals(ElementProvider.OXYGEN);
        }

        public static Predicate<Atom> isPhosphorus() {
            return atom -> Objects.equals(atom.getAtomName(), AtomName.P.getName());
        }

        public static Predicate<Atom> isSideChain() {
            return isBackbone().negate();
        }

        public static Predicate<Atom> isWithinRange(int startId, int endId) {
            Range<Integer> range = new Range<>(startId, endId);
            return atom -> range.isInRange(atom.getAtomIdentifier());
        }
    }
}
