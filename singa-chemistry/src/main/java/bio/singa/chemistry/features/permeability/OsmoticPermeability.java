package bio.singa.chemistry.features.permeability;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.Quantity;
import javax.measure.Unit;

import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * Represents the rate at which a substrate of a Transporter may be transported through the membrane.
 *
 * @author cl
 */
public class OsmoticPermeability extends AbstractScalableQuantitativeFeature<OsmoticPermeability> implements Quantity<OsmoticPermeability> {

    /**
     * Unit most commonly used to describe osmotic permeability.
     */
    public static final Unit<OsmoticPermeability> CUBIC_CENTIMETRE_PER_SECOND = new ProductUnit<>(METRE.divide(100).pow(3).divide(SECOND));

    /**
     * Creates a new Instance of the {@link OsmoticPermeability} Feature.
     *
     * @param osmoticPermeabilityQuantity The osmotic permeability.
     * @param evidence The origin of the feature.
     */
    public OsmoticPermeability(Quantity<OsmoticPermeability> osmoticPermeabilityQuantity, Evidence evidence) {
        super(osmoticPermeabilityQuantity, evidence);
    }

    public OsmoticPermeability(Quantity<OsmoticPermeability> quantity) {
        super(quantity);
    }

    /**
     * Creates a new Instance of the {@link OsmoticPermeability} Feature. Quantity is interpreted as
     * {@link OsmoticPermeability#CUBIC_CENTIMETRE_PER_SECOND}.
     *
     * @param osmoticPermeabilityQuantity The osmotic permeability in {@link OsmoticPermeability#CUBIC_CENTIMETRE_PER_SECOND}
     * @param evidence The origin of the feature.
     */
    public OsmoticPermeability(double osmoticPermeabilityQuantity, Evidence evidence) {
        super(Quantities.getQuantity(osmoticPermeabilityQuantity, CUBIC_CENTIMETRE_PER_SECOND), evidence);
    }

    @Override
    public Quantity<OsmoticPermeability> add(Quantity<OsmoticPermeability> augend) {
        return getContent().add(augend);
    }

    @Override
    public Quantity<OsmoticPermeability> subtract(Quantity<OsmoticPermeability> subtrahend) {
        return getContent().subtract(subtrahend);
    }

    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        return getContent().divide(divisor);
    }

    @Override
    public Quantity<OsmoticPermeability> divide(Number divisor) {
        return getContent().divide(divisor);
    }

    @Override
    public Quantity<?> multiply(Quantity<?> multiplier) {
        return getContent().multiply(multiplier);
    }

    @Override
    public Quantity<OsmoticPermeability> multiply(Number multiplier) {
        return getContent().multiply(multiplier);
    }

    @Override
    public Quantity<?> inverse() {
        return getContent().inverse();
    }

    @Override
    public Quantity<OsmoticPermeability> to(Unit<OsmoticPermeability> unit) {
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
    public Unit<OsmoticPermeability> getUnit() {
        return getContent().getUnit();
    }

}
