package de.bioforscher.mathematics.functions.modifiers;

import de.bioforscher.mathematics.functions.Variable;

import java.util.function.Function;

/**
 * Created by Christoph on 09.08.2016.
 */
public interface ElementaryModifier extends Function<Variable, Double> {

    String getStingRepresentation(Variable variable);

}
