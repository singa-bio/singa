package bio.singa.mathematics.matrices;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.vectors.RegularVector;

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
        rowLabelMap = new IdentityHashMap<>();
        columnLabelMap = new IdentityHashMap<>();
    }

    @Override
    public void setRowLabel(LabelType label, int rowIndex) {
        if (rowIndex > getRowDimension()) {
            throw new IllegalArgumentException("specified index " + rowIndex + " exceeds row dimension " + getRowDimension());
        }
        rowLabelMap.put(label, rowIndex);
    }

    @Override
    public RegularVector getRowByLabel(LabelType label) {
        int rowIndex = rowLabelMap.entrySet().stream()
                .filter(entry -> entry.getKey().equals(label))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException("specified label " + label + " is not assigned to any row"));
        return getRow(rowIndex);
    }

    @Override
    public LabelType getRowLabel(int rowIndex) {
        return rowLabelMap.entrySet().stream().filter(entry -> entry.getValue().equals(rowIndex)).map(Map.Entry::getKey)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("no row label exists for row index " + rowIndex));
    }

    @Override
    public List<LabelType> getRowLabels() {
        return rowLabelMap.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void setColumnLabel(LabelType label, int columnIndex) {
        if (columnIndex > getColumnDimension()) {
            throw new IllegalArgumentException("specified index " + columnIndex + " exceeds column dimension " + getColumnDimension());
        }
        columnLabelMap.put(label, columnIndex);
    }

    @Override
    public RegularVector getColumnByLabel(LabelType label) {
        int columnIndex = columnLabelMap.entrySet().stream()
                .filter(entry -> entry.getKey().equals(label))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException("specified label " + label + " is not assigned to any column"));
        return getColumn(columnIndex);
    }

    @Override
    public LabelType getColumnLabel(int columnIndex) {
        return columnLabelMap.entrySet().stream().filter(entry -> entry.getValue().equals(columnIndex)).map(Map.Entry::getKey)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("no column label exists for column index " + columnIndex));
    }

    @Override
    public List<LabelType> getColumnLabels() {
        return columnLabelMap.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public Pair<Integer> getPositionFromLabels(LabelType rowLabel, LabelType columnLabel) {
        return new Pair<>(rowLabelMap.get(rowLabel), columnLabelMap.get(columnLabel));
    }

    @Override
    public double getValueFromPosition(Pair<Integer> position) {
        return getElement(position.getFirst(), position.getSecond());
    }

    @Override
    public String getStringRepresentation() {
        StringJoiner rowJoiner = new StringJoiner("\n");
        if (!columnLabelMap.isEmpty())
            // assemble first line of string representation
            rowJoiner.add("," + columnLabelMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .map(String::valueOf).collect(Collectors.joining(",")));
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(STRING_REPRESENTATION_DECIMAL_FORMAT);
        double[][] elements = getElements();
        for (int i = 0; i < elements.length; i++) {
            StringJoiner columnJoiner = new StringJoiner(",");
            if (!rowLabelMap.isEmpty())
                columnJoiner.add(String.valueOf(getRowLabel(i)));
            for (int j = 0; j < elements[i].length; j++) {
                columnJoiner.add(df.format(elements[i][j]));
            }
            rowJoiner.add(columnJoiner.toString());
        }
        return rowJoiner.toString();
    }
}
