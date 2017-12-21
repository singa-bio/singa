package de.bioforscher.singa.features.model;

import java.util.Objects;

/**
 * @author cl
 */
public class IllegalFeatureRequestException extends RuntimeException {

    private static final long serialVersionUID = 114131020476902252L;

    public IllegalFeatureRequestException(Class<? extends Feature> featureClass, InstantiationException cause) {
        super("Could not instantiate any FeatureProvider for the Feature " + featureClass.getSimpleName() + ". An " +
                "empty constructor should be accessible from the FeatureRegistry.", Objects.requireNonNull(cause));
    }

    public IllegalFeatureRequestException(Class<? extends Feature> featureClass, NoSuchMethodException cause) {
        super("Could register the Feature " + featureClass.getSimpleName() + ". Be sure the register the Feature " +
                "manually by calling the addProviderForFeature() method or provide a static register() method in the " +
                "Features' class.", Objects.requireNonNull(cause));
    }

    /**
     * Constructs an instance of this class.
     *
     * @param cause the {@code IOException}
     * @throws NullPointerException if the cause is {@code null}
     */
    public IllegalFeatureRequestException(Exception cause) {
        super(Objects.requireNonNull(cause));
    }

}
