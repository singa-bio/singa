package de.bioforscher.singa.chemistry.descriptive.features.predictors;

import de.bioforscher.singa.chemistry.descriptive.features.FeatureDescriptor;
import de.bioforscher.singa.chemistry.descriptive.features.Featureable;
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

    /**
     * Standard system temperature [temperature] (293 K = 20 C)
     */
    protected static final Quantity<Temperature> TEMPERATURE = Quantities.getQuantity(293.0, KELVIN);

    /**
     * Standard system viscosity [pressure per time] (1 mPa*s = 1cP = Viscosity of Water at 20 C)
     */
    protected static final Quantity<DynamicViscosity> VISCOSITY = Quantities.getQuantity(1.0, MILLI(PASCAL_SECOND));

    protected String sourceName;
    protected String sourcePublication;

    protected void setSourceName(String methodName) {
        this.sourceName = methodName;
    }

    protected void setSourcePublication(String methodPublication) {
        this.sourcePublication = methodPublication;
    }

    @Override
    public String getSourceName() {
        return this.sourceName;
    }

    @Override
    public String getSourcePublication() {
        return this.sourcePublication;
    }

    public abstract <FeaturableType extends Featureable> Quantity<FeatureType> calculate(FeaturableType featureable);

}
