package de.bioforscher.mathematics.functions;

import de.bioforscher.mathematics.concepts.Ring;

/**
 * Created by Christoph on 09.08.2016.
 */
public interface FunctionComponent extends Ring<FunctionComponent> {

    FunctionComponent evaluate();

    double getValue();

}
