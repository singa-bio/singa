package de.bioforscher.singa.core.parameters;

import de.bioforscher.singa.core.utility.Bounded;
import de.bioforscher.singa.core.utility.Nameable;

public interface Parameter<Type extends Comparable<Type>> extends Nameable, Bounded<Type> {

}
