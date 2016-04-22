package de.bioforscher.simulation.util;

import java.util.prefs.Preferences;

public class SingaPerferences {

    public Preferences preferences;

    public SingaPerferences() {
        this.preferences = Preferences.userRoot().node(this.getClass().getName());
    }

    public void restoreDefaults() {
        restorePlotDefaults();
    }

    public void restorePlotDefaults() {
        this.preferences.remove(Plot.MAXIMAL_DATA_POINTS);
        this.preferences.remove(Plot.TICK_SPACING);
        this.preferences.remove(Plot.SCROLL_PLOT);
    }

    public static class Plot {

        /**
         * Maximal number of displayed data points.
         */
        public static final String MAXIMAL_DATA_POINTS = "PLOT_MAXIMAL_DATA_POINTS";
        /**
         * 500
         */
        public static final int MAXIMAL_DATA_POINTS_VALUE = 500;

        /**
         * Spacing between ticks
         */
        public static final String TICK_SPACING = "PLOT_TICK_SPACING";
        /**
         * 50
         */
        public static final int TICK_SPACING_VALUE = MAXIMAL_DATA_POINTS_VALUE / 10;

        /**
         * If {@code true} the plot scrolls eternally, else the the plot will be
         * resized to capture all values.
         */
        public static final String SCROLL_PLOT = "PLOT_SCROLL_PLOT";
        /**
         * true
         */
        public static final boolean SCROLL_PLOT_VALUE = true;

    }

}
