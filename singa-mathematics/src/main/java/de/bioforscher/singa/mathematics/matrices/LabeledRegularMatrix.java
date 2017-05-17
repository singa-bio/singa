package de.bioforscher.singa.mathematics.matrices;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.vectors.RegularVector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class LabeledRegularMatrix<LabelType> extends RegularMatrix implements LabeledMatrix<LabelType> {

    private static final long serialVersionUID = 6232384719610197540L;

    private final Map<LabelType, Integer> rowLabelMap;
    private final Map<LabelType, Integer> columnLabelMap;

    public LabeledRegularMatrix(double[][] values) {
        super(values);
        this.rowLabelMap = new HashMap<>();
        this.columnLabelMap = new HashMap<>();
    }

    @Override
    public void setRowLabel(LabelType label, int rowIndex) {
        if (rowIndex > getRowDimension()) {
            throw new IllegalArgumentException("specified index " + rowIndex + " exceeds row dimension " + getRowDimension());
        }
        this.rowLabelMap.put(label, rowIndex);
    }

    @Override
    public RegularVector getRowByLabel(LabelType label) {
        int rowIndex = this.rowLabelMap.entrySet().stream()
                .filter(entry -> entry.getKey().equals(label))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException("specified label " + label + " is not assigned to any row"));
        return getRow(rowIndex);
    }

    @Override
    public LabelType getRowLabel(int rowIndex) {
        return this.rowLabelMap.entrySet().stream().filter(entry -> entry.getValue().equals(rowIndex)).map(Map.Entry::getKey)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("no row label exists for row index " + rowIndex));
    }

    @Override
    public List<LabelType> getRowLabels() {
        return this.rowLabelMap.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void setColumnLabel(LabelType label, int columnIndex) {
        if (columnIndex > getColumnDimension()) {
            throw new IllegalArgumentException("specified index " + columnIndex + " exceeds column dimension " + getColumnDimension());
        }
        this.columnLabelMap.put(label, columnIndex);
    }

    @Override
    public RegularVector getColumnByLabel(LabelType label) {
        int columnIndex = this.columnLabelMap.entrySet().stream()
                .filter(entry -> entry.getKey().equals(label))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException("specified label " + label + " is not assigned to any column"));
        return getColumn(columnIndex);
    }

    @Override
    public LabelType getColumnLabel(int columnIndex) {
        return this.columnLabelMap.entrySet().stream().filter(entry -> entry.getValue().equals(columnIndex)).map(Map.Entry::getKey)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("no column label exists for column index " + columnIndex));
    }

    @Override
    public List<LabelType> getColumnLabels() {
        return this.columnLabelMap.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
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
        double[][] elements = getElements();
        for (int i = 0; i < elements.length; i++) {
            StringJoiner columnJoiner = new StringJoiner(",");
            if (!this.rowLabelMap.isEmpty())
                columnJoiner.add(String.valueOf(getRowLabel(i)));
            for (int j = 0; j < elements[i].length; j++) {
                columnJoiner.add(df.format(elements[i][j]));
            }
            rowJoiner.add(columnJoiner.toString());
        }
        return rowJoiner.toString();
    }
}
