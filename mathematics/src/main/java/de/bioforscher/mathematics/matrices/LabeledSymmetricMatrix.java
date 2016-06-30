package de.bioforscher.mathematics.matrices;

import de.bioforscher.core.utility.Pair;

import java.util.HashMap;
import java.util.Map;

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
        this.labelMap.put(label, rowIndex);
    }

    @Override
    public LabelType getRowLabelFromPosition(int rowIndex) {
        return labelMap.entrySet().stream().filter(entry -> entry.getValue().equals(rowIndex)).map(Map.Entry::getKey)
                .findFirst().get();
    }

    @Override
    public LabelType getColumnLabelFromPosition(int columnIndex) {
        return getRowLabelFromPosition(columnIndex);
    }

    @Override
    public void setColumnLabel(LabelType label, int columnIndex) {
        setRowLabel(label, columnIndex);
    }

    @Override
    public Pair<Integer> getPositionFromLabel(LabelType rowLabel, LabelType columnLabel) {
        return new Pair<>(this.labelMap.get(rowLabel), this.labelMap.get(columnLabel));
    }

    @Override
    public double getValueFromPosition(Pair<Integer> position) {
        return getElement(position.getFirst(), position.getSecond());
    }

}
