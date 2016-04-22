package de.bioforscher.mathematics.exceptions;

public class MalformedMatrixException extends RuntimeException {

    private static final long serialVersionUID = -3029571561860254855L;

    public MalformedMatrixException(double[][] values, int rowNumber) {
        super("The values " + values + " are not wellformed in order to create a Matrix. The row number " + rowNumber + " seems to be invalid.");
    }

    public MalformedMatrixException(double[][] values) {
        super("The values " + values + " are not wellformed in order to create a Matrix. At least one row seems to be invalid.");
    }


}
