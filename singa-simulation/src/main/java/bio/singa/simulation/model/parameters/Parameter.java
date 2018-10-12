package bio.singa.simulation.model.parameters;

import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.model.FeatureOrigin;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class Parameter<QuantityType extends Quantity<QuantityType>> {

    /**
     * The distinct {@link Identifier} by which this parameter is identified.
     */
    private final String identifier;

    private FeatureOrigin origin;

    /**
     * The value and unit (quantity) of this parameter.
     */
    private Quantity<QuantityType> quantity;

    public Parameter(String identifier) {
        this.identifier = identifier;
    }

    public Parameter(String identifier, Quantity<QuantityType> quantity, FeatureOrigin origin) {
        this.identifier = identifier;
        this.quantity = quantity;
        this.origin = origin;
    }

    public String getIdentifier() {
        return identifier;
    }

    public FeatureOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(FeatureOrigin origin) {
        this.origin = origin;
    }

    public Quantity<QuantityType> getQuantity() {
        return quantity;
    }

    public void setQuantity(Quantity<QuantityType> quantity) {
        this.quantity = quantity;
    }

}
