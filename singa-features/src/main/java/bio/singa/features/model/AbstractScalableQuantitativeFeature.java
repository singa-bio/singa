package bio.singa.features.model;

import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public abstract class AbstractScalableQuantitativeFeature<FeatureContent extends Quantity<FeatureContent>> extends AbstractFeature<Quantity<FeatureContent>> implements QuantitativeFeature<FeatureContent> {

    protected double scaledQuantity;
    protected double halfScaledQuantity;

    public AbstractScalableQuantitativeFeature(Quantity<FeatureContent> quantity, List<Evidence> evidence) {
        super(quantity, evidence);
        FeatureRegistry.addScalableQuantitativeFeatures(this);
    }

    public AbstractScalableQuantitativeFeature(Quantity<FeatureContent> quantity, Evidence evidence) {
        super(quantity, evidence);
        FeatureRegistry.addScalableQuantitativeFeatures(this);
    }

    public AbstractScalableQuantitativeFeature(Quantity<FeatureContent> quantity) {
        super(quantity);
        FeatureRegistry.addScalableQuantitativeFeatures(this);
    }

    public double getScaledQuantity() {
        return scaledQuantity;
    }

    public double getHalfScaledQuantity() {
        return halfScaledQuantity;
    }

    @Override
    public void setAlternativeContent(int index) {
        double value = getAlternativeContents().get(index).getValue().doubleValue();
        featureContent = Quantities.getQuantity(value, baseContent.getUnit());
        scale();
    }

    public void addAlternativeValue(Double alternativeValue) {
        super.addAlternativeContent(Quantities.getQuantity(alternativeValue, baseContent.getUnit()));
    }

    public void scale() {
        scaledQuantity = UnitRegistry.scale(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

    public void scale(double factor) {
        scaledQuantity *= factor;
        halfScaledQuantity = scaledQuantity * 0.5;
    }

}
