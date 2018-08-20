package bio.singa.simulation.model.modules;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.ScalableFeature;
import bio.singa.simulation.model.modules.concentration.ModuleState;

import javax.measure.Quantity;
import java.util.Collection;
import java.util.Set;

/**
 * @author cl
 */
public interface UpdateModule {

    void calculateUpdates();

    ModuleState getState();

    void resetState();

    void scaleScalableFeatures();

    Set<Class<? extends Feature>> getRequiredFeatures();

    <FeatureContentType extends Quantity<FeatureContentType>> Quantity<FeatureContentType> getScaledFeature(Class<? extends ScalableFeature<FeatureContentType>> featureClass);

    Collection<Feature<?>> getFeatures();

    void optimizeTimeStep();

    Set<ChemicalEntity> getReferencedEntities();

    void checkFeatures();

    String getIdentifier();

    String getStringForProtocol();

    void onReset();

    void onCompletion();

}
