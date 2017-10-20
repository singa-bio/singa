package de.bioforscher.singa.structure.model.graph.model;

import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.core.utility.Range;
import de.bioforscher.singa.structure.model.graph.atoms.AtomName;
import de.bioforscher.singa.structure.model.interfaces.*;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * This static class bundles filters for {@link LeafSubstructure}s and {@link Atom}s that can be
 * concatenated by using the {@link Predicate} interface.
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
            return this.filter;
        }
    }

    /**
     * Simple {@link AtomFilter} representation as functional Enum class.
     */
    public enum AtomFilterType {
        ALPHA_CARBON(AtomFilter.isAlphaCarbon()),
        ARBITRARY(AtomFilter.isArbitrary()),
        BACKBONE(AtomFilter.isBackbone()),
        BACKBONE_CARBON(AtomFilter.isBackboneCarbon()),
        BACKBONE_NITROGEN(AtomFilter.isBackboneNitrogen()),
        BACKBONE_OXYGEN(AtomFilter.isBackboneOxygen()),
        BETA_CARBON(AtomFilter.isBetaCarbon()),
        CARBON(AtomFilter.isCarbon()),
        HYDROGEN(AtomFilter.isHydrogen()),
        NITROGEN(AtomFilter.isNitrogen()),
        OXYGEN(AtomFilter.isOxygen()),
        PHOSPHORUS(AtomFilter.isPhosphorus()),
        SIDE_CHAIN(AtomFilter.isSideChain());

        private final Predicate<Atom> filter;

        AtomFilterType(Predicate<Atom> filter) {
            this.filter = filter;
        }

        public Predicate<Atom> getFilter() {
            return this.filter;
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
            return atom -> atom.getIdentifier() == identifier;
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
            return atom -> atom.getElement().equals(ElementProvider.HYDROGEN);
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
            return atom -> range.isInRange(atom.getIdentifier());
        }
    }
}
