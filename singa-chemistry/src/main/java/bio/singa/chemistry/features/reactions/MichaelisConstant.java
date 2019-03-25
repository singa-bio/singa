package bio.singa.chemistry.features.reactions;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QuantitativeFeature;
import bio.singa.features.quantities.MolarConcentration;

import javax.measure.Quantity;

/**
 * The michaelis constant is an inverse measure of the substrate's affinity to the enzyme.
 *
 * @author cl
 */
public class MichaelisConstant extends QuantitativeFeature<MolarConcentration> {

    public static final String SYMBOL = "k_m";

    public MichaelisConstant(Quantity<MolarConcentration> molarConcentration, Evidence evidence) {
        super(molarConcentration, evidence);
    }

    public MichaelisConstant(Quantity<MolarConcentration> quantity) {
        super(quantity);
    }

    @Override
    public String getDescriptor() {
        return SYMBOL;
    }
    
}
