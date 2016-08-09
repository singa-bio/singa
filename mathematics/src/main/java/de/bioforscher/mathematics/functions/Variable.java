package de.bioforscher.mathematics.functions;

import de.bioforscher.core.utility.Nameable;

/**
 * Created by Christoph on 09.08.2016.
 */
public class Variable implements Nameable {

    private final String name;
    private Double value;

    public Variable(String name) {
        this.name = name;
    }

    public Variable(String name, Double value) {
        this(name);
        this.value = value;
    }

    public double getValue() {
        if (this.value != null) {
            return this.value;
        } else {
            throw new IllegalStateException("Unable to retrieve value. Probably no value has been set.");
        }

    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name+"["+this.value+"]";
    }
}
