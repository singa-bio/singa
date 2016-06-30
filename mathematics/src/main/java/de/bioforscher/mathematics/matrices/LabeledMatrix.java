package de.bioforscher.mathematics.matrices;

import de.bioforscher.core.utility.Pair;

import java.util.List;

/**
 * Created by Christoph on 22.06.2016.
 */
public interface LabeledMatrix<LabelType> {

    void setRowLabel(LabelType label, int rowIndex);

    LabelType getRowLabelFromPosition(int rowIndex);

    LabelType getColumnLabelFromPosition(int columnLabel);

    void setColumnLabel(LabelType label, int columnIndex);

    default void setRowLabels(List<LabelType> labels) {
        for (int i = 0; i < labels.size(); i++) {
            setRowLabel(labels.get(i), i);
        }
    }

    default void setColumnLabels(List<LabelType> labels) {
        for (int i = 0; i < labels.size(); i++) {
            setColumnLabel(labels.get(i), i);
        }
    }

    Pair<Integer> getPositionFromLabel(LabelType rowLabel, LabelType columnLabel);

    double getValueFromPosition(Pair<Integer> position);

    default double getValueForLabel(LabelType rowLabel, LabelType columnLabel) {
        return getValueFromPosition(getPositionFromLabel(rowLabel, columnLabel));
    }

}
