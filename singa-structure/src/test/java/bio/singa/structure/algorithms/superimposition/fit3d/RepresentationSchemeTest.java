package bio.singa.structure.algorithms.superimposition.fit3d;

import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationScheme;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeFactory;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.interfaces.AminoAcid;
import org.junit.jupiter.api.Test;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.ALANINE;
import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.TYROSINE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author fk
 */
class RepresentationSchemeTest {

    @Test
    void shouldRepresentAsAlphaCarbon() {
        AminoAcid alanine = StructuralFamilies.AminoAcids.getPrototype(ALANINE);
        RepresentationScheme alphaCarbonRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.ALPHA_CARBON);
        assertArrayEquals(alanine.getAtomByName("CA").get().getPosition().getElements(),
                alphaCarbonRepresentation.determineRepresentingAtom(alanine).getPosition().getElements(),
                1E-6);

    }

    @Test
    void shouldRepresentBetaCarbon() {
        AminoAcid alanine =  StructuralFamilies.AminoAcids.getPrototype(ALANINE);
        RepresentationScheme betaCarbonRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.BETA_CARBON);
        assertArrayEquals(alanine.getAtomByName("CB").get().getPosition().getElements(),
                betaCarbonRepresentation.determineRepresentingAtom(alanine).getPosition().getElements(),
                1E-6);

    }

    @Test
    void shouldRepresentCentroid() {
        AminoAcid tyrosine =  StructuralFamilies.AminoAcids.getPrototype(TYROSINE);
        RepresentationScheme centroidRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.CENTROID);
        assertArrayEquals(new double[]{0.032916666666666705, -0.1403333333333334, 0.04999999999999997},
                centroidRepresentation.determineRepresentingAtom(tyrosine).getPosition().getElements(),
                1E-6);
    }

    @Test
    void shouldRepresentLastHeavySidechain() {
        AminoAcid tyrosine =  StructuralFamilies.AminoAcids.getPrototype(TYROSINE);
        RepresentationScheme lastHavySidechainRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.LAST_HEAVY_SIDE_CHAIN);
        assertArrayEquals(tyrosine.getAtomByName("OH").get().getPosition().getElements(),
                lastHavySidechainRepresentation.determineRepresentingAtom(tyrosine).getPosition().getElements(),
                1E-6);
    }

    @Test
    void shouldRepresentSideChainCentroid() {
        AminoAcid tyrosine =  StructuralFamilies.AminoAcids.getPrototype(TYROSINE);
        RepresentationScheme sidechainCentroidRepresentation = RepresentationSchemeFactory.createRepresentationScheme(RepresentationSchemeType.SIDE_CHAIN_CENTROID);
        assertArrayEquals(new double[]{-0.22149999999999997, 1.09525, -0.906125},
                sidechainCentroidRepresentation.determineRepresentingAtom(tyrosine).getPosition().getElements(),
                1E-6);
    }
}