package de.bioforscher.singa.chemistry.descriptive.features;

import de.bioforscher.singa.chemistry.descriptive.features.implementations.DiffusivityFeature;
import de.bioforscher.singa.chemistry.descriptive.features.implementations.MolarMassFeature;

/**
 * The FeatureKind enumerates all features that can be explicitly annotated. This enum also provides the
 * {@link FeatureProvider} that is able to assign the feature to any {@link Featureable} Entity.
 *
 * @author cl
 */
public enum FeatureKind {

    /**
     * Molar mass is a physical property defined as the mass of a given substance divided by the amount of substance.
     *
     * The base SI unit for molar mass is kg/mol (mass/amount).
     */
    MOLAR_MASS(MolarMassFeature.getInstance()),

    /**
     * Diffusivity is a proportionality constant between the molar flux due to molecular
     * diffusion and the gradient in the concentration of the species (or the driving force for diffusion).
     *
     * The base SI unit for molar mass is m^2/s (length^2/time).
     */
    DIFFUSIVITY(DiffusivityFeature.getInstance());
    // SIMILES_STRING,
    // MEMBRANE_PERMEABILITY;

    /**
     * The {@link FeatureProvider} that is able to estimate or fetch this kind of feature.
     */
    private FeatureProvider provider;

    FeatureKind(FeatureProvider provider) {
        this.provider = provider;
    }

    /**
     * Return the {@link FeatureProvider} that is able to estimate or fetch this kind of feature.
     * @return The {@link FeatureProvider} that is able to estimate or fetch this kind of feature.
     */
    public FeatureProvider getProvider() {
        return this.provider;
    }

}
