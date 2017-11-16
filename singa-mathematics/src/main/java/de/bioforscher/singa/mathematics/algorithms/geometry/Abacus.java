package de.bioforscher.singa.mathematics.algorithms.geometry;

import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author cl
 */
public class Abacus {

    private static final Logger logger = LoggerFactory.getLogger(Abacus.class);

    private List<Sphere> spheres;
    private BitPlane xyBitPlane;

    private int volume = 0;
    private int surface = 0;

    double[] xx;
    double[] yy;
    double[] zz;
    double[] rr;

    int[] xc;
    int[] yc;
    int[] zc;
    int[] rc;

    private double xMin;
    private double yMin;
    private double zMin;

    private double xMax;
    private double yMax;
    private double zMax;

    private int maximalXExtension = 1000;
    private int maximalYExtension = 1000;
    private int maximalZExtension = 1000;
    private final int numberOfSpheres;
    private double scale;

    public Abacus(List<Sphere> spheres) {
        this.spheres = spheres;
        xyBitPlane = new BitPlane(maximalXExtension, maximalYExtension);
        numberOfSpheres = spheres.size();
        xx = new double[numberOfSpheres];
        yy = new double[numberOfSpheres];
        zz = new double[numberOfSpheres];
        rr = new double[numberOfSpheres];
        xc = new int[numberOfSpheres];
        yc = new int[numberOfSpheres];
        zc = new int[numberOfSpheres];
        rc = new int[numberOfSpheres];
        initializeBoundaries();
        initializeArrays();
    }

    private void initializeArrays() {
        for (int i = 0; i < spheres.size(); i++) {
            final Sphere sphere = spheres.get(i);
            xx[i] = sphere.getCenter().getX();
            yy[i] = sphere.getCenter().getY();
            zz[i] = sphere.getCenter().getZ();
            rr[i] = sphere.getRadius();
        }
    }

    private void initializeBoundaries() {
        xMin = Double.MAX_VALUE;
        yMin = Double.MAX_VALUE;
        zMin = Double.MAX_VALUE;
        xMax = -Double.MAX_VALUE;
        yMax = -Double.MAX_VALUE;
        zMax = -Double.MAX_VALUE;
        for (Sphere sphere : spheres) {
            final double cMinX = sphere.getCenter().getX() - sphere.getRadius();
            if (cMinX < xMin) {
                xMin = cMinX;
            }
            final double cMinY = sphere.getCenter().getY() - sphere.getRadius();
            if (cMinY < yMin) {
                yMin = cMinY;
            }
            final double cMinZ = sphere.getCenter().getZ() - sphere.getRadius();
            if (cMinZ < zMin) {
                zMin = cMinZ;
            }
            final double cMaxX = sphere.getCenter().getX() + sphere.getRadius();
            if (cMaxX > xMax) {
                xMax = cMaxX;
            }
            final double cMaxY = sphere.getCenter().getY() + sphere.getRadius();
            if (cMaxY > yMax) {
                yMax = cMaxY;
            }
            final double cMaxZ = sphere.getCenter().getZ() + sphere.getRadius();
            if (cMaxZ > zMax) {
                zMax = cMaxZ;
            }
        }
        logger.debug("Minimal Boundaries: x = {}, y = {}, z = {}.", xMin, yMin, zMin);
        logger.debug("Maximal Boundaries: x = {}, y = {}, z = {}.", xMax, yMax, zMax);
    }

    public void calcualte() {
       scale();
       double scale2 = scale*scale;
       double scale3 = scale2*scale;
       volume = 0;
       for (int j = 0; j < maximalZExtension; j++) {
           xyBitPlane = new BitPlane(maximalXExtension, maximalYExtension);
           createPlane(j);
           trace();
       }
        System.out.println(volume);
    }

    public void scale() {
        int delta = 0;
        boolean fitted = false;
        ScalingFactor[] scalingFactors = new ScalingFactor[5];
        for (int i = 0; i < scalingFactors.length; i++) {
            scalingFactors[i] = new ScalingFactor();
        }
        scale = 0;

        while (!fitted) {
            // calculate scaling factors
            scalingFactors[0].value = (xMax - xMin + delta) / (maximalXExtension - 1);
            scalingFactors[1].value = (yMax - yMin + delta) / (maximalYExtension - 1);
            scalingFactors[2].value = (zMax - zMin + delta) / (maximalZExtension - 1);
            scalingFactors[3].value = (xMax - xMin + delta) / (maximalZExtension - 1);
            scalingFactors[4].value = (zMax - zMin + delta) / (maximalXExtension - 1);
            for (ScalingFactor scalingFactor : scalingFactors) {
                scalingFactor.magnitude++;
            }
            // sort scaling factors (bubble)
            boolean ordered;
            do {
                ordered = true;
                for (int i = 4; i > 0; i--) {
                    if (scalingFactors[i - 1].value > scalingFactors[i].value) {
                        ScalingFactor temp = scalingFactors[i];
                        scalingFactors[i] = scalingFactors[i - 1];
                        scalingFactors[i - 1] = temp;
                        ordered = false;
                    }
                }
            } while (!ordered);

            // determine integer scale
            int j = -1;
            int x2;
            int y2;
            int z2;
            do {
                j++;
                scale = scalingFactors[j].value;
                y2 = (int) Math.round((yMax - yMin) / scale);
                if (scalingFactors[j].magnitude < 4) {
                    x2 = (int) Math.round((xMax - xMin) / scale);
                    z2 = (int) Math.round((zMax - zMin) / scale);
                } else {
                    x2 = (int) Math.round((zMax - zMin) / scale);
                    z2 = (int) Math.round((xMax - xMin) / scale);
                }
                if (x2 < maximalXExtension - 1 && y2 < maximalYExtension && z2 < maximalZExtension) {
                    fitted = true;
                }
            } while (!(fitted || (j == 4)));
            delta++;
        }
        // actual rescaling
        if (scalingFactors[0].magnitude > 4) {
            for (int i = 0; i < numberOfSpheres; i++) {
                xc[i] = (int) ((zz[i] - zMin) / scale) + 1;
                zc[i] = (int) ((xx[i] - xMin) / scale) + 1;
                yc[i] = (int) ((yy[i] - yMin) / scale) + 1;
                rc[i] = (int) (rr[i] / scale);
            }
        } else {
            for (int i = 0; i < numberOfSpheres; i++) {
                xc[i] = (int) ((xx[i] - xMin) / scale) + 1;
                zc[i] = (int) ((zz[i] - zMin) / scale) + 1;
                yc[i] = (int) ((yy[i] - yMin) / scale) + 1;
                rc[i] = (int) (rr[i] / scale);
            }
        }
    }

    public void trace() {
        for (int x = 0; x < maximalXExtension; x++) {
            for (int y = 0;  y < maximalYExtension;  y++) {
                if (xyBitPlane.getBit(x, y)) {
                    volume++;
                    // calc surface here
                }
            }
        }
    }

    public void createPlane(int zi) {
        // zi is the layer in z dimension
        for (int i = 0; i < numberOfSpheres; i++) {
            // height (slice) of the current sphere
            int h = zi - zc[i];
            // radius
            int rh = rc[i];
            double rcirc2 = rh * rh - h * h;
            if (rcirc2 > 0) {
                double rcirc = Math.sqrt(rcirc2);
                createCircle(i, rcirc, rcirc2);
            }
        }
    }

    public void createCircle(int i, double rcirc, double rcirc2) {
        for (int x = (int) Math.round(xc[i] - rcirc); x <= xc[i]; x++) {
            double dist = x - xc[i];
            double d = rcirc2 - dist * dist;
            if (d > 0.0) {
                int w = (int) Math.sqrt(d);
                int y1 = yc[i] + w;
                int y2 = yc[i] - w;
                int ix1 = xc[i] + xc[i] - x;
                for (int iy = y2; iy < y1; iy++) {
                    // TODO ust setFromTo to do this
                    xyBitPlane.setBit(x, iy);
                    xyBitPlane.setBit(ix1, iy);
                }
            }
        }
    }

    private class ScalingFactor {
        private double value;
        private int magnitude;

        public ScalingFactor() {
            value = 0.0;
            magnitude = 0;
        }
    }

}
