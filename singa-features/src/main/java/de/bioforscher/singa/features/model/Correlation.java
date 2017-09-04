package de.bioforscher.singa.features.model;

/**
 * @author cl
 */
public interface Correlation<FeatureType extends Feature<?>>  {

    <FeatureableType extends Featureable> FeatureType predict(FeatureableType featureable);

}
