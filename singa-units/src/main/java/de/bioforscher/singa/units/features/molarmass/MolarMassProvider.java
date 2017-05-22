package de.bioforscher.singa.units.features.molarmass;

import de.bioforscher.singa.units.features.model.FeatureOrigin;
import de.bioforscher.singa.units.features.model.FeatureProvider;
import de.bioforscher.singa.units.features.model.Featureable;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.singa.units.UnitProvider.GRAM_PER_MOLE;

/**
 * @author cl
 */
public class MolarMassProvider extends FeatureProvider<MolarMass> {

    public MolarMassProvider() {
        setProvidedFeature(MolarMass.class);
    }

    @Override
    public <FeatureableType extends Featureable> MolarMass provide(FeatureableType featureable) {
        Quantity<MolarMass> quantity = Quantities.getQuantity(10, GRAM_PER_MOLE);
        MolarMass molarMass = new MolarMass(quantity, new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION));
        return molarMass;
    }
}
