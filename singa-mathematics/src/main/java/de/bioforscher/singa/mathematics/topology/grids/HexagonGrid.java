package de.bioforscher.singa.mathematics.topology.grids;

import java.util.HashMap;
import java.util.Map;

public class HexagonGrid extends HashMap<HexagonCoordinate, Cell> {

    private static final long serialVersionUID = 1L;

    public HexagonGrid() {
        super();
    }

    public HexagonGrid(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public HexagonGrid(int initialCapacity) {
        super(initialCapacity);
    }

    public HexagonGrid(Map<? extends HexagonCoordinate, ? extends Cell> m) {
        super(m);
    }

    public HexagonCoordinate getNeighborHexagon(HexagonCoordinate coordinate, HexagonCoordianteDirection direction) {
        return getNeighborHexagon(coordinate, direction, 1);
    }

    public HexagonCoordinate getNeighborHexagon(HexagonCoordinate coordinate, HexagonCoordianteDirection direction,
                                                int scale) {
        short q = coordinate.getQ();
        short r = coordinate.getR();
        HexagonCoordinate[] result = {new HexagonCoordinate(q + scale, r), new HexagonCoordinate(q + scale, r - scale),
                new HexagonCoordinate(q, r - scale), new HexagonCoordinate(q - scale, r),
                new HexagonCoordinate(q - scale, r + scale), new HexagonCoordinate(q, r + scale)};
        return result[direction.getValue()];
    }

    public Cell getNeighborCell(HexagonCoordinate hexagon, HexagonCoordianteDirection direction) {
        short q = hexagon.getQ();
        short r = hexagon.getR();
        Cell[] result = {this.get(q + 1, r), this.get(q + 1, r - 1), this.get(q, r - 1), this.get(q - 1, r),
                this.get(q - 1, r + 1), this.get(q, r + 1)};
        return result[direction.getValue()];
    }

    public Cell[] getNeighborCells(HexagonCoordinate hexagon) {
        short q = hexagon.getQ();
        short r = hexagon.getR();
        return new Cell[]{this.get(q + 1, r), this.get(q + 1, r - 1), this.get(q, r - 1), this.get(q - 1, r),
                this.get(q - 1, r + 1), this.get(q, r + 1)};
    }

    public Cell[] getNeighborCells(Cell cell) {
        short q = cell.getQ();
        short r = cell.getR();
        return new Cell[]{this.get(q + 1, r), this.get(q + 1, r - 1), this.get(q, r - 1), this.get(q - 1, r),
                this.get(q - 1, r + 1), this.get(q, r + 1)};
    }

    public Cell[] getNeighborCells(short q, short r) {
        return new Cell[]{this.get(q + 1, r), this.get(q + 1, r - 1), this.get(q, r - 1), this.get(q - 1, r),
                this.get(q - 1, r + 1), this.get(q, r + 1)};
    }

    public Cell[] getNeighborCells(int q, int r) {
        return new Cell[]{this.get(q + 1, r), this.get(q + 1, r - 1), this.get(q, r - 1), this.get(q - 1, r),
                this.get(q - 1, r + 1), this.get(q, r + 1)};
    }

    public Cell get(short q, short r) {
        return this.get(new HexagonCoordinate(q, r));
    }

    public Cell get(int q, int r) {
        return this.get(new HexagonCoordinate(q, r));
    }

    public int getMinQValue() {
        int minQ = Integer.MAX_VALUE;
        for (HexagonCoordinate hexagon : this.keySet()) {
            if (minQ > hexagon.getQ()) {
                minQ = hexagon.getQ();
            }
        }
        return minQ;
    }

    public int getMaxQValue() {
        int maxQ = Integer.MIN_VALUE;
        for (HexagonCoordinate hexagon : this.keySet()) {
            if (maxQ < hexagon.getQ()) {
                maxQ = hexagon.getQ();
            }
        }
        return maxQ;
    }

    public int getMinRValue() {
        int minR = Integer.MAX_VALUE;
        for (HexagonCoordinate hexagon : this.keySet()) {
            if (minR > hexagon.getR()) {
                minR = hexagon.getR();
            }
        }
        return minR;
    }

    public int getMaxRValue() {
        int maxR = Integer.MIN_VALUE;
        for (HexagonCoordinate hexagon : this.keySet()) {
            if (maxR < hexagon.getR()) {
                maxR = hexagon.getR();
            }
        }
        return maxR;
    }

    public HexagonCoordinate getLeftMostHexagon() {
        double minX = Double.MAX_VALUE;
        short minQ = 0;
        short minR = 0;
        for (HexagonCoordinate hexagon : this.keySet()) {
            double currentX = (double) hexagon.getQ() + (double) hexagon.getR() * 0.5;
            if (currentX < minX) {
                minX = currentX;
                minQ = hexagon.getQ();
                minR = hexagon.getR();
            }
        }
        return new HexagonCoordinate(minQ, minR);
    }

    public HexagonCoordinate getRightMostHexagon() {
        double maxX = -Double.MAX_VALUE;
        short maxQ = 0;
        short maxR = 0;
        for (HexagonCoordinate hexagon : this.keySet()) {
            double currentX = (double) hexagon.getQ() + (double) hexagon.getR() * 0.5;
            if (currentX > maxX) {
                maxX = currentX;
                maxQ = hexagon.getQ();
                maxR = hexagon.getR();
            }
        }
        return new HexagonCoordinate(maxQ, maxR);
    }

    public void fillParallelogram(int qMin, int qMax, int rMin, int rMax) {
        for (int q = qMin; q <= qMax; q++) {
            for (int r = rMin; r <= rMax; r++) {
                this.put(new HexagonCoordinate(q, r), new Cell(q, r));
            }
        }
    }

    public void fillCircle(HexagonCoordinate centerHex, int radius) {
        this.put(centerHex, new Cell(centerHex.getQ(), centerHex.getR()));
        HexagonCoordinate h = centerHex;
        for (int r = 1; r <= radius; r++) {
            h = this.getNeighborHexagon(centerHex, HexagonCoordianteDirection.SouthWest, r);
            for (HexagonCoordianteDirection direction : HexagonCoordianteDirection.values()) {
                for (int k = 0; k < r; k++) {
                    h = this.getNeighborHexagon(h, direction);
                    this.put(h, new Cell(h.getQ(), h.getR()));
                }
            }
        }
    }

    public void fillRectangle(int startQ, int startR, int width, int height) {
        for (int q = startQ; q < startQ + height; q++) {
            for (int r = startR; r < startR + width; r++) {
                if (r % 2 == 0) {
                    this.put(new HexagonCoordinate(q - r / 2, r), new Cell(q - r / 2, r));
                } else {
                    this.put(new HexagonCoordinate(q - r / 2, r), new Cell(q - r / 2, r));
                }
            }
        }
    }

    public void setStateOfAllCellsWithRValue(int r, CellState state) {
        this.keySet().stream().filter(hexagon -> hexagon.getR() == r).forEach(hexagon -> this.get(hexagon).setState(state));
    }

    public void setStateOfAllCellsWithQValue(int q, CellState state) {
        this.keySet().stream().filter(hexagon -> hexagon.getQ() == q).forEach(hexagon -> this.get(hexagon).setState(state));
    }

    public static HexagonGrid fillRadom() {
        HexagonGrid grid = new HexagonGrid(400);
        for (int i = -5; i <= 5; i++) {
            for (int j = -5; j <= 5; j++) {
                if (Math.random() < 0.6) {
                    if (Math.random() < 0.6) {
                        grid.put(new HexagonCoordinate(i, j), new Cell(i, j, CellState.Alive));
                    } else {
                        grid.put(new HexagonCoordinate(i, j), new Cell(i, j, CellState.Dead));
                    }
                }
            }
        }
        return grid;
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
