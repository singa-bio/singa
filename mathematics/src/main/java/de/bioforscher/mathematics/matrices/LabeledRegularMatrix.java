package de.bioforscher.mathematics.matrices;

import de.bioforscher.core.utility.Pair;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by fkaiser on 04.10.16.
 */
public class LabeledRegularMatrix<LabelType> extends RegularMatrix implements LabeledMatrix<LabelType> {

    private final Map<LabelType, Integer> rowLabelMap;
    private final Map<LabelType, Integer> columnLabelMap;

    public LabeledRegularMatrix(double[][] values) {
        super(values);
        this.rowLabelMap = new HashMap<>();
        this.columnLabelMap = new HashMap<>();
    }

    @Override
    public void setRowLabel(LabelType label, int rowIndex) {
        if (rowIndex > getRowDimension())
            throw new IllegalArgumentException("specified index " + rowIndex + " exceeds row dimension " + getRowDimension());
        this.rowLabelMap.put(label, rowIndex);
    }

    @Override
    public LabelType getRowLabel(int rowIndex) {
        return this.rowLabelMap.entrySet().stream().filter(entry -> entry.getValue().equals(rowIndex)).map(Map.Entry::getKey)
                .findFirst().get();
    }

    @Override
    public void setColumnLabel(LabelType label, int columnIndex) {
        if (columnIndex > getRowDimension())
            throw new IllegalArgumentException("specified index " + columnIndex + " exceeds row dimension " + getColumnDimension());
        this.columnLabelMap.put(label, columnIndex);
    }

    @Override
    public LabelType getColumnLabel(int columnIndex) {
        return this.rowLabelMap.entrySet().stream().filter(entry -> entry.getValue().equals(columnIndex)).map(Map.Entry::getKey)
                .findFirst().get();
    }

    @Override
    public Pair<Integer> getPositionFromLabels(LabelType rowLabel, LabelType columnLabel) {
        return new Pair<>(this.rowLabelMap.get(rowLabel), this.columnLabelMap.get(columnLabel));
    }

    @Override
    public double getValueFromPosition(Pair<Integer> position) {
        return getElement(position.getFirst(), position.getSecond());
    }

    @Override
    public String getStringRepresentation() {
        StringJoiner rowJoiner = new StringJoiner("\n");
        if (!this.columnLabelMap.isEmpty())
            // assemble first line of string representation
            rowJoiner.add("," + this.columnLabelMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .map(String::valueOf).collect(Collectors.joining(",")));
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(STRING_REPRESENTATION_DECIMAL_FORMAT);
        for (int i = 0; i < getElements().length; i++) {
            StringJoiner columnJoiner = new StringJoiner(",");
            if (!this.rowLabelMap.isEmpty())
                columnJoiner.add(String.valueOf(getColumnLabel(i)));
            for (int j = 0; j < getElements()[i].length; j++) {
                columnJoiner.add(df.format(getElements()[i][j]));
            }
            rowJoiner.add(columnJoiner.toString());
        }
        return rowJoiner.toString();
    }
}
