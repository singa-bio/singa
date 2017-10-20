package de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations;

import de.bioforscher.singa.structure.model.interfaces.Atom;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;

/**
 * This represents a representation scheme of a {@link GraphLeafSubstructure}. When applied to a {@link GraphLeafSubstructure}
 * the concrete implementation tries to reduce all {@link GraphAtom}s to a single artificial atom.
 *
 * @author fk
 */
public interface RepresentationScheme {
    /**
     * Determined the representing {@link GraphAtom} for the concrete implementation.
     *
     * @param leafSubstructure The {@link GraphLeafSubstructure} for which the one atom representation should be calculated.
     * @return The one-atom representation of the given {@link GraphLeafSubstructure}.
     */
    Atom determineRepresentingAtom(LeafSubstructure<?> leafSubstructure);

    /**
     * Returns the actual {@link RepresentationSchemeType} of this {@link RepresentationScheme}.
     *
     * @return The actual type of this {@link RepresentationScheme}.
     */
    RepresentationSchemeType getType();
}
