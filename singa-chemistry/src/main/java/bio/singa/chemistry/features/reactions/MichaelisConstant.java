package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.quantities.MolarConcentration;

import javax.measure.Quantity;

/**
 * The michaelis constant is an inverse measure of the substrate's affinity to the enzyme.
 *
 * @author cl
 */
public class MichaelisConstant extends AbstractFeature<Quantity<MolarConcentration>> {

    public static final String SYMBOL = "k_m";

    public MichaelisConstant(Quantity<MolarConcentration> molarConcentration, FeatureOrigin featureOrigin) {
        super(molarConcentration, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
