package de.bioforscher.chemistry.algorithms.superimposition;

/**
 * Created by fkaiser on 22.11.16.
 */
public class SubStructureSuperimpositionException extends Throwable {
    public SubStructureSuperimpositionException() {
    }

    public SubStructureSuperimpositionException(String message) {
        super(message);
    }

    public SubStructureSuperimpositionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubStructureSuperimpositionException(Throwable cause) {
        super(cause);
    }

    public SubStructureSuperimpositionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
