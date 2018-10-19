package bio.singa.mathematics.topology.grids.hexagonal;

import bio.singa.mathematics.topology.model.DiscreteGrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HexagonalGrid<ValueType> implements DiscreteGrid<ValueType, HexagonalDirection, HexagonalCoordinate> {

    private final int width;
    private final int height;
    private final ValueType[][] values;

    public HexagonalGrid(int width, int height) {
        this.width = width;
        this.height = height;
        values = (ValueType[][]) new Object[width][height];
    }

    public void setValue(int q, int r, ValueType value) {
        values[q][r] = value;
    }

    public void setValue(HexagonalCoordinate coordinate, ValueType value) {
       setValue(coordinate.getQ(),coordinate.getR(), value);
    }

    public ValueType getValue(int q, int r) {
        return values[q][r];
    }

    public List<ValueType> getValues() {
        List<ValueType> results = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < width; columnIndex++) {
            for (int rowIndex = 0; rowIndex < height; rowIndex++) {
                ValueType value = values[columnIndex][rowIndex];
                if (value != null) {
                    results.add(value);
                }
            }
        }
        return results;
    }
    public List<ValueType> getColumn(int columnIndex) {
        if (columnIndex > width - 1) {
            throw new IndexOutOfBoundsException("The row " + columnIndex + " is out of bounds.");
        }
        return Arrays.asList(values[columnIndex]);
    }

    public List<ValueType> getRow(int rowIndex) {
        if (rowIndex > height - 1) {
            throw new IndexOutOfBoundsException("The row " + rowIndex + " is out of bounds.");
        }
        List<ValueType> results = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < width; columnIndex++) {
            results.add(values[columnIndex][rowIndex]);
        }
        return results;
    }

    public boolean containsValue(Object value) {
        for (int columnIndex = 0; columnIndex < width; columnIndex++) {
            for (int rowIndex = 0; rowIndex < height; rowIndex++) {
                if (getValue(columnIndex, rowIndex).equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ValueType getValue(HexagonalCoordinate coordinate) {
        return getValue(coordinate.getQ(), coordinate.getR());
    }

    public ValueType removeValue(HexagonalCoordinate coordinate) {
        ValueType nodeType = getValue(coordinate);
        setValue(coordinate, null);
        return nodeType;
    }

    @Override
    public boolean isInRange(HexagonalCoordinate coordinate) {
        return coordinate.getColumn() < width && coordinate.getColumn() > 0 && coordinate.getRow() < height && coordinate.getRow() > 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


// public Graphics2D simpledraw(double hexagonSize, Graphics g) {
    //
    // Graphics2D g2d = (Graphics2D) g;
    //
    // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    // RenderingHints.VALUE_ANTIALIAS_ON);
    //
    // double centerX;
    // double centerY;
    //
    // double midX = g.getClipBounds().getWidth() / 2.0;
    // double midY = g.getClipBounds().getHeight() / 2.0;
    //
    // double height = hexagonSize * 2.0;
    // double width = Math.sqrt(3) / 2.0 * height;
    //
    // // for every hexagon
    // for (HexagonCoordinate hex : this.keySet()) {
    //
    // int q = hex.getQ();
    // int r = hex.getR();
    // Cell cell = this.get(hex);
    //
    // // x positioning
    // centerX = q * width + midX + r * (width / 2.0);
    //
    // // y positioning
    // centerY = r * 0.75 * height + midY;
    //
    // // preparing path
    // Path2D.Double path = new Path2D.Double();
    // for (int s = 0; s <= 6; s++) {
    // double angle = 2 * Math.PI / 6 * (s + 0.5);
    // double x = centerX + hexagonSize * Math.cos(angle);
    // double y = centerY + hexagonSize * Math.sin(angle);
    // if (s == 0) {
    // path.moveTo(x, y);
    // } else {
    // path.lineTo(x, y);
    // }
    // }
    // path.closePath();
    //
    // // fill path
    // // g2d.setColor(cell.getStateColor());
    // g2d.setColor(cell.getConcentrationColor());
    // g2d.fill(path);
    //
    // // draw outline
    // g2d.setColor(Color.BLACK);
    // g2d.draw(path);
    //
    // }
    //
    // return null;
    // }

}
