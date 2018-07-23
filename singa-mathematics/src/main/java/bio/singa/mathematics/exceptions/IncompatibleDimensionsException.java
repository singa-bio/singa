package bio.singa.mathematics.exceptions;

import bio.singa.mathematics.concepts.MultiDimensional;

public class IncompatibleDimensionsException extends RuntimeException {

    private static final long serialVersionUID = 2227763988593733302L;

    public IncompatibleDimensionsException(MultiDimensional<?> firstMultiDimensionalObject, MultiDimensional<?> secondMultiDimensionalObject) {
        super("This operation cannot be performed for dimension "
                + firstMultiDimensionalObject.getDimensionAsString() + " and dimension "
                + secondMultiDimensionalObject.getDimensionAsString() + ".");
    }

}
