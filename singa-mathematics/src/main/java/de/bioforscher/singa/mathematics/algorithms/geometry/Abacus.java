package de.bioforscher.singa.mathematics.algorithms.geometry;

import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class Abacus {

    private static final Logger logger = LoggerFactory.getLogger(Abacus.class);

    private List<Sphere> spheres;
    private BitPlane xyBitPlane;

    private List<BitPlane> slices;

    private int volume = 0;

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

    private int cubesSideLength = 100;

    private final int numberOfSpheres;
    private double scale;

    public Abacus(List<Sphere> spheres) {
        this.spheres = spheres;
        slices = new ArrayList<>();
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
        volume = 0;
        for (int zSlice = 0; zSlice < cubesSideLength; zSlice++) {
            xyBitPlane = new BitPlane(cubesSideLength, cubesSideLength);
            createPlane(zSlice);
            trace();
        }
        // TODO determine correct factor
        System.out.println("Calculated: " + volume / 10);
    }

    public void scale() {
        // get largest extend
        final double width = Math.abs(xMax - xMin);
        final double height = Math.abs(yMax - yMin);
        final double depth = Math.abs(zMax - zMin);

        // scale between 0 and cube side length
        // get min max coordinates
        double scaleMin;
        double scaleMax;
        if (width > height && width > depth) {
            scaleMin = xMin;
            scaleMax = xMax;
        } else if (height > width && height > depth) {
            scaleMin = yMin;
            scaleMax = yMax;
        } else {
            scaleMin = zMin;
            scaleMax = zMax;
        }

        scale = cubesSideLength / (scaleMax - scaleMin);

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
