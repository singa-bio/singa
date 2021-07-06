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
public class LabeledSymmetricMatrix<LabelType> extends SymmetricMatrix implements LabeledMatrix<LabelType> {

    private static final long serialVersionUID = 2860722869189599846L;

    private final Map<LabelType, Integer> labelMap;

    /**
     * Creates a new {@code SymmetricMatrix} with the given double values. The first index of the double array
     * represents the row index and the second index represents the column index. <br>
     * <p>
     * The following array:
     * <pre>
     * {{1.0, 2.0, 3.0}, {2.0, 5.0, 6.0}, {3.0, 6.0, 9.0}} </pre>
     * result in the matrix:
     * <pre>
     * 1.0  2.0  3.0
     * 2.0  5.0  6.0
     * 3.0  6.0  9.0 </pre>
     *
     * @param values The values of the matrix.
     */
    public LabeledSymmetricMatrix(double[][] values) {
        super(values);
        labelMap = new IdentityHashMap<>();
    }

    @Override
    public void setRowLabel(LabelType label, int rowIndex) {
        if (rowIndex > getRowDimension())
            throw new IllegalArgumentException("specified index " + rowIndex + " exceeds dimension " + getRowDimension());
        labelMap.values().remove(rowIndex);
        labelMap.put(label, rowIndex);
    }

    @Override
    public RegularVector getRowByLabel(LabelType label) {
        int index = labelMap.entrySet().stream()
                .filter(entry -> entry.getKey().equals(label))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException("specified label " + label + " is not assigned"));
        return getRow(index);
    }

    @Override
    public LabelType getRowLabel(int rowIndex) {
        return labelMap.entrySet().stream().filter(entry -> entry.getValue().equals(rowIndex)).map(Map.Entry::getKey)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("no label exists for index " + rowIndex));
    }

    @Override
    public List<LabelType> getRowLabels() {
        return labelMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public LabelType getColumnLabel(int columnIndex) {
        return getRowLabel(columnIndex);
    }

    @Override
    public List<LabelType> getColumnLabels() {
        return getRowLabels();
    }

    @Override
    public void setColumnLabel(LabelType label, int columnIndex) {
        setRowLabel(label, columnIndex);
    }

    @Override
    public RegularVector getColumnByLabel(LabelType label) {
        return getRowByLabel(label);
    }

    @Override
    public Pair<Integer> getPositionFromLabels(LabelType rowLabel, LabelType columnLabel) {
        return new Pair<>(labelMap.get(rowLabel), labelMap.get(columnLabel));
    }

    @Override
    public double getValueFromPosition(Pair<Integer> position) {
        return getElement(position.getFirst(), position.getSecond());
    }

    @Override
    public String getStringRepresentation() {
        StringJoiner rowJoiner = new StringJoiner("\n");
        if (!labelMap.isEmpty())
            // assemble first line of string representation
            rowJoiner.add("," + labelMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .map(String::valueOf).collect(Collectors.joining(",")));
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(STRING_REPRESENTATION_DECIMAL_FORMAT);
        double[][] completeElements = getCompleteElements();
        for (int i = 0; i < completeElements.length; i++) {
            StringJoiner columnJoiner = new StringJoiner(",");
            if (!labelMap.isEmpty())
                columnJoiner.add(String.valueOf(getColumnLabel(i)));
            for (int j = 0; j < completeElements.length; j++) {
                columnJoiner.add(df.format(completeElements[i][j]));
            }
            rowJoiner.add(columnJoiner.toString());
        }
        return rowJoiner.toString();
    }
}
