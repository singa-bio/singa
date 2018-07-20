package bio.singa.core.parameters;

import bio.singa.core.utility.Bounded;
import bio.singa.core.utility.Nameable;

public interface Parameter<Type extends Comparable<Type>> extends Nameable, Bounded<Type> {

}
