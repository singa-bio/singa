package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.mathematics.graphs.model.Graph;

import java.util.List;

/**
 * @author cl
 */
public interface Substructure<SubstructureType extends Substructure<SubstructureType>>
        extends Graph<Atom, Bond>, StructuralEntity<SubstructureType>{

    List<Atom> getAllAtoms();

    SubstructureType getCopy();
}