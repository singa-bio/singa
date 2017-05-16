package de.bioforscher.singa.chemistry.descriptive.features.predictors;

import de.bioforscher.singa.chemistry.descriptive.features.Featureable;
import de.bioforscher.singa.chemistry.descriptive.features.descriptor.FeatureDescriptor;
import de.bioforscher.singa.units.quantities.DynamicViscosity;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Temperature;

import static de.bioforscher.singa.units.UnitProvider.PASCAL_SECOND;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.KELVIN;

/**
 * @author cl
 */
public abstract class PredictionDescriptor<FeatureType extends Quantity<FeatureType>> implements FeatureDescriptor {

    protected String methodName;
    protected String methodPublication;

    /**
     * Standard system temperature [temperature] (293 K = 20 C)
     */
    protected static final Quantity<Temperature> TEMPERATURE = Quantities.getQuantity(293.0, KELVIN);

    /**
     * Standard system viscosity [pressure per time] (1 mPa*s = 1cP = Viscosity of Water at 20 C)
     */
    protected static final Quantity<DynamicViscosity> VISCOSITY = Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND));

    protected void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    protected void setMethodPublication(String methodPublication) {
        this.methodPublication = methodPublication;
    }

    @Override
    public String getMethodName() {
        return this.methodName;
    }

    @Override
    public String getMethodPublication() {
        return this.methodPublication;
    }

    public abstract <FeaturableType extends Featureable> Quantity<FeatureType> calculate(FeaturableType featureable);

}
