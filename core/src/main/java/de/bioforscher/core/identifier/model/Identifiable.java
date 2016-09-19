package de.bioforscher.core.identifier.model;

/**
 * The Identifiable interface specifies that, Objects implementing this interface can be identified by a certain
 * {@link Identifier}.
 *
 * @param <IdentifierType> The @link Identifier}, by which this Object is identified.
 * @author cl
 */
public interface Identifiable<IdentifierType extends Identifier> {

    /**
     * Returns the {@link Identifier}.
     * @return The {@link Identifier}.
     */
    IdentifierType getIdentifier();

}
