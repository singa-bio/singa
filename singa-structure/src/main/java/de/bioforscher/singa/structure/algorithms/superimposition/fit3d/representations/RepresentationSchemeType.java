package de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations;

/**
 * Defines the types for which adequately {@link RepresentationScheme}s are defined. <p>
 *
 * @author fk
 */
public enum RepresentationSchemeType {

    CA(AlphaCarbonRepresentationScheme.class, "CA"),
    CB(BetaCarbonRepresentationScheme.class, "CB"),
    CENTROID(CentroidRepresentationScheme.class, "CO"),
    LAST_HEAVY_SIDE_CHAIN(LastHeavySidechainRepresentationScheme.class, "LH"),
    SIDE_CHAIN_CENTROID(SideChainCentroidRepresentationScheme.class, "SC");

    private Class<? extends AbstractRepresentationScheme> compatibleRepresentationScheme;
    private String atomNomenclature;

    RepresentationSchemeType(Class<? extends AbstractRepresentationScheme> compatibleRepresentationScheme,
                             String atomNomenclature) {
        this.compatibleRepresentationScheme = compatibleRepresentationScheme;
        this.atomNomenclature = atomNomenclature;
    }

    public Class<? extends AbstractRepresentationScheme> getCompatibleRepresentationScheme() {
        return compatibleRepresentationScheme;
    }

    public String getAtomNameString() {
        return atomNomenclature;
    }
}
