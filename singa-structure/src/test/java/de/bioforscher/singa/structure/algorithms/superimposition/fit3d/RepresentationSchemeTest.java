package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;

import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeFactory;
import de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author fk
 */
public class RepresentationSchemeTest {

    @Test
    public void shouldRepresentAsAlphaCarbon() throws IOException {
        AminoAcid alanine = AminoAcidFamily.ALANINE.getPrototype();
        RepresentationScheme alphaCarbonRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.CA);
        assertArrayEquals(alanine.getAtomByName("CA").get().getPosition().getElements(),
                alphaCarbonRepresentation.determineRepresentingAtom(alanine).getPosition().getElements(),
                1E-6);

    }

    @Test
    public void shouldRepresentBetaCarbon() throws IOException {
        AminoAcid alanine = AminoAcidFamily.ALANINE.getPrototype();
        RepresentationScheme betaCarbonRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.CB);
        assertArrayEquals(alanine.getAtomByName("CB").get().getPosition().getElements(),
                betaCarbonRepresentation.determineRepresentingAtom(alanine).getPosition().getElements(),
                1E-6);

    }

    @Test
    public void shouldRepresentCentroid() throws IOException {
        AminoAcid tyrosine = AminoAcidFamily.TYROSINE.getPrototype();
        RepresentationScheme centroidRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.CENTROID);
        assertArrayEquals(new double[]{0.032916666666666705, -0.1403333333333334, 0.04999999999999997},
                centroidRepresentation.determineRepresentingAtom(tyrosine).getPosition().getElements(),
                1E-6);
    }

    @Test
    public void shouldRepresentLastHeavySidechain() throws IOException {
        AminoAcid tyrosine = AminoAcidFamily.TYROSINE.getPrototype();
        RepresentationScheme lastHavySidechainRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.LAST_HEAVY_SIDE_CHAIN);
        assertArrayEquals(tyrosine.getAtomByName("OH").get().getPosition().getElements(),
                lastHavySidechainRepresentation.determineRepresentingAtom(tyrosine).getPosition().getElements(),
                1E-6);
    }

    @Test
    public void shouldRepresentSideChainCentroid() throws IOException {
        AminoAcid tyrosine = AminoAcidFamily.TYROSINE.getPrototype();
        RepresentationScheme sidechainCentroidRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.SIDE_CHAIN_CENTROID);
        assertArrayEquals(new double[]{-0.22149999999999997, 1.09525, -0.906125},
                sidechainCentroidRepresentation.determineRepresentingAtom(tyrosine).getPosition().getElements(),
                1E-6);
    }
}