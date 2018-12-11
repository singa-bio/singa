package bio.singa.chemistry.features.permeability;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.ScalableQuantityFeature;
import bio.singa.features.units.UnitRegistry;
import tec.uom.se.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

import static tec.uom.se.unit.MetricPrefix.CENTI;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;


public class MembranePermeability extends ScalableQuantityFeature<MembranePermeability> implements Quantity<MembranePermeability> {

    // theoretically it is volume / time * area - volume and area cancel out
    private static Unit<Length> CENTIMETRE = CENTI(METRE);
    public static final Unit<MembranePermeability> CENTIMETRE_PER_SECOND = new ProductUnit<>(CENTIMETRE.divide(SECOND));

    public static final String SYMBOL = "P_d";

    public MembranePermeability(Quantity<MembranePermeability> membranePermeabilityQuantity, Evidence evidence) {
        super(membranePermeabilityQuantity, evidence);
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scale(getFeatureContent());
        halfScaledQuantity = scaledQuantity.multiply(0.5);
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
