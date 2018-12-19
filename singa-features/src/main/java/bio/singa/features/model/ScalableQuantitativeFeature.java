package bio.singa.features.model;

import bio.singa.features.units.UnitRegistry;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public abstract class ScalableQuantitativeFeature<FeatureContent extends Quantity<FeatureContent>> extends AbstractFeature<Quantity<FeatureContent>> {

    protected double scaledQuantity;
    protected double halfScaledQuantity;

    public ScalableQuantitativeFeature(Quantity<FeatureContent> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
        FeatureRegistry.addScalableQuantitativeFeatures(this);
    }

    public ScalableQuantitativeFeature(Quantity<FeatureContent> quantity, Evidence evidence) {
        super(quantity, evidence);
        FeatureRegistry.addScalableQuantitativeFeatures(this);
    }

    public ScalableQuantitativeFeature(Quantity<FeatureContent> quantity) {
        super(quantity);
        FeatureRegistry.addScalableQuantitativeFeatures(this);
    }

    public double getScaledQuantity() {
        return scaledQuantity;
    }

    public double getHalfScaledQuantity() {
        return halfScaledQuantity;
    }

    public void scale() {
        scaledQuantity = UnitRegistry.scaleTime(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

    public void scale(double factor) {
        scaledQuantity *= factor;
        halfScaledQuantity = scaledQuantity * 0.5;
    }

}
