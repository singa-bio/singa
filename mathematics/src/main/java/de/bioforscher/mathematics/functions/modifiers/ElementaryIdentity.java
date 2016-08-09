package de.bioforscher.mathematics.functions.modifiers;

import de.bioforscher.mathematics.functions.Variable;

/**
 * Created by Christoph on 09.08.2016.
 */
public class ElementaryIdentity implements ElementaryModifier {
    @Override
    public String getStingRepresentation(Variable variable) {
        return variable.toString();
    }

    @Override
    public Double apply(Variable variable) {
        return variable.getValue();
    }
}
