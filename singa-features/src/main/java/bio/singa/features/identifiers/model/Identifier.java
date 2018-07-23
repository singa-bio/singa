package bio.singa.features.identifiers.model;

import bio.singa.features.model.Feature;

/**
 * The identifier is used to identify objects that have the same structure.
 *
 * @author cl
 */
public interface Identifier<IdentifierType> extends Feature<IdentifierType> {

    /**
     * Returns the string representation of the identifier.
     *
     * @return The string representation of the identifier.
     */
    String getIdentifier();

}
