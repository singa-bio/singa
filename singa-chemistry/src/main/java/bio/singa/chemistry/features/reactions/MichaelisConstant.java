package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.model.ScalableQuantityFeature;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

/**
 * The michaelis constant is an inverse measure of the substrate's affinity to the enzyme.
 *
 * @author cl
 */
public class MichaelisConstant extends ScalableQuantityFeature<MolarConcentration> {

    public static final String SYMBOL = "k_m";

    public MichaelisConstant(Quantity<MolarConcentration> molarConcentration, FeatureOrigin featureOrigin) {
        super(molarConcentration, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

    @Override
    public void scale(Quantity<Time> targetTimeScale, Quantity<Length> targetLengthScale) {
        // transform to correct concentration unit
        scaledQuantity = getFeatureContent().to(Environment.getConcentrationUnit());
        halfScaledQuantity = getFeatureContent().to(Environment.getConcentrationUnit());
    }
}
