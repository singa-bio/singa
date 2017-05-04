package de.bioforscher.singa.chemistry.descriptive.features;

import de.bioforscher.singa.chemistry.descriptive.features.implementations.DiffusivityFeature;
import de.bioforscher.singa.chemistry.descriptive.features.implementations.MolarMassFeature;

/**
 * @author cl
 */
public enum FeatureKind {

    MOLAR_MASS(MolarMassFeature.getInstance()),
    // SIMILES_STRING,
    DIFFUSIVITY(DiffusivityFeature.getInstance());
    // MEMBRANE_PERMEABILITY;

    private FeatureProvider provider;

    FeatureKind(FeatureProvider provider) {
        this.provider = provider;
    }

    public FeatureProvider getProvider() {
        return this.provider;
    }

}
