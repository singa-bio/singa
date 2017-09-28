package de.bioforscher.singa.chemistry.physical.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * @author cl
 */
public interface AtomContainer {

    List<Atom> getAllAtoms();

    Optional<Atom> getAtom(int atomIdentifier);

}
