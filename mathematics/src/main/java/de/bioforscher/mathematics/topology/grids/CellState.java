package de.bioforscher.mathematics.topology.grids;

import java.awt.*;

public enum CellState {

    Alive(new Color(250, 250, 250)),
    Dead(new Color(189, 189, 189)),
    Highlight1(new Color(254, 224, 139)),
    Highlight2(new Color(217, 239, 139));

    private final Color color;

    CellState(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

}
