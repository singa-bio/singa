package de.bioforscher.singa.chemistry.descriptive.features;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * A Feature is a kind of Annotation that represents a {@link Quantity} required by any algorithm. The {@link FeatureKind}
 * represents its identity - each feature type can only ba annotated once. The Quantity contains the {@link Unit} as
 * well as the value of the feature.
 *
 * @author cl
 */
public class Feature<QuantityType extends Quantity<QuantityType>> {

    /**
     * The kind of the feature.
     */
    private FeatureKind kind;

    /**
     * The quantity (unit and value) of the feature.
     */
    private Quantity<QuantityType> quantity;

    /**
     * The descriptor (prediction or database) used to assign this feature.
     */
    private FeatureDescriptor descriptor;

    /**
     * Creates a new Feature with the given values. Most commonly this should be some value from literature, you
     * measured. Most commonly a Feature should be created by {@link FeatureProvider}s.
     *
     * @param kind The kind of the feature.
     * @param quantity The quantity (unit and value) of the feature.
     */
    public Feature(FeatureKind kind, Quantity<QuantityType> quantity) {
        this.kind = kind;
        this.quantity = quantity;
    }

    /**
     * Creates a new Feature with the given values. Most commonly this should be some value from literature, you
     * measured. Most commonly a Feature should be created by {@link FeatureProvider}s.
     *
     * @param kind The kind of the feature.
     */
    public Feature(FeatureKind kind) {
        this.kind = kind;
    }


    /**
     * Returns the kind of the feature.
     * @return The kind of the feature.
     */
    public FeatureKind getKind() {
        return this.kind;
    }

    /**
     * Sets the kind of the feature.
     * @param kind The kind of the feature.
     */
    public void setKind(FeatureKind kind) {
        this.kind = kind;
    }

    /**
     * Returns the quantity (unit and value) of the feature.
     * @return The quantity (unit and value) of the feature.
     */
    public Quantity<QuantityType> getQuantity() {
        return this.quantity;
    }

    /**
     * Sets the quantity (unit and value) of the feature.
     * @param quantity The quantity (unit and value) of the feature.
     */
    public void setQuantity(Quantity<QuantityType> quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the plain double value in the unit of the quantity.
     * @return The value of this feature.
     */
    public double getValue() {
        return this.quantity.getValue().doubleValue();
    }

    /**
     * Returns the descriptor (prediction or database) used to assign this feature.
     * @return The descriptor (prediction or database) used to assign this feature.
     */
    public FeatureDescriptor getDescriptor() {
        return this.descriptor;
    }

    /**
     * Sets the descriptor (prediction or database) used to assign this feature.
     * @param descriptor The descriptor (prediction or database) used to assign this feature.
     */
    public void setDescriptor(FeatureDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "kind=" + this.kind +
                ", quantity=" + this.quantity +
                ", method=" + this.descriptor.getSourceName() +
                '}';
    }
}
