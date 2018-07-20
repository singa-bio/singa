package bio.singa.features.model;

import javax.measure.Quantity;

/**
 * @author cl
 */
public abstract class ScalableQuantityFeature<QuantityType extends Quantity<QuantityType>> implements ScalableFeature<QuantityType> {

    private final Quantity<QuantityType> featureContent;
    private final FeatureOrigin featureOrigin;

    protected Quantity<QuantityType> scaledQuantity;
    protected Quantity<QuantityType> halfScaledQuantity;

    public ScalableQuantityFeature(Quantity<QuantityType> featureContent, FeatureOrigin featureOrigin) {
        this.featureContent = featureContent;
        this.featureOrigin = featureOrigin;
    }

    @Override
    public Quantity<QuantityType> getScaledQuantity() {
        return scaledQuantity;
    }

    @Override
    public Quantity<QuantityType> getHalfScaledQuantity() {
        return halfScaledQuantity;
    }

    @Override
    public Quantity<QuantityType> getFeatureContent() {
        return featureContent;
    }

    @Override
    public FeatureOrigin getFeatureOrigin() {
        return featureOrigin;
    }

    @Override
    public String getSymbol() {
        return null;
    }

}
