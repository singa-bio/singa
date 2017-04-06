package de.bioforscher.singa.chemistry.physical.leafes;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationSchemeFactory;
import de.bioforscher.singa.chemistry.physical.atoms.representations.RepresentationSchemeType;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;

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
