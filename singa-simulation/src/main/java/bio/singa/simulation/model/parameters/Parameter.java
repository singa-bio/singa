package bio.singa.simulation.model.parameters;

import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.model.Evidence;
import bio.singa.features.units.UnitRegistry;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class Parameter<QuantityType extends Quantity<QuantityType>> {

    /**
     * The distinct {@link Identifier} by which this parameter is identified.
     */
    private final String identifier;

    private Evidence origin;

    /**
     * The value and unit (quantity) of this parameter.
     */
    private Quantity<QuantityType> quantity;

    protected Quantity<QuantityType> scaledQuantity;
    protected Quantity<QuantityType> halfScaledQuantity;

    public Parameter(String identifier) {
        this.identifier = identifier;
    }

    public Parameter(String identifier, Quantity<QuantityType> quantity, Evidence origin) {
        this.identifier = identifier;
        this.quantity = quantity;
        this.origin = origin;
    }

    public void scale() {
        scaledQuantity = UnitRegistry.scale(quantity);
        halfScaledQuantity = scaledQuantity.multiply(0.5);
    }

    public String getIdentifier() {
        return identifier;
    }

    public Evidence getOrigin() {
        return origin;
    }

    public void setOrigin(Evidence origin) {
        this.origin = origin;
    }

    public Quantity<QuantityType> getQuantity() {
        return quantity;
    }

    public void setQuantity(Quantity<QuantityType> quantity) {
        this.quantity = quantity;
    }

    public Quantity<QuantityType> getScaledQuantity() {
        return scaledQuantity;
    }

    public Quantity<QuantityType> getHalfScaledQuantity() {
        return halfScaledQuantity;
    }

    @Override
    public String toString() {
        return "Parameter: " + identifier + " = " + getQuantity() + " (" + getScaledQuantity() + ")";
    }
}
