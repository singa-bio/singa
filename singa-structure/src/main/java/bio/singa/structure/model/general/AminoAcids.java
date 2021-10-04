package bio.singa.structure.model.general;

import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeFactory;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Atom;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.GLYCINE;

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
        if (aminoAcid.getFamily() != GLYCINE) {
            throw new IllegalArgumentException("virtual beta carbon can only be calculated for glycine");
        }
        return RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.BETA_CARBON)
                .determineRepresentingAtom(aminoAcid);
    }
}
