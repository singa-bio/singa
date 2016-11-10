package de.bioforscher.chemistry.physical.atoms;

import de.bioforscher.chemistry.descriptive.elements.ElementProvider;

import java.util.function.Predicate;

/**
 * Created by fkaiser on 10.11.16.
 */
public class AtomFilter {

    public static Predicate<Atom> isCarbon() {
        return atom -> atom.getElement().equals(ElementProvider.CARBON);
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
        return atom -> atom.getAtomName() == AtomName.CA;
    }

    public static Predicate<Atom> isBackbone() {
        return atom -> atom.getAtomName() == AtomName.N ||
                       atom.getAtomName() == AtomName.CA ||
                       atom.getAtomName() == AtomName.C ||
                       atom.getAtomName() == AtomName.O;
    }

    public static Predicate<Atom> isSidechain() {
        return isBackbone().negate();
    }
}