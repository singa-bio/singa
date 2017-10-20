package de.bioforscher.singa.structure.model.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Everything that contains {@link Atom}s.
 *
 * @author cl
 */
public interface AtomContainer {

    /**
     * Returns all {@link Atom}s.
     *
     * @return All {@link Atom}s.
     */
    List<Atom> getAllAtoms();

    /**
     * Returns an {@link Optional} of the {@link Atom} with the given identifier. If no Atom with the identifier could
     * be found, an empty optional is returned.
     *
     * @param atomIdentifier The identifier of the atom.
     * @return An {@link Optional} encapsulating the {@link Atom}.
     */
    Optional<Atom> getAtom(Integer atomIdentifier);

    /**
     * Removes an atom from this container.
     * @param atomIdentifier The identifier of the atom to be removed.
     */
    void removeAtom(Integer atomIdentifier);

}
