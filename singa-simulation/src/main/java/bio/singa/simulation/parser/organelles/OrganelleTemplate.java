package bio.singa.simulation.parser.organelles;

import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.sections.CellRegion;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.*;

/**
 * @author cl
 */
public class OrganelleTemplate {

    private Quantity<Length> scale;
    private Polygon polygon;
    private Map<Integer, Set<Vector2D>> groups;
    private Map<Integer, CellRegion> regionMap;

    public OrganelleTemplate(Quantity<Length> scale, Polygon polygon, Map<Integer, Set<Vector2D>> groups) {
        this.scale = scale;
        this.polygon = polygon;
        this.groups = groups;
        regionMap = new HashMap<>();
    }

    public Quantity<Length> getScale() {
        return scale;
    }

    public void setScale(Quantity<Length> scale) {
        this.scale = scale;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void scale(double scalingFactor) {
        polygon.scale(scalingFactor);
        for (Map.Entry<Integer, Set<Vector2D>> entry : groups.entrySet()) {
            Set<Vector2D> scaledVectors = new HashSet<>();
            for (Vector2D vertex : entry.getValue()) {
                scaledVectors.add(vertex.multiply(scalingFactor));
            }
            entry.setValue(scaledVectors);
        }
    }

    public void move(Vector2D targetLocation) {
        polygon.move(targetLocation);
        Vector2D displacement = targetLocation.subtract(polygon.getCentroid());
        for (Map.Entry<Integer, Set<Vector2D>> entry : groups.entrySet()) {
            Set<Vector2D> movedVertices = new HashSet<>();
            for (Vector2D vertex : entry.getValue()) {
                movedVertices.add(vertex.add(displacement));
            }
            entry.setValue(movedVertices);
        }


    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Map<Integer, Set<Vector2D>> getGroups() {
        return groups;
    }

    public void setGroups(Map<Integer, Set<Vector2D>> groups) {
        this.groups = groups;
    }

    public Collection<CellRegion> getRegions() {
        return regionMap.values();
    }

    public boolean isGrouped() {
        return !regionMap.isEmpty();
    }

    public void initializeGroup(int groupIdentifier, CellRegion region) {
        regionMap.put(groupIdentifier, region);
    }

    public CellRegion getRegion(Vector2D vector) {
        for (Map.Entry<Integer, Set<Vector2D>> entry : groups.entrySet()) {
            if (entry.getValue().contains(vector)) {
                return regionMap.get(entry.getKey());
            }
        }
        throw new IllegalArgumentException("The segment is not contained in this membrane");
    }

}
