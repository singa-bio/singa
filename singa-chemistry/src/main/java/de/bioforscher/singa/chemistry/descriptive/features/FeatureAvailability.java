package de.bioforscher.singa.chemistry.descriptive.features;


import de.bioforscher.singa.chemistry.descriptive.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.Species;

/**
 * @author cl
 */
public enum FeatureAvailability {

    SPECIES(Species.class),
    ENZYME(Enzyme.class);
    // MEMBRANE,
    // MEMBRANE_PROTEIN();

    Class featureClass;

    FeatureAvailability(Class featureClass) {
        this.featureClass = featureClass;
    }

    public Class getFeatureClass() {
        return this.featureClass;
    }
}
