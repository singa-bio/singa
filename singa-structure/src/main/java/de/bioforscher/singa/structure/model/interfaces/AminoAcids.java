package de.bioforscher.singa.structure.model.interfaces;

import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeFactory;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import de.bioforscher.singa.structure.model.graph.families.AminoAcidFamily;

/**
 * Utility methods for {@link AminoAcid}s.
 *
 * @author fk
 */
public class AminoAcids {

    /**
     * prevent instantiation
     */
    private AminoAcids() {
    }

    /**
     * This creates a virtual beta carbon for glycine by superimposing alanine.
     *
     * @param aminoAcid The glycine {@link AminoAcid} for which the virtual beta carbon should be calculated.
     * @return The virtual beta carbon of glycine.
     */
    public static Atom createVirtualCBAtom(AminoAcid aminoAcid) {
        if (aminoAcid.getFamily() != AminoAcidFamily.GLYCINE) {
            throw new IllegalArgumentException("virtual beta carbon can only be calculated for glycine");
        }
        return RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.CB)
                .determineRepresentingAtom(aminoAcid);

    }
}
