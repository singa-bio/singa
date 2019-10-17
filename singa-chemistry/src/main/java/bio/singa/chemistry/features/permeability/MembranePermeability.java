package bio.singa.chemistry.features.permeability;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.ScalableQuantitativeFeature;
import bio.singa.features.units.UnitRegistry;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

import static tech.units.indriya.unit.MetricPrefix.CENTI;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;


public class MembranePermeability extends ScalableQuantitativeFeature<MembranePermeability> implements Quantity<MembranePermeability> {

    // theoretically it is volume / time * area - volume and area cancel out
    private static Unit<Length> CENTIMETRE = CENTI(METRE);
    public static final Unit<MembranePermeability> CENTIMETRE_PER_SECOND = new ProductUnit<>(CENTIMETRE.divide(SECOND));

    public MembranePermeability(Quantity<MembranePermeability> membranePermeabilityQuantity, Evidence evidence) {
        super(membranePermeabilityQuantity, evidence);
    }

    public MembranePermeability(Quantity<MembranePermeability> membranePermeabilityQuantity) {
        super(membranePermeabilityQuantity);
    }

    @Override
    public void scale() {
        scaledQuantity = UnitRegistry.scale(getContent()).getValue().doubleValue();
        halfScaledQuantity = scaledQuantity * 0.5;
    }

    @Override
    public Quantity<MembranePermeability> add(Quantity<MembranePermeability> augend) {
        return getContent().add(augend);
    }

    @Override
    public Quantity<MembranePermeability> subtract(Quantity<MembranePermeability> subtrahend) {
        return getContent().subtract(subtrahend);
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        return getContent().divide(divisor);
    }

    @Override
    public Quantity<MembranePermeability> divide(Number divisor) {
        return getContent().divide(divisor);
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        return getContent().multiply(multiplier);
    }

    @Override
    public Quantity<MembranePermeability> multiply(Number multiplier) {
        return getContent().multiply(multiplier);
    }

    @Override
    public Quantity<?> inverse() {
        return getContent().inverse();
    }

    @Override
    public Quantity<MembranePermeability> to(Unit<MembranePermeability> unit) {
        return getContent().to(unit);
    }

    @Override
    public <T extends Quantity<T>> Quantity<T> asType(Class<T> type) throws ClassCastException {
        return getContent().asType(type);
    }

    @Override
    public Number getValue() {
        return getContent().getValue();
    }

    @Override
    public Unit<MembranePermeability> getUnit() {
        return getContent().getUnit();
    }

}
