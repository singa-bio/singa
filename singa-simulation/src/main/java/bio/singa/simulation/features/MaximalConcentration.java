package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.quantities.MolarConcentration;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class MaximalConcentration extends AbstractFeature<Quantity<MolarConcentration>> {

    private static final String SYMBOL = "cMax";

    public MaximalConcentration(Quantity<MolarConcentration> concentration, FeatureOrigin featureOrigin) {
        super(concentration, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
