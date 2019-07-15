package bio.singa.chemistry.features;

import bio.singa.chemistry.features.identifiers.InChIKeyProvider;
import bio.singa.chemistry.features.identifiers.PDBLigandIdentiferProvider;
import bio.singa.chemistry.features.identifiers.PubChemIdentifierProvider;
import bio.singa.chemistry.features.identifiers.PubChemToChEBI;
import bio.singa.chemistry.features.molarmass.MolarMassProvider;
import bio.singa.chemistry.features.molarvolume.MolarVolumePredictor;
import bio.singa.features.exceptions.IllegalFeatureRequestException;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.PDBLigandIdentifier;
import bio.singa.features.identifiers.PubChemIdentifier;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureProvider;
import bio.singa.features.quantities.MolarVolume;
import bio.singa.structure.features.molarmass.MolarMass;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class FeatureProviderRegistry {

    private static FeatureProviderRegistry instance = new FeatureProviderRegistry();

    static {
        // identifiers
        addProviderForFeature(InChIKey.class, InChIKeyProvider.class);
        addProviderForFeature(ChEBIIdentifier.class, PubChemToChEBI.class);
        addProviderForFeature(PubChemIdentifier.class, PubChemIdentifierProvider.class);
        addProviderForFeature(PDBLigandIdentifier.class, PDBLigandIdentiferProvider.class);
        addProviderForFeature(MolarMass.class, MolarMassProvider.class);
        addProviderForFeature(MolarVolume.class, MolarVolumePredictor.class);
    }

    private final Map<Class<? extends Feature>, Class<? extends FeatureProvider>> featureRegistry;

    private FeatureProviderRegistry() {
        featureRegistry = new HashMap<>();
    }

    public static FeatureProviderRegistry getInstance() {
        if (instance == null) {
            synchronized (FeatureProviderRegistry.class) {
                instance = new FeatureProviderRegistry();
            }
        }
        return instance;
    }

    public static synchronized <FeatureType extends Feature, ProviderType extends FeatureProvider> void addProviderForFeature(Class<FeatureType> featureClass, Class<ProviderType> providerClass) {
        getInstance().featureRegistry.put(featureClass, providerClass);
    }

    public static <FeatureType extends Feature> FeatureProvider getProvider(Class<FeatureType> featureClass) {
        try {
            if (!getInstance().featureRegistry.containsKey(featureClass)) {
                featureClass.getDeclaredMethod("register").invoke(null);
            }
            return getInstance().featureRegistry.get(featureClass).newInstance();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalFeatureRequestException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalFeatureRequestException(featureClass, e);
        } catch (InstantiationException e) {
            throw new IllegalFeatureRequestException(featureClass, e);
        }
    }

}