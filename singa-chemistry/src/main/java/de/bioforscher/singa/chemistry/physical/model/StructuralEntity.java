package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

/**
 * A Structural Entity is anything that is able to be represented in a {@link Structure}. This can encompass anything
 * from a single {@link Atom} over AminoAcids to ChainFilter and Domains.
 * */
public interface StructuralEntity<T extends Node<T, Vector3D>> extends Node<T, Vector3D> {

}
