package de.bioforscher.singa.javafx.renderer.colors;

import javafx.scene.paint.Color;

/**
 * This object provides a color scale between the given minimal and maximal
 * values.
 *
 * @author cl
 */
public class ColorScale {

    // from red to green over yellow
    private static final double defaultMinimalHue = 0.0;
    private static final double defaultMaximalHue = 120.0;
    // somewhat pastel colors
    private static final double defaultSaturation = 0.6;
    private static final double defaultBrightness = 0.9;

    private double minimalValue;
    private double maximalValue;
    private double scalingFactor;

    private final double minimalHue;
    private final double maximalHue;

    private final double saturation;
    private final double brightness;

    private ColorScale(Builder builder) {
        minimalValue = builder.minimalValue;
        maximalValue = builder.maximalValue;
        scalingFactor = builder.scalingFactor;
        minimalHue = builder.minimalHue;
        maximalHue = builder.maximalHue;
        saturation = builder.saturation;
        brightness = builder.brightness;
    }

    /**
     * Gets the color as specified by this gradient for this value between the minimal and maximal value.
     *
     * @param value The value to be converted to a color.
     * @return The resulting color.
     */
    public Color getColor(double value) {
        if (value < minimalValue || value > maximalValue) {
            throw new IllegalArgumentException(
                    "The requested value " + value + " is not contained in the initialized range [" + minimalValue
                            + "," + maximalValue + "].");
        }
        final double requestedHue = (value - minimalValue) * scalingFactor + minimalHue;
        return Color.hsb(requestedHue, saturation, brightness);
    }

    public double getMinimalValue() {
        return minimalValue;
    }

    public void setMinimalValue(double minimalValue) {
        this.minimalValue = minimalValue;
        scalingFactor = (maximalHue - minimalHue) / (maximalValue - minimalValue);
    }

    public double getMaximalValue() {
        return maximalValue;
    }

    public void setMaximalValue(double maximalValue) {
        this.maximalValue = maximalValue;
        scalingFactor = (maximalHue - minimalHue) / (maximalValue - minimalValue);
    }

    public static class Builder {

        private double minimalValue;
        private double maximalValue;
        private double scalingFactor;

        private double minimalHue = defaultMinimalHue;
        private double maximalHue = defaultMaximalHue;

        private double saturation = defaultSaturation;
        private double brightness = defaultBrightness;

        public Builder(double minimalValue, double maximalValue) {
            if (minimalValue > maximalValue) {
                throw new IllegalArgumentException("The minimal value hue has to be smaller than the maximal value.");
            }
            this.minimalValue = minimalValue;
            this.maximalValue = maximalValue;
        }

        public Builder minimalHue(double minimalHue) {
            if (minimalHue < 0.0 || minimalHue > 360.0) {
                throw new IllegalArgumentException("The value for hues has to be between 0.0 and 360.0");
            }
            this.minimalHue = minimalHue;
            return this;
        }

        public Builder minimalHue(Color minimalHueColor) {
            minimalHue = minimalHueColor.getHue();
            return this;
        }

        public Builder maximalHue(double maximalHue) {
            if (maximalHue < 0.0 || maximalHue > 360.0) {
                throw new IllegalArgumentException("The value for hues has to be between 0.0 and 360.0");
            }
            this.maximalHue = maximalHue;
            return this;
        }

        public Builder maximalHue(Color maximalHueColor) {
            maximalHue = maximalHueColor.getHue();
            return this;
        }

        public Builder saturation(double saturation) {
            if (saturation < 0.0 || saturation > 1.0) {
                throw new IllegalArgumentException("The value for saturation has to be between 0.0 and 1.0");
            }
            this.saturation = saturation;
            return this;
        }

        public Builder brightness(double brightness) {
            if (brightness < 0.0 || brightness > 1.0) {
                throw new IllegalArgumentException("The value for brightness has to be between 0.0 and 1.0");
            }
            this.brightness = brightness;
            return this;
        }

        public ColorScale build() {
            if (minimalHue > maximalHue) {
                throw new IllegalArgumentException(
                        "The value for minimal hue has to be larger than the one for maximal hue.");
            }
            // scale min and max values
            scalingFactor = (maximalHue - minimalHue) / (maximalValue - minimalValue);
            return new ColorScale(this);
        }

    }

}
