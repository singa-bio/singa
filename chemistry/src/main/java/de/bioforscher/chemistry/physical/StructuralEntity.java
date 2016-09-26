package de.bioforscher.chemistry.physical;

import de.bioforscher.mathematics.graphs.model.Node;
import de.bioforscher.mathematics.vectors.Vector3D;

/**
 * A Structural Entity is anything that is able to be represented in a {@link Structure}. This can encompass anything
 * from a single {@link Atom} over Residues to Chains and Domains.
 * */
public interface StructuralEntity<T extends Node<T, Vector3D>> extends Node<T, Vector3D> {

}
