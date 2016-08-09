package de.bioforscher.mathematics.functions;

import de.bioforscher.mathematics.functions.modifiers.TermModifier;
import de.bioforscher.mathematics.functions.modifiers.TermModifiers;

/**
 * Created by Christoph on 09.08.2016.
 */
public class Term implements FunctionComponent {

    private TermModifier modifier;
    private FunctionComponent component;

    public Term(FunctionComponent component) {
        this.component = component;
        this.modifier = TermModifiers.identity();
    }

    public Term(TermModifier modifier, FunctionComponent component) {
        this.modifier = modifier;
        this.component = component;
    }

    @Override
    public FunctionComponent evaluate() {
        return this.modifier.apply(this.component.evaluate());
    }

    @Override
    public double getValue() {
        return evaluate().getValue();
    }

    @Override
    public FunctionComponent additivelyInvert() {
        return this.component.additivelyInvert();
    }

    @Override
    public FunctionComponent subtract(FunctionComponent subtrahend) {
        return this.component.subtract(subtrahend);
    }

    @Override
    public FunctionComponent multiply(FunctionComponent multiplicand) {
        return this.component.multiply(multiplicand);
    }

    @Override
    public FunctionComponent add(FunctionComponent summand) {
        return this.component.evaluate().add(summand);
    }
}
