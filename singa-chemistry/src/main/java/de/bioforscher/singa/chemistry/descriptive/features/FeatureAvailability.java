package de.bioforscher.singa.chemistry.descriptive.features;


import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.Protein;
import de.bioforscher.singa.chemistry.descriptive.Species;

/**
 * This enumeration provides all classes that might be annotated by any {@link Feature}. This enum is used to define
 * what features might be assigned to what entities.
 *
 * @author cl
 */
public enum FeatureAvailability {

    /**
     * {@link Species} as implementation of {@link ChemicalEntity}.
     */
    SPECIES(Species.class),

    /**
     * {@link Enzyme} as implementation of {@link ChemicalEntity}.
     */
    ENZYME(Enzyme.class),

    /**
     * {@link Protein} as implementation of {@link ChemicalEntity}.
     */
    PROTEIN(Protein.class);

    // MEMBRANE,
    // MEMBRANE_PROTEIN();

    /**
     * The referenced class.
     */
    Class featureClass;

    FeatureAvailability(Class featureClass) {
        this.featureClass = featureClass;
    }

    /**
     * Returns the referenced class.
     * @return The referenced class.
     */
    public Class getFeatureClass() {
        return this.featureClass;
    }
}
