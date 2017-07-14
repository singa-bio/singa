package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.mathematics.graphs.model.Graph;

import java.util.List;

/**
 * @author cl
 */
public interface Substructure<SubstructureType extends Substructure<SubstructureType, IdentifierType>, IdentifierType>
        extends Graph<Atom, Bond, Integer>, StructuralEntity<SubstructureType, IdentifierType> {

    List<Atom> getAllAtoms();

    SubstructureType getCopy();
}