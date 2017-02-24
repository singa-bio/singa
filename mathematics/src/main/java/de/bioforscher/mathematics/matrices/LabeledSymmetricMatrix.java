package de.bioforscher.mathematics.matrices;

import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.vectors.RegularVector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 21.06.2016.
 */
public class LabeledSymmetricMatrix<LabelType> extends SymmetricMatrix implements LabeledMatrix<LabelType> {

    private Map<LabelType, Integer> labelMap;

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
        this.labelMap = new HashMap<>();
    }

    @Override
    public void setRowLabel(LabelType label, int rowIndex) {
        if (rowIndex > getRowDimension())
            throw new IllegalArgumentException("specified index " + rowIndex + " exceeds dimension " + getRowDimension());
        this.labelMap.values().remove(rowIndex);
        this.labelMap.put(label, rowIndex);
    }

    @Override
    public RegularVector getRowByLabel(LabelType label) {
        int index = this.labelMap.entrySet().stream()
                .filter(entry -> entry.getKey().equals(label))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new IllegalArgumentException("specified label " + label + " is not assigned"));
        // we have to use reconstruct the vector because only jagged values are stored
        return new RegularVector(getCompleteElements()[index]);
    }

    @Override
    public LabelType getRowLabel(int rowIndex) {
        return this.labelMap.entrySet().stream().filter(entry -> entry.getValue().equals(rowIndex)).map(Map.Entry::getKey)
                .findFirst().get();
    }

    @Override
    public LabelType getColumnLabel(int columnIndex) {
        return getRowLabel(columnIndex);
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
        return new Pair<>(this.labelMap.get(rowLabel), this.labelMap.get(columnLabel));
    }

    @Override
    public double getValueFromPosition(Pair<Integer> position) {
        return getElement(position.getFirst(), position.getSecond());
    }

    @Override
    public String getStringRepresentation() {
        StringJoiner rowJoiner = new StringJoiner("\n");
        if (!this.labelMap.isEmpty())
            // assemble first line of string representation
            rowJoiner.add("," + this.labelMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .map(String::valueOf).collect(Collectors.joining(",")));
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(STRING_REPRESENTATION_DECIMAL_FORMAT);
        for (int i = 0; i < getCompleteElements().length; i++) {
            StringJoiner columnJoiner = new StringJoiner(",");
            if (!this.labelMap.isEmpty())
                columnJoiner.add(String.valueOf(getColumnLabel(i)));
            for (int j = 0; j < getCompleteElements()[i].length; j++) {
                columnJoiner.add(df.format(getCompleteElements()[i][j]));
            }
            rowJoiner.add(columnJoiner.toString());
        }
        return rowJoiner.toString();
    }
}
