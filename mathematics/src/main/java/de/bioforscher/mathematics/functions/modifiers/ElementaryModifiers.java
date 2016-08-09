package de.bioforscher.mathematics.functions.modifiers;

/**
 * Created by Christoph on 09.08.2016.
 */
public class ElementaryModifiers {

    public static ElementaryModifier square() {
        return new ElementarySquare();
    }

    public static ElementaryModifier identity() {
        return new ElementaryIdentity();
    }

}
