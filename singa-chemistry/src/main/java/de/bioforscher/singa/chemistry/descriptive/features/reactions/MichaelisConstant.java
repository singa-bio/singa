package de.bioforscher.singa.chemistry.descriptive.features.reactions;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.quantities.MolarConcentration;

import javax.measure.Quantity;

/**
 * The michaelis constant is an inverse measure of the substrate's affinity to the enzyme.
 *
 * @author cl
 */
public class MichaelisConstant extends AbstractFeature<Quantity<MolarConcentration>> {

    public static final String SYMBOL = "k_m";

    public MichaelisConstant(Quantity<MolarConcentration> molarConcentrationQuantity, FeatureOrigin featureOrigin) {
        super(molarConcentrationQuantity, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
