package de.bioforscher.singa.core.utility;

/**
 * A label is an object (LabelType) das identifies the location or position (PositionType) of something (ValueType) in
 * the object that implements this interface.
 *
 * @param <LabelType> The type of label.
 * @param <PositionType> The type of the referenced position.
 * @param <ValueType> The type of the value.
 * @author cl
 */
public interface Labeled<LabelType, PositionType, ValueType> {

    /**
     * Applies a label to a certain position.
     *
     * @param label The label.
     * @param position The position.
     */
    void labelPosition(LabelType label, PositionType position);

    /**
     * Returns the position from the label (if there is any).
     *
     * @param label The label.
     * @return The position.
     */
    PositionType getPositionFromLabel(LabelType label);

    /**
     * Returns a value from a position.
     *
     * @param position The position.
     * @return The value.
     */
    ValueType getValueFromPosition(PositionType position);

    /**
     * Returns the value for a label.
     *
     * @param label The label.
     * @return The value.
     */
    default ValueType getValueForLabel(LabelType label) {
        return getValueFromPosition(getPositionFromLabel(label));
    }

}
