package bio.singa.features.model;

import bio.singa.features.units.UnitRegistry;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public abstract class QuantitativeFeature<FeatureContent extends Quantity<FeatureContent>> extends AbstractFeature<Quantity<FeatureContent>> {

    public QuantitativeFeature(Quantity<FeatureContent> quantity, List<Evidence> evidence) {
        super(UnitRegistry.convert(quantity), evidence);
        FeatureRegistry.addQuantitativeFeature(this);
    }

    public QuantitativeFeature(Quantity<FeatureContent> quantity, Evidence evidence) {
        super(UnitRegistry.convert(quantity), evidence);
        FeatureRegistry.addQuantitativeFeature(this);
    }

    public QuantitativeFeature(Quantity<FeatureContent> quantity) {
        super(UnitRegistry.convert(quantity));
        FeatureRegistry.addQuantitativeFeature(this);
    }

}
