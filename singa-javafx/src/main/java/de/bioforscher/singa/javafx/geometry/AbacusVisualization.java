package de.bioforscher.singa.javafx.geometry;

import de.bioforscher.singa.mathematics.algorithms.geometry.BitPlane;
import de.bioforscher.singa.mathematics.geometry.bodies.Cube;
import de.bioforscher.singa.mathematics.topology.grids.UnitCubeCoordinate;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class AbacusVisualization {

    private List<BitPlane> slices;
    private double scale;
    private double xMin;
    private double yMin;
    private double zMin;

    private Map<UnitCubeCoordinate, Cube> unitCubes;

    public AbacusVisualization(List<BitPlane> slices, double scale, double xMin, double yMin, double zMin) {
        this.slices = slices;
        this.scale = scale;
        this.xMin = xMin;
        this.yMin = yMin;
        this.zMin = zMin;
        unitCubes = new HashMap<>();
        generateUnitCubes();
    }

    private void generateUnitCubes() {
        for (int z = 0; z < slices.size(); z++) {
            BitPlane slice = slices.get(z);
            for (int x = 0; x < slice.getWidth(); x++) {
                for (int y = 0; y < slice.getWidth(); y++) {
                    if (slice.getBit(x,y)) {
                        double cubeX = x / scale + xMin;
                        double cubeY = y / scale + yMin;
                        double cubeZ = z / scale + zMin;
                        double cubeSideLength = 0.15;
                        unitCubes.put(new UnitCubeCoordinate(x, y, z), new Cube(new Vector3D(cubeX, cubeY, cubeZ), cubeSideLength));
                    }
                }
            }
        }
    }

    public List<Cube> getAllUnitCubes() {
        return new ArrayList<>(unitCubes.values());
    }

    public List<Cube> getXSlice(int x) {
        return unitCubes.entrySet().stream()
                .filter(entity -> entity.getKey().getX() == x)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public List<Cube> getYSlice(int y) {
        return unitCubes.entrySet().stream()
                .filter(entity -> entity.getKey().getY() == y)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public List<Cube> getZSlice(int z) {
        return unitCubes.entrySet().stream()
                .filter(entity -> entity.getKey().getX() == z)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

}
