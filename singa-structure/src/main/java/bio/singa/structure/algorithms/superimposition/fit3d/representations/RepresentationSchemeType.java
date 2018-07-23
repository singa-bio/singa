package bio.singa.structure.algorithms.superimposition.fit3d.representations;

/**
 * Defines the types for which adequately {@link RepresentationScheme}s are defined. <p>
 *
 * @author fk
 */
public enum RepresentationSchemeType {

    ALPHA_CARBON(AlphaCarbonRepresentationScheme.class, "CA", "alpha carbon"),
    BETA_CARBON(BetaCarbonRepresentationScheme.class, "CB", "beta carbon"),
    CENTROID(CentroidRepresentationScheme.class, "CO", "all-atom centroid"),
    LAST_HEAVY_SIDE_CHAIN(LastHeavySidechainRepresentationScheme.class, "LH", "last heavy side chain atom"),
    SIDE_CHAIN_CENTROID(SideChainCentroidRepresentationScheme.class, "SC", "side chain centroid");

    private final Class<? extends AbstractRepresentationScheme> compatibleRepresentationScheme;
    private final String atomNomenclature;
    private final String description;

    RepresentationSchemeType(Class<? extends AbstractRepresentationScheme> compatibleRepresentationScheme,
                             String atomNomenclature, String description) {
        this.compatibleRepresentationScheme = compatibleRepresentationScheme;
        this.atomNomenclature = atomNomenclature;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends AbstractRepresentationScheme> getCompatibleRepresentationScheme() {
        return compatibleRepresentationScheme;
    }

    public String getAtomNameString() {
        return atomNomenclature;
    }
}
