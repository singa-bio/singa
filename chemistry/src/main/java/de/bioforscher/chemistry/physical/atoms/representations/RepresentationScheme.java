package de.bioforscher.chemistry.physical.atoms.representations;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;

/**
 * This represents a representation scheme of a {@link LeafSubstructure}. When applied to a {@link LeafSubstructure}
 * the concrete implementation tries to reduce all {@link Atom}s to a single artificial atom.
 *
 * @author fk
 */
public interface RepresentationScheme {
    /**
     * Determined the representing {@link Atom} for the concrete implementation.
     *
     * @param leafSubstructure The {@link LeafSubstructure} for which the one atom representation should be calculated.
     * @return The one-atom representation of the given {@link LeafSubstructure}.
     */
    Atom determineRepresentingAtom(LeafSubstructure<?, ?> leafSubstructure);
}
