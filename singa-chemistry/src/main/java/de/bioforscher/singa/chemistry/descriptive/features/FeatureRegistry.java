package de.bioforscher.singa.chemistry.descriptive.features;

import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMassProvider;
import de.bioforscher.singa.chemistry.descriptive.features.molarvolume.MolarVolumePredictor;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.IllegalFeatureRequestException;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import de.bioforscher.singa.structure.features.molarvolume.MolarVolume;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class FeatureRegistry {

    private static FeatureRegistry instance = new FeatureRegistry();

    static {
        addProviderForFeature(MolarMass.class, MolarMassProvider.class);
        addProviderForFeature(MolarVolume.class, MolarVolumePredictor.class);
    }

    private final Map<Class<? extends Feature>, Class<? extends FeatureProvider>> featureRegistry;

    private FeatureRegistry() {
        featureRegistry = new HashMap<>();
    }

    public static FeatureRegistry getInstance() {
        if (instance == null) {
            synchronized (FeatureRegistry.class) {
                instance = new FeatureRegistry();
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
