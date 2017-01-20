package de.bioforscher.chemistry.physical.model;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.branches.Chain;
import de.bioforscher.chemistry.physical.branches.StructuralModel;
import de.bioforscher.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.chemistry.physical.leafes.*;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author cl
 */
public class StructureFilter {

    public static Predicate<BranchSubstructure<?>> isModel() {
        return branch -> branch instanceof StructuralModel;
    }

    public static Predicate<BranchSubstructure<?>> isChain() {
        return branch -> branch instanceof Chain;
    }

    public static Predicate<BranchSubstructure<?>> isMotif() {
        return branch -> branch instanceof StructuralMotif;
    }

    public static Predicate<LeafSubstructure<?,?>> isAtomContainer() {
        return leaf -> leaf instanceof AtomContainer;
    }

    public static Predicate<LeafSubstructure<?,?>> isResidue() {
        return leaf -> leaf instanceof AminoAcid;
    }

    public static Predicate<LeafSubstructure<?,?>> isNucleotide() {
        return leaf -> leaf instanceof Nucleotide;
    }

    public static Predicate<Chain> isInChain(String chainIdentifier) {
        return chain -> chainIdentifier.equals(chain.getChainIdentifier());
    }

    public static Predicate<Atom> hasAtomName(AtomName atomName) {
        return atom -> Objects.equals(atom.getAtomNameString(), atomName.getName());
    }

    public static Predicate<Atom> hasAtomName(String atomName) {
        return atom -> atom.getAtomNameString().equals(atomName);
    }

}
