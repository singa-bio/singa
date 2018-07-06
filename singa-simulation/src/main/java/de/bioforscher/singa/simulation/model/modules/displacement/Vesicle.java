package de.bioforscher.singa.simulation.model.modules.displacement;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.features.ChemistryFeatureContainer;
import de.bioforscher.singa.chemistry.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.FeatureContainer;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.geometry.faces.Circle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.modules.UpdateModule;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDeltaManager;
import de.bioforscher.singa.simulation.model.sections.CellRegion;
import de.bioforscher.singa.simulation.model.sections.CellSubsection;
import de.bioforscher.singa.simulation.model.sections.CellTopology;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.simulation.Updatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Volume;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cl
 */
public class Vesicle implements Updatable, Featureable {

    private static final Logger logger = LoggerFactory.getLogger(Vesicle.class);
    private static AtomicInteger vesicleCounter = new AtomicInteger();

    protected static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        availableFeatures.add(Diffusivity.class);
    }

    private String identifier;
    private Quantity<Length> radius;
    private Quantity<Area> area;
    private Quantity<Volume> volume;
    private double clathrins;

    protected FeatureContainer features;

    private ConcentrationDeltaManager updateManager;
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
        updateManager = new ConcentrationDeltaManager(region.setUpConcentrationContainer());
        associatedNodes = new HashMap<>();
    }

    public Vesicle(Vector2D position, Quantity<Length> radius) {
        this("Vesicle "+vesicleCounter.getAndIncrement(), position, radius);
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
        // relation between clathrins and radius = r^2 * 60/50^2
        clathrins = radius.multiply(radius).multiply(60.0/Math.pow(50.0,2)).getValue().doubleValue();
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

    /**
     * Returns {@code true} if this node is observed.
     *
     * @return {@code true} if this node is observed.
     */
    public boolean isObserved() {
        return updateManager.isObserved();
    }

    /**
     * Sets the observed state of this node.
     *
     * @param isObserved {@code true} if this node is observed.
     */
    public void setObserved(boolean isObserved) {
        updateManager.setObserved(isObserved);
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
    public List<ConcentrationDelta> getPotentialSpatialDeltas() {
        return updateManager.getPotentialDeltas();
    }

    @Override
    public void addPotentialDelta(ConcentrationDelta delta) {
        updateManager.addPotentialDelta(delta);
    }

    @Override
    public void clearPotentialConcentrationDeltas() {
        updateManager.clearPotentialDeltas();
    }

    @Override
    public void clearPotentialDeltasBut(UpdateModule module) {
        updateManager.clearPotentialDeltasBut(module);
        clearPotentialSpatialDeltas();
    }

    @Override
    public boolean hasDeltas() {
        return !updateManager.getFinalDeltas().isEmpty();
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
