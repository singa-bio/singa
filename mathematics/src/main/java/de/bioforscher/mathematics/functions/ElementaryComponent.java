package de.bioforscher.mathematics.functions;

import de.bioforscher.mathematics.functions.modifiers.ElementaryModifier;
import de.bioforscher.mathematics.functions.modifiers.ElementaryModifiers;

/**
 * Created by Christoph on 09.08.2016.
 */
public class ElementaryComponent implements FunctionComponent {

    private ElementaryModifier modifier;
    private Variable variable;

    public ElementaryComponent(ElementaryModifier modifier, Variable variable) {
        this.modifier = modifier;
        this.variable = variable;
    }

    private ElementaryComponent(Variable oldVariable, double value) {
        this.variable = oldVariable;
        this.variable.setValue(value);
        this.modifier = ElementaryModifiers.identity();
    }

    @Override
    public FunctionComponent evaluate() {
        return new ElementaryComponent(this.variable, getValue());
    }

    @Override
    public double getValue() {
        return this.modifier.apply(this.variable);
    }

    @Override
    public FunctionComponent additivelyInvert() {
        return new ElementaryComponent(this.variable, -this.variable.getValue());
    }

    @Override
    public FunctionComponent subtract(FunctionComponent subtrahend) {
        return new ElementaryComponent(this.variable, this.variable.getValue() - subtrahend.getValue());
    }

    @Override
    public FunctionComponent multiply(FunctionComponent multiplicand) {
        return new ElementaryComponent(this.variable, this.variable.getValue() * multiplicand.getValue());
    }

    @Override
    public FunctionComponent add(FunctionComponent summand) {
        return new ElementaryComponent(this.variable, this.variable.getValue() + summand.getValue());
    }

    @Override
    public String toString() {
        return this.modifier.getStingRepresentation(this.variable);
    }
}
