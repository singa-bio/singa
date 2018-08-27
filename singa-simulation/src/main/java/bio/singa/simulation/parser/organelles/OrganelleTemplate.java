package bio.singa.simulation.parser.organelles;

import bio.singa.features.identifiers.GoTerm;
import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellTopology;

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
    private CellRegion innerRegion;
    private CellRegion membraneRegion;

    public OrganelleTemplate(Quantity<Length> scale, Polygon polygon, Map<Integer, Set<Vector2D>> groups) {
        this.scale = scale;
        this.polygon = polygon;
        this.groups = groups;
        regionMap = new HashMap<>();
    }

    public void mapToSystemExtend() {
        Quantity<Length> systemScale = Environment.convertSimulationToSystemScale(1).to(scale.getUnit());
        scale(scale.getValue().doubleValue()/systemScale.getValue().doubleValue());
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

    public void reduce() {
        // resize to a handleable number of edges
        while (getPolygon().getVertices().size() > 200) {
            Set<Vector2D> reduce = getPolygon().reduce(1);
            for (Set<Vector2D> values : groups.values()) {
                values.removeAll(reduce);
            }
        }
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

    public void initializeGroup(int groupIdentifier, String identifier, String goTerm) {
        CellRegion region = new CellRegion(identifier, new GoTerm(goTerm));
        region.addSubSection(CellTopology.MEMBRANE, membraneRegion.getMembraneSubsection());
        region.addSubSection(CellTopology.INNER, membraneRegion.getInnerSubsection());
        region.addSubSection(CellTopology.OUTER, membraneRegion.getOuterSubsection());
        initializeGroup(groupIdentifier, region);
    }

    public void initializeGroup(int groupIdentifier, CellRegion region) {
        regionMap.put(groupIdentifier, region);
    }

    public void initializeGroup(CellRegion region) {
        initializeGroup(groups.keySet().iterator().next(), region);
    }

    public CellRegion getInnerRegion() {
        return innerRegion;
    }

    public void setInnerRegion(CellRegion innerRegion) {
        this.innerRegion = innerRegion;
    }

    public CellRegion getMembraneRegion() {
        return membraneRegion;
    }

    public void setMembraneRegion(CellRegion membraneRegion) {
        this.membraneRegion = membraneRegion;
    }

    public CellRegion getRegion(Vector2D vector) {
        for (Map.Entry<Integer, Set<Vector2D>> entry : groups.entrySet()) {
            if (entry.getValue().contains(vector)) {
                return regionMap.get(entry.getKey());
            }
        }
        throw new IllegalArgumentException("The segment is not contained in this membrane");
    }

    public Map<CellRegion, Set<Vector2D>> getRegionMap() {
        Map<CellRegion, Set<Vector2D>> mapping = new HashMap<>();
        for (Map.Entry<Integer, CellRegion> entry : regionMap.entrySet()) {
            Integer key = entry.getKey();
            CellRegion region = entry.getValue();
            mapping.put(region, groups.get(key));
        }
        return mapping;
    }
}
