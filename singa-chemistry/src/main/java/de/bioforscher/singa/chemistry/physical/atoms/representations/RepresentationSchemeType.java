package de.bioforscher.singa.chemistry.physical.atoms.representations;

/**
 * Defines the types for which adequately {@link RepresentationScheme}s are defined.
 * <p>
 * @author fk
 */
public enum RepresentationSchemeType {

    CA(AlphaCarbonRepresentationScheme.class, "CA"),
    CB(BetaCarbonRepresentationScheme.class, "CB"),
    CENTROID(CentroidRepresentationScheme.class, "CO"),
    LAST_HEAVY_SIDECHAIN(LastHeavySidechainRepresentationScheme.class, "LH"),
    SIDECHAIN_CENTROID(SidechainCentroidRepresentationScheme.class, "SC");

    private Class<? extends AbstractRepresentationScheme> compatibleRepresentationScheme;
    private String atomNomenclature;

    RepresentationSchemeType(Class<? extends AbstractRepresentationScheme> compatibleRepresentationScheme,
                             String atomNomenclature) {
        this.compatibleRepresentationScheme = compatibleRepresentationScheme;
        this.atomNomenclature = atomNomenclature;
    }

    public Class<? extends AbstractRepresentationScheme> getCompatibleRepresentationScheme() {
        return this.compatibleRepresentationScheme;
    }

    public String getAtomNameString() {
        return this.atomNomenclature;
    }
}
