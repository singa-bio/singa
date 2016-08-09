package de.bioforscher.mathematics.functions.modifiers;

import de.bioforscher.mathematics.functions.Variable;

/**
 * Created by Christoph on 09.08.2016.
 */
public class ElementarySquare implements ElementaryModifier {

    @Override
    public Double apply(Variable variable) {
        return variable.getValue() * variable.getValue();
    }

    @Override
    public String getStingRepresentation(Variable variable) {
        return variable.toString()+"^2";
    }
}
