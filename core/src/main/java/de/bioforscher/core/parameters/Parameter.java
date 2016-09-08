package de.bioforscher.core.parameters;

import de.bioforscher.core.utility.Bounded;
import de.bioforscher.core.utility.Nameable;

public interface Parameter<Type extends Comparable<Type>> extends Nameable, Bounded<Type> {

}
