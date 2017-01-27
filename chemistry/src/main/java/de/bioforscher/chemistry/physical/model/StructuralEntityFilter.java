package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.branches.StructuralModel;
import de.bioforscher.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Nucleotide;

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

    public static Predicate<BranchSubstructure<?>> isModel() {
        return branch -> branch instanceof StructuralModel;
    }

    public static Predicate<BranchSubstructure<?>> isChain() {
        return branch -> branch instanceof Chain;
    }

    public static Predicate<BranchSubstructure<?>> isStructuralMotif() {
        return branch -> branch instanceof StructuralMotif;
    }

    public static Predicate<LeafSubstructure<?, ?>> isAminoAcid() {
        return leaf -> leaf instanceof AminoAcid;
    }

    public static Predicate<LeafSubstructure<?, ?>> isNucleotide() {
        return leaf -> leaf instanceof Nucleotide;
    }

    public static Predicate<LeafSubstructure<?, ?>> isAtomContainer() {
        return leaf -> leaf instanceof AtomContainer;
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
     * Filters for {@link Atom}s.
     */
    public static final class AtomFilter {
        public static Predicate<Atom> isArbitrary() {
            return atom -> true;
        }

        public static Predicate<Atom> isCarbon() {
            return atom -> atom.getElement().equals(ElementProvider.CARBON);
        }

        public static Predicate<Atom> isHydrogen() {
            return atom -> atom.getElement().equals(ElementProvider.HYDROGEN);
        }

        public static Predicate<Atom> isOxygen() {
            return atom -> atom.getElement().equals(ElementProvider.OXYGEN);
        }

        public static Predicate<Atom> isNitrogen() {
            return atom -> atom.getElement().equals(ElementProvider.NITROGEN);
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

        public static Predicate<Atom> isAlphaCarbon() {
            return atom -> Objects.equals(atom.getAtomNameString(), AtomName.CA.getName());
        }

        public static Predicate<Atom> isBetaCarbon() {
            return atom -> Objects.equals(atom.getAtomNameString(), AtomName.CB.getName());
        }

        public static Predicate<Atom> isBackbone() {
            return atom -> Objects.equals(atom.getAtomNameString(), AtomName.N.getName()) ||
                    Objects.equals(atom.getAtomNameString(), AtomName.CA.getName()) ||
                    Objects.equals(atom.getAtomNameString(), AtomName.C.getName()) ||
                    Objects.equals(atom.getAtomNameString(), AtomName.O.getName());
        }

        public static Predicate<Atom> isPhosphorus() {
            return atom -> Objects.equals(atom.getAtomNameString(), AtomName.P.getName());
        }

        public static Predicate<Atom> isSidechain() {
            return isBackbone().negate();
        }

        public static Predicate<Atom> hasAtomName(AtomName atomName) {
            return atom -> Objects.equals(atom.getAtomNameString(), atomName.getName());
        }

        public static Predicate<Atom> hasAtomName(String atomName) {
            return atom -> Objects.equals(atom.getAtomNameString(), atomName);
        }
    }
}
