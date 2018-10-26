package bio.singa.simulation.features;

import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.model.ScalableQuantityFeature;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import static tec.uom.se.AbstractUnit.ONE;

/**
 * @author cl
 */
public class AppliedScale extends ScalableQuantityFeature<Dimensionless> {

    public static final String SYMBOL = "scale";

    private Quantity<Time> previousTimeStep;

    public AppliedScale(double scale, FeatureOrigin featureOrigin) {
        super(Quantities.getQuantity(scale, ONE), featureOrigin);
    }

    public AppliedScale() {
        super(Quantities.getQuantity(1.0, ONE), FeatureOrigin.MANUALLY_ANNOTATED);
    }

    @Override
    public Quantity<Dimensionless> getScaledQuantity() {
        return scaledQuantity;
    }

    @Override
    public Quantity<Dimensionless> getHalfScaledQuantity() {
        return halfScaledQuantity;
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
