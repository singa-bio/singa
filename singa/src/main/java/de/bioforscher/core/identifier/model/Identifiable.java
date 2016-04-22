package de.bioforscher.core.identifier.model;

/**
 * Created by Christoph on 18.04.2016.
 */
public interface Identifiable<IdentifierType extends Identifier> {

    IdentifierType getIdentifier();

}
