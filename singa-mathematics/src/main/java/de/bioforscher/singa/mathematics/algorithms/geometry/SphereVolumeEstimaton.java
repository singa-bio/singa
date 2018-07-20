package de.bioforscher.singa.mathematics.algorithms.geometry;

import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Predicts the molar volume of a set of spheres.
 *
 * <pre>
 *     Ott, Rolf, et al. "A computer method for estimating volumes and surface areas of complex structures consisting of overlapping spheres."
 *     Mathematical and computer modelling 16.12 (1992): 83-98.
 * </pre>
 *
 * @author cl
 */
public class SphereVolumeEstimaton {

    private static final Logger logger = LoggerFactory.getLogger(SphereVolumeEstimaton.class);

    private static final int DEFAULT_CUBE_SIDE_LENGTH = 200;

    private List<Sphere> spheres;
    private BitPlane xyBitPlane;

    private List<BitPlane> slices;

    private long volume = 0;

    private double[] xx;
    private double[] yy;
    private double[] zz;
    private double[] rr;

    private int[] scaledX;
    private int[] scaledY;
    private int[] scaledZ;
    private int[] scaledR;

    private double xMin;
    private double yMin;
    private double zMin;

    private double xMax;
    private double yMax;
    private double zMax;

    private int cubesSideLength = DEFAULT_CUBE_SIDE_LENGTH;

    private int numberOfSpheres;
    private double scale;

    public SphereVolumeEstimaton() {
        slices = new ArrayList<>();
    }

    private void initialize() {
        xyBitPlane = new BitPlane(cubesSideLength, cubesSideLength);
        numberOfSpheres = spheres.size();
        xx = new double[numberOfSpheres];
        yy = new double[numberOfSpheres];
        zz = new double[numberOfSpheres];
        rr = new double[numberOfSpheres];
        scaledX = new int[numberOfSpheres];
        scaledY = new int[numberOfSpheres];
        scaledZ = new int[numberOfSpheres];
        scaledR = new int[numberOfSpheres];
        initializeBoundaries();
        initializeArrays();
    }

    private void initializeArrays() {
        logger.debug("Initializing arrays...");
        for (int i = 0; i < spheres.size(); i++) {
            final Sphere sphere = spheres.get(i);
            xx[i] = sphere.getCenter().getX();
            yy[i] = sphere.getCenter().getY();
            zz[i] = sphere.getCenter().getZ();
            rr[i] = sphere.getRadius();
        }
    }

    private void initializeBoundaries() {
        logger.debug("Initializing system boundaries ...");
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

    public static double predict(List<Sphere> spheres) {
        SphereVolumeEstimaton abacus = new SphereVolumeEstimaton();
        abacus.spheres = spheres;
        return abacus.calculate();
    }

    public double calculate() {
        logger.info("Using Abacus algorithm to estimate volume of {} spheres in a {} side length cube.", spheres.size(), cubesSideLength);
        initialize();
        scale();
        volume = 0;
        for (int zSlice = 0; zSlice < cubesSideLength; zSlice++) {
            xyBitPlane = new BitPlane(cubesSideLength, cubesSideLength);
            createPlane(zSlice);
            trace();
        }
        final double scaledVolume = volume / (scale * scale * scale);
        logger.info("Predicted volume of {}.", scaledVolume);
        return scaledVolume;
    }

    private void scale() {
        logger.debug("Scaling system ...");
        // get largest extend
        final double width = Math.abs(xMax - xMin);
        final double height = Math.abs(yMax - yMin);
        final double depth = Math.abs(zMax - zMin);
        logger.debug("Width (x): {}, Height (y): {}, Depth (z): {}.", width, height, depth);
        // scale between 0 and cube side length
        // get min max coordinates
        double scaleMin;
        double scaleMax;
        if (width > height && width > depth) {
            scaleMin = xMin;
            scaleMax = xMax;
            scale = cubesSideLength / width;
        } else if (height > width && height > depth) {
            scaleMin = yMin;
            scaleMax = yMax;
            scale = cubesSideLength / height;
        } else {
            scaleMin = zMin;
            scaleMax = zMax;
            scale = cubesSideLength / depth;
        }

        logger.info("Using scale between {} and {}, resulting in a final scaling factor of {}.", scaleMin, scaleMax, scale);
        for (int i = 0; i < numberOfSpheres; i++) {
            scaledX[i] = (int) Math.round((xx[i] - xMin) * scale);
            scaledY[i] = (int) Math.round((yy[i] - yMin) * scale);
            scaledZ[i] = (int) Math.round((zz[i] - zMin) * scale);
            scaledR[i] = (int) Math.round(rr[i] * scale);
        }

    }

    public void trace() {
        for (int x = 0; x < cubesSideLength; x++) {
            for (int y = 0; y < cubesSideLength; y++) {
                if (xyBitPlane.getBit(x, y)) {
                    volume++;
                    // calc surface here
                }
            }
        }
    }

    private void createPlane(int zSlice) {
        // zi is the layer in z dimension
        for (int i = 0; i < numberOfSpheres; i++) {
            // squared distance of the current slice to the sphere
            int sliceDistance = zSlice - scaledZ[i];
            // radius
            int sphereRadius = scaledR[i];
            // calculate squared center distance
            double squaredCenterDistance = sphereRadius * sphereRadius - sliceDistance * sliceDistance;
            // check if layer intersects the current sphere ((z - z0)² <= r² = r)
            if (squaredCenterDistance > 0) {
                createCircle(i, squaredCenterDistance);
            }
        }
        slices.add(xyBitPlane);
    }

    private void createCircle(int i, double squaredCenterDistance) {
        double centerDistance = Math.sqrt(squaredCenterDistance);
        for (int x = (int) Math.round(scaledX[i] - centerDistance); x <= scaledX[i]; x++) {
            double dist = x - scaledX[i];
            double d = squaredCenterDistance - dist * dist;
            if (d > 0.0) {
                int w = (int) Math.sqrt(d);
                int y1 = scaledY[i] + w;
                int y2 = scaledY[i] - w;
                int ix1 = scaledX[i] + scaledX[i] - x;
                for (int iy = y2; iy < y1; iy++) {
                    // TODO ust setFromTo to do this
                    xyBitPlane.setBit(x, iy);
                    xyBitPlane.setBit(ix1, iy);
                }
            }
        }
    }

    public List<Sphere> getSpheres() {
        return spheres;
    }

    public void setSpheres(List<Sphere> spheres) {
        this.spheres = spheres;
    }

    public int getCubesSideLength() {
        return cubesSideLength;
    }

    public void setCubesSideLength(int cubesSideLength) {
        this.cubesSideLength = cubesSideLength;
    }

    public List<BitPlane> getSlices() {
        return slices;
    }

    public double getScale() {
        return scale;
    }

    public double getxMin() {
        return xMin;
    }

    public double getyMin() {
        return yMin;
    }

    public double getzMin() {
        return zMin;
    }
}
