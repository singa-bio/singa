package de.bioforscher.mathematics.functions.modifiers;

/**
 * Created by Christoph on 09.08.2016.
 */
public class TermModifiers {

    public static TermModifier square() {
        return new TermSquare();
    }

    public static TermModifier identity() {
        return new TermIdentity();
    }

}
