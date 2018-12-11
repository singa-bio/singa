package bio.singa.features.model;

import bio.singa.features.units.UnitRegistry;

import javax.measure.Quantity;

/**
 * @author cl
 */
public abstract class ScalableQuantityFeature<QuantityType extends Quantity<QuantityType>> implements ScalableFeature<QuantityType> {

    private final Quantity<QuantityType> featureContent;
    private final Evidence evidence;

    protected Quantity<QuantityType> scaledQuantity;
    protected Quantity<QuantityType> halfScaledQuantity;

    public ScalableQuantityFeature(Quantity<QuantityType> featureContent, Evidence evidence) {
        this.featureContent = featureContent;
        this.evidence = evidence;
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scaleTime(featureContent);
        halfScaledQuantity = scaledQuantity.multiply(0.5);
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
    public Evidence getEvidence() {
        return evidence;
    }

    @Override
    public String getSymbol() {
        return null;
    }

    @Override
    public String toString() {
        return "Feature: " + (getSymbol() != null ? getSymbol() : getClass().getSimpleName()) + " = " + getFeatureContent() + " (" + getScaledQuantity() + ")";
    }
}
