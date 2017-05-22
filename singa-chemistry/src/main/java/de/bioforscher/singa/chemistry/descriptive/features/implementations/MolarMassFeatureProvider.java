package de.bioforscher.singa.chemistry.descriptive.features.implementations;

import de.bioforscher.singa.chemistry.descriptive.features.Feature;
import de.bioforscher.singa.chemistry.descriptive.features.FeatureProvider;
import de.bioforscher.singa.chemistry.descriptive.features.Featureable;
import de.bioforscher.singa.chemistry.descriptive.features.databases.ChEBIDatabase;
import de.bioforscher.singa.chemistry.descriptive.features.databases.UniProtDatabase;
import de.bioforscher.singa.units.features.molarmass.MolarMass;

import java.util.EnumSet;

import static de.bioforscher.singa.chemistry.descriptive.features.FeatureAvailability.*;
import static de.bioforscher.singa.chemistry.descriptive.features.FeatureKind.MOLAR_MASS;

/**
 * @author cl
 */
public class MolarMassFeatureProvider extends FeatureProvider {

    private static MolarMassFeatureProvider instance = new MolarMassFeatureProvider();

    private MolarMassFeatureProvider() {
        setAvailabilities(EnumSet.of(SPECIES, PROTEIN, ENZYME));
        setProvidedFeature(MOLAR_MASS);
    }

    public static MolarMassFeatureProvider getInstance() {
        if (instance == null) {
            synchronized (MolarMassFeatureProvider.class) {
                instance = new MolarMassFeatureProvider();
            }
        }
        return instance;
    }

    @Override
    protected <FeaturableType extends Featureable> Feature<MolarMass> getFeatureFor(FeaturableType featureable) {
        // mass is parsed from databases
        Feature<MolarMass> feature = new Feature<>(MOLAR_MASS);
        if (featureable.getClass().equals(SPECIES.getFeatureClass())) {
            // use ChEBI
            feature.setQuantity(ChEBIDatabase.fetchMolarMass(featureable));
            feature.setDescriptor(ChEBIDatabase.getInstance());
        } else if (featureable.getClass().equals(ENZYME.getFeatureClass()) ||
                featureable.getClass().equals(PROTEIN.getFeatureClass())) {
            // use UniProt
            feature.setQuantity(UniProtDatabase.fetchMolarMass(featureable));
            feature.setDescriptor(UniProtDatabase.getInstance());
        }
        return feature;
    }



}
