package de.bioforscher.singa.structure.algorithms.superimposition.fit3d.representations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory for creating implementations of {@link RepresentationScheme}s.
 *
 * @author fk
 */
public abstract class RepresentationSchemeFactory {

    protected static final Logger logger = LoggerFactory.getLogger(RepresentationSchemeFactory.class);
    private static final RepresentationSchemeType FALLBACK_REPRESENTATION_SCHEME_TYPE = RepresentationSchemeType.CA;

    /**
     * prevent instantiation
     */
    private RepresentationSchemeFactory() {
    }

    /**
     * Creates a {@link RepresentationScheme} for the given {@link RepresentationSchemeType}.
     *
     * @param representationSchemeType The {@link RepresentationSchemeType} for which a {@link RepresentationScheme}
     * should be calculated.
     * @return The {@link RepresentationScheme} (defaults to {@link AbstractRepresentationScheme}) for unknown types.
     */
    public static RepresentationScheme createRepresentationScheme(RepresentationSchemeType representationSchemeType) {
        logger.debug("creating representation scheme for type {}", representationSchemeType);
        try {
            return representationSchemeType.getCompatibleRepresentationScheme().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            // fallback to default representation scheme
            logger.warn("failed to create representation scheme for type {}, defaulting to alpha carbon",
                    representationSchemeType, e);
            return new AlphaCarbonRepresentationScheme();
        }
    }
}
