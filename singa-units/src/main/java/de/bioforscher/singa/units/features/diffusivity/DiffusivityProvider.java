package de.bioforscher.singa.units.features.diffusivity;

import de.bioforscher.singa.units.features.model.FeatureOrigin;
import de.bioforscher.singa.units.features.model.FeatureProvider;
import de.bioforscher.singa.units.features.model.Featureable;
import de.bioforscher.singa.units.features.molarmass.MolarMass;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.singa.units.UnitProvider.SQUARE_CENTIMETER_PER_SECOND;

/**
 * @author cl
 */
public class DiffusivityProvider extends FeatureProvider<Diffusivity> {

    public DiffusivityProvider() {
        setProvidedFeature(Diffusivity.class);
        addRequirement(MolarMass.class);
    }

    @Override
    public <FeatureableType extends Featureable> Diffusivity provide(FeatureableType featureable) {
        Quantity<Diffusivity> quantity = Quantities.getQuantity(10, SQUARE_CENTIMETER_PER_SECOND);
        Diffusivity diffusivity = new Diffusivity(quantity, new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION));
        return diffusivity;
    }
}
