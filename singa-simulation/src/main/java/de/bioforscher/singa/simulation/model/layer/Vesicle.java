package de.bioforscher.singa.simulation.model.layer;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.ChemistryFeatureContainer;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.FeatureContainer;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.geometry.faces.Circle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.newsections.CellTopology;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.SimpleUpdateManager;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;
import de.bioforscher.singa.simulation.modules.newmodules.module.DisplacementBasedModule;
import de.bioforscher.singa.simulation.modules.newmodules.module.UpdateModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Volume;
import java.util.*;

/**
 * @author cl
 */
public class Vesicle implements Updatable, Featureable {

    private static final Logger logger = LoggerFactory.getLogger(Vesicle.class);

    private static final FeatureOrigin EINSTEIN1905 = new FeatureOrigin(FeatureOrigin.OriginType.PREDICTION, "Strokes-Einstein Equation", "Einstein, Albert. \"Über die von der molekularkinetischen Theorie der Wärme geforderte Bewegung von in ruhenden Flüssigkeiten suspendierten Teilchen.\" Annalen der physik 322.8 (1905): 549-560.");

    protected static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        availableFeatures.add(Diffusivity.class);
    }

    private String identifier;
    private Quantity<Length> radius;
    private Quantity<Area> area;
    private Quantity<Volume> volume;

    protected FeatureContainer features;

    private SimpleUpdateManager updateManager;
    private final CellRegion region;
    private Map<AutomatonNode, Quantity<Area>> associatedNodes;

    private Vector2D position;
    private List<SpatialDelta> potentialSpatialDeltas;
    private Vector2D potentialUpdate;

    /**
     * The volume of a sphere is calculated by
     * V = 4/3 * pi * radius * radius * radius
     *
     * @param radius the radius of the vesicle
     * @return The volume.
     */
    private static Quantity<Volume> calculateVolume(Quantity<Length> radius) {
        return radius.multiply(radius).multiply(radius).multiply(Math.PI).multiply(4.0 / 3.0).asType(Volume.class);
    }

    /**
     * The volume of a sphere is calculated by
     * V = 4/3 * pi * radius * radius * radius
     *
     * @param radius the radius of the vesicle
     * @return The volume.
     */
    private static Quantity<Area> calculateArea(Quantity<Length> radius) {
        return radius.multiply(radius).multiply(Math.PI).multiply(4.0).asType(Area.class);
    }

    public Vesicle(String identifier, Vector2D position, Quantity<Length> radius) {
        this.identifier = identifier;
        this.position = position;
        potentialSpatialDeltas = new ArrayList<>();
        features = new ChemistryFeatureContainer();
        setRadius(radius);
        region = CellRegion.forVesicle(identifier);
        updateManager = new SimpleUpdateManager(region.setUpConcentrationContainer());
        associatedNodes = new HashMap<>();
    }

    public Vector2D getPosition() {
        return position;
    }

    public Quantity<Length> getRadius() {
        return radius;
    }

    public Quantity<Area> getArea() {
        return area;
    }

    public Quantity<Volume> getVolume() {
        return volume;
    }

    public Vector2D getPotentialUpdate() {
        return potentialUpdate;
    }

    public void addPotentialSpatialDelta(SpatialDelta spatialDelta) {
        potentialSpatialDeltas.add(spatialDelta);
    }

    public SpatialDelta getSpatialDelta(DisplacementBasedModule module) {
        for (SpatialDelta potentialSpatialDelta : potentialSpatialDeltas) {
            if (potentialSpatialDelta.getModule().equals(module)) {
                return potentialSpatialDelta;
            }
        }
        return null;
    }

    public void setRadius(Quantity<Length> radius) {
        this.radius = radius;
        area = calculateArea(radius);
        volume = calculateVolume(radius);
        setFeature(Diffusivity.calculate(radius));
    }

    public void setConcentration(ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        updateManager.getConcentrationContainer().set(region.getInnerSubsection(), entity, concentration);
    }

    public Quantity<MolarConcentration> getConcentration(ChemicalEntity entity) {
        return updateManager.getConcentrationContainer().get(region.getInnerSubsection(), entity);
    }

    public Map<AutomatonNode, Quantity<Area>> getAssociatedNodes() {
        return associatedNodes;
    }

    public void addAssociatedNode(AutomatonNode node, Quantity<Area> associatedArea) {
        associatedNodes.put(node, associatedArea);
        CellSubsection subsection = node.getConcentrationContainer().getSubsection(CellTopology.INNER);
        getConcentrationContainer().putSubsectionPool(subsection, CellTopology.OUTER, node.getConcentrationContainer().getPool(CellTopology.INNER).getValue());
    }

    public void clearAssociatedNodes() {
        getConcentrationContainer().removeSubsection(CellTopology.OUTER);
        associatedNodes.clear();
    }

    public Vector2D calculateTotalDisplacement() {
        Vector2D sum = new Vector2D(0.0,0.0);
        for (SpatialDelta potentialSpatialDelta : potentialSpatialDeltas) {
            sum = sum.add(potentialSpatialDelta.getDeltaVector());
        }
        potentialUpdate = position.add(sum);
        return sum;
    }

    public void clearPotentialSpatialDeltas() {
        potentialSpatialDeltas.clear();
    }

    public void permitDisplacement() {
        potentialUpdate = position;
    }

    public void move() {
        logger.trace("Moving vesicle from {} to {}.", position, potentialUpdate);
        position = potentialUpdate;
    }

    @Override
    public String getStringIdentifier() {
        return "Vesicle "+identifier;
    }

    @Override
    public ConcentrationContainer getConcentrationContainer() {
        return updateManager.getConcentrationContainer();
    }

    @Override
    public Quantity<MolarConcentration> getConcentration(CellSubsection cellSection, ChemicalEntity chemicalEntity) {
        return updateManager.getConcentrationContainer().get(cellSection, chemicalEntity);
    }

    @Override
    public CellRegion getCellRegion() {
        return region;
    }

    @Override
    public Set<CellSubsection> getAllReferencedSections() {
        return updateManager.getConcentrationContainer().getReferencedSubSections();
    }

    @Override
    public List<Delta> getPotentialSpatialDeltas() {
        return updateManager.getPotentialDeltas();
    }

    @Override
    public void addPotentialDelta(Delta delta) {
        updateManager.addPotentialDelta(delta);
    }

    @Override
    public void clearPotentialDeltas() {
        updateManager.clearPotentialDeltas();
    }

    @Override
    public void clearPotentialDeltasBut(UpdateModule module) {
        updateManager.clearPotentialDeltasBut(module);
        clearPotentialSpatialDeltas();
    }

    @Override
    public void shiftDeltas() {
        updateManager.shiftDeltas();
    }

    @Override
    public void applyDeltas() {
        updateManager.applyDeltas();
    }

    public Circle getCircleRepresentation() {
        return new Circle(position,Environment.convertSystemToSimulationScale(radius));
    }

    @Override
    public Collection<Feature<?>> getFeatures() {
        return features.getAllFeatures();
    }

    @Override
    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        if (!features.hasFeature(featureTypeClass)) {
            setFeature(featureTypeClass);
        }
        return features.getFeature(featureTypeClass);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(Class<FeatureType> featureTypeClass) {
        features.setFeature(featureTypeClass, this);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(FeatureType feature) {
        features.setFeature(feature);
    }

    @Override
    public <FeatureType extends Feature> boolean hasFeature(Class<FeatureType> featureTypeClass) {
        return features.hasFeature(featureTypeClass);
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    @Override
    public String toString() {
        return "Vesicle{" +
                "radius=" + radius +
                ", position=" + position +
                '}';
    }
}
