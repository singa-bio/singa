package de.bioforscher.singa.chemistry.descriptive.features.permeability;

import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.ScalableQuantityFeature;
import de.bioforscher.singa.features.parameters.Environment;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static tec.uom.se.unit.MetricPrefix.CENTI;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;


public class MembranePermeability extends ScalableQuantityFeature<MembranePermeability> implements Quantity<MembranePermeability> {

    // theoretically it is volume / time * area - volume and area cancel out
    private static Unit<Length> CENTIMETRE = CENTI(METRE);
    public static final Unit<MembranePermeability> CENTIMETRE_PER_SECOND = new ProductUnit<>(CENTIMETRE.divide(SECOND));
    public static final Unit<MembranePermeability> CUBIC_CENITMETRE_PER_SECOND_AND_SQUARE_CENTIMETRE = new ProductUnit<>(CENTIMETRE.pow(3).divide(SECOND.multiply(CENTIMETRE.pow(2))));

    public static final String SYMBOL = "P_d";

    public MembranePermeability(Quantity<MembranePermeability> membranePermeabilityQuantity, FeatureOrigin featureOrigin) {
        super(membranePermeabilityQuantity.to(new ProductUnit<>(Environment.getTransformedLength().divide(SECOND))), featureOrigin);
    }

    @Override
    public void scale(Quantity<Time> time, Quantity<Length> space) {
        // transform to specified unit
        Quantity<MembranePermeability> transformedQuantity = getFeatureContent().to(new ProductUnit<>(Environment.getTransformedLength().divide(time.getUnit())));
        // transform to specified amount
        scaledQuantity = transformedQuantity.multiply(time.getValue().doubleValue());
        // and half
        halfScaledQuantity = transformedQuantity.multiply(time.multiply(0.5).getValue().doubleValue());
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

    @Override
    public Quantity<MembranePermeability> add(Quantity<MembranePermeability> augend) {
        return getFeatureContent().add(augend);
    }

    @Override
    public Quantity<MembranePermeability> subtract(Quantity<MembranePermeability> subtrahend) {
        return getFeatureContent().subtract(subtrahend);
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        return getFeatureContent().divide(divisor);
    }

    @Override
    public Quantity<MembranePermeability> divide(Number divisor) {
        return getFeatureContent().divide(divisor);
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        return getFeatureContent().multiply(multiplier);
    }

    @Override
    public Quantity<MembranePermeability> multiply(Number multiplier) {
        return getFeatureContent().multiply(multiplier);
    }

    @Override
    public Quantity<?> inverse() {
        return getFeatureContent().inverse();
    }

    @Override
    public Quantity<MembranePermeability> to(Unit<MembranePermeability> unit) {
        return getFeatureContent().to(unit);
    }

    @Override
    public <T extends Quantity<T>> Quantity<T> asType(Class<T> type) throws ClassCastException {
        return getFeatureContent().asType(type);
    }

    @Override
    public Number getValue() {
        return getFeatureContent().getValue();
    }

    @Override
    public Unit<MembranePermeability> getUnit() {
        return getFeatureContent().getUnit();
    }

}
