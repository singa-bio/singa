package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationSchemeFactory;
import de.bioforscher.chemistry.physical.atoms.representations.RepresentationSchemeType;
import de.bioforscher.chemistry.physical.families.ResidueFamily;

import java.io.IOException;

/**
 * Utility methods for {@link Residue}s.
 *
 * @author fk
 */
public class Residues {

    /**
     * prevent instantiation
     */
    private Residues() {
    }

    /**
     * This creates a virtual beta carbon for glycine by superimposing alanine.
     *
     * @param residue The glycine {@link Residue} for which the virtual beta carbon should be calculated.
     * @return The virtual beta carbon of glycine.
     * @throws IOException If prototype alanine residue could not be read.
     */
    public static Atom createVirtualCBAtom(Residue residue) throws IOException {
        if (residue.getFamily() != ResidueFamily.GLYCINE) {
            throw new IllegalArgumentException("virtual beta carbon can only be calculated for glycine");
        }
        return RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.CB)
                .determineRepresentingAtom(residue);

    }
}
