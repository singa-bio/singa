package de.bioforscher.mathematics.functions.modifiers;

import de.bioforscher.mathematics.functions.FunctionComponent;

/**
 * Created by Christoph on 09.08.2016.
 */
public class TermSquare implements TermModifier {

    @Override
    public FunctionComponent apply(FunctionComponent functionComponent) {
        return functionComponent.evaluate().multiply(functionComponent.evaluate());
    }

}
