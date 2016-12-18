package de.bioforscher.chemistry.physical.atoms.representations;

import de.bioforscher.chemistry.physical.atoms.AtomName;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.chemistry.physical.leafes.Residue;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author fk
 */
public class RepresentationSchemeTest {

    @Test
    public void shouldRepresentAsAlphaCarbon() throws IOException {
        Residue alanine = ResidueFamily.ALANINE.getPrototype();
        RepresentationScheme alphaCarbonRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.CA);
        assertArrayEquals(alanine.getAlphaCarbon().getPosition().getElements(),
                alphaCarbonRepresentation.determineRepresentingAtom(alanine).getPosition().getElements(),
                1E-6);

    }

    @Test
    public void shouldRepresentBetaCarbon() throws IOException {
        Residue alanine = ResidueFamily.ALANINE.getPrototype();
        RepresentationScheme betaCarbonRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.CB);
        assertArrayEquals(alanine.getBetaCarbon().getPosition().getElements(),
                betaCarbonRepresentation.determineRepresentingAtom(alanine).getPosition().getElements(),
                1E-6);

    }

    @Test
    public void shouldRepresentCentroid() throws IOException {
        Residue alanine = ResidueFamily.TYROSINE.getPrototype();
        RepresentationScheme centroidRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.CENTROID);
        assertArrayEquals(new double[]{0.032916666666666705, -0.1403333333333334, 0.04999999999999997},
                centroidRepresentation.determineRepresentingAtom(alanine).getPosition().getElements(),
                1E-6);
    }

    @Test
    public void shouldRepresentLastHeavySidechain() throws IOException {
        Residue tyrosine = ResidueFamily.ALANINE.getPrototype();
        RepresentationScheme lastHavySidechainRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.LAST_HEAVY_SIDECHAIN);
        assertArrayEquals(tyrosine.getAtomByName(AtomName.OH).getPosition().getElements(),
                lastHavySidechainRepresentation.determineRepresentingAtom(tyrosine).getPosition().getElements(),
                1E-6);
    }

    @Test
    public void shouldRepresentSidechaiCentroid() throws IOException {
        Residue tyrosine = ResidueFamily.TYROSINE.getPrototype();
        RepresentationScheme sidechainCentroidRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.SIDECHAIN_CENTROID);
        System.out.println();
        System.out.println(sidechainCentroidRepresentation.determineRepresentingAtom(tyrosine));
        assertArrayEquals(new double[]{-0.22149999999999997, 1.09525, -0.906125},
                sidechainCentroidRepresentation.determineRepresentingAtom(tyrosine).getPosition().getElements(),
                1E-6);
    }
}