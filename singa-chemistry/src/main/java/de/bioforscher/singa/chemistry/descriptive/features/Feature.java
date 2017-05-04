package de.bioforscher.singa.chemistry.descriptive.features;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class Feature<QuantityType extends Quantity<QuantityType>> {

    private FeatureKind kind;
    private Quantity<QuantityType> quantity;

    public Feature(FeatureKind kind, Quantity<QuantityType> quantity) {
        this.kind = kind;
        this.quantity = quantity;
    }

    public FeatureKind getKind() {
        return this.kind;
    }

    public void setKind(FeatureKind kind) {
        this.kind = kind;
    }

    public Quantity<QuantityType> getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Quantity<QuantityType> quantity) {
        this.quantity = quantity;
    }

    public double getValue() {
        return this.quantity.getValue().doubleValue();
    }

    @Override
    public String toString() {
        return "Feature{" +
                "kind=" + kind +
                ", quantity=" + quantity +
                '}';
    }
}
