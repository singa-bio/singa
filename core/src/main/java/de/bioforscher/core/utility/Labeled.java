package de.bioforscher.core.utility;

/**
 * A label is an object (LabelType) das identifies the location or position (PositionType) of something (ValueType) in
 * the object that implements this interface.
 */
public interface Labeled<LabelType, PositionType, ValueType> {

    void labelPosition(LabelType label, PositionType position);

    PositionType getPositionFromLabel(LabelType label);

    ValueType getValueFromPosition(PositionType position);

    default ValueType getValueForLabel(LabelType label) {
        return getValueFromPosition(getPositionFromLabel(label));
    }

}
