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
import de.bioforscher.singa.simulation.model.modules.macroscopic.filaments.SkeletalFilament;
import de.bioforscher.singa.simulation.model.sections.CellRegion;
import de.bioforscher.singa.simulation.model.sections.CellSubsection;
import de.bioforscher.singa.simulation.model.sections.CellTopology;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.simulation.Updatable;

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

    public enum AttachmentState {
        ACTIN_DEPOLYMERIZATION, MICROTUBULE, UNATTACHED
    }

    public enum TargetDirection {
        PLUS, MINUS
    }

    private static AtomicInteger vesicleCounter = new AtomicInteger();

    protected static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        availableFeatures.add(Diffusivity.class);
    }

    private String identifier;
    private Quantity<Length> radius;
    private Quantity<Area> area;
    private Quantity<Volume> volume;

    protected FeatureContainer features;

    private ConcentrationDeltaManager concentrationManager;
    private DisplacementDeltaManager displacementManager;

    private final CellRegion region;
    private Map<AutomatonNode, Quantity<Area>> associatedNodes;

    private AttachmentState attachmentState;
    private TargetDirection targetDirection;
    private SkeletalFilament attachedFilament;
    private ListIterator<Vector2D> segmentIterator;

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
     * The area of a sphere is calculated by
     * V = 4/3 * pi * radius * radius * radius
     *
     * @param radius the radius of the vesicle
     * @return The area.
     */
    private static Quantity<Area> calculateArea(Quantity<Length> radius) {
        return radius.multiply(radius).multiply(Math.PI).multiply(4.0).asType(Area.class);
    }

    public Vesicle(String identifier, Vector2D position, Quantity<Length> radius) {
        this.identifier = identifier;
        features = new ChemistryFeatureContainer();
        setRadius(radius);
        region = CellRegion.forVesicle(identifier);
        concentrationManager = new ConcentrationDeltaManager(region.setUpConcentrationContainer());
        displacementManager = new DisplacementDeltaManager(position);
        associatedNodes = new HashMap<>();
    }

    public Vesicle(Vector2D position, Quantity<Length> radius) {
        this("Vesicle " + vesicleCounter.getAndIncrement(), position, radius);
    }

    @Override
    public String getStringIdentifier() {
        return identifier;
    }

    public Vector2D getCurrentPosition() {
        return displacementManager.getCurrentPosition();
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

    public Vector2D getNextPosition() {
        return displacementManager.getNextPosition();
    }

    public AttachmentState getAttachmentState() {
        return attachmentState;
    }

    public void setAttachmentState(AttachmentState attachmentState) {
        this.attachmentState = attachmentState;
    }

    public SkeletalFilament getAttachedFilament() {
        return attachedFilament;
    }

    public void setAttachedFilament(SkeletalFilament attachedFilament) {
        this.attachedFilament = attachedFilament;
    }

    public TargetDirection getTargetDirection() {
        return targetDirection;
    }

    public void setTargetDirection(TargetDirection targetDirection) {
        this.targetDirection = targetDirection;
    }

    public ListIterator<Vector2D> getSegmentIterator() {
        return segmentIterator;
    }

    public void setSegmentIterator(ListIterator<Vector2D> segmentIterator) {
        this.segmentIterator = segmentIterator;
    }

    public void addPotentialSpatialDelta(DisplacementDelta spatialDelta) {
        displacementManager.addPotentialSpatialDelta(spatialDelta);
    }

    public DisplacementDelta getSpatialDelta(DisplacementBasedModule module) {
        return displacementManager.getPotentialSpatialDelta(module);
    }

    public void setRadius(Quantity<Length> radius) {
        this.radius = radius;
        area = calculateArea(radius);
        volume = calculateVolume(radius);
        setFeature(Diffusivity.calculate(radius));
    }

    public void setConcentration(ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        concentrationManager.getConcentrationContainer().set(region.getInnerSubsection(), entity, concentration);
    }

    public Quantity<MolarConcentration> getConcentration(ChemicalEntity entity) {
        return concentrationManager.getConcentrationContainer().get(region.getInnerSubsection(), entity);
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
        return displacementManager.calculateTotalDisplacement();
    }

    public void clearPotentialDisplacementDeltas() {
        displacementManager.clearPotentialDisplacementDeltas();
    }

    public void resetNextPosition() {
        displacementManager.resetNextPosition();
    }

    public void updatePosition() {
        displacementManager.updatePosition();
    }

    /**
     * Returns {@code true} if this node is observed.
     *
     * @return {@code true} if this node is observed.
     */
    public boolean isObserved() {
        return concentrationManager.isObserved();
    }

    /**
     * Sets the observed state of this node.
     *
     * @param isObserved {@code true} if this node is observed.
     */
    public void setObserved(boolean isObserved) {
        concentrationManager.setObserved(isObserved);
    }

    @Override
    public ConcentrationContainer getConcentrationContainer() {
        return concentrationManager.getConcentrationContainer();
    }

    @Override
    public Quantity<MolarConcentration> getConcentration(CellSubsection cellSection, ChemicalEntity chemicalEntity) {
        return concentrationManager.getConcentrationContainer().get(cellSection, chemicalEntity);
    }

    @Override
    public CellRegion getCellRegion() {
        return region;
    }

    @Override
    public Set<CellSubsection> getAllReferencedSections() {
        return concentrationManager.getConcentrationContainer().getReferencedSubSections();
    }

    @Override
    public List<ConcentrationDelta> getPotentialSpatialDeltas() {
        return concentrationManager.getPotentialDeltas();
    }

    @Override
    public void addPotentialDelta(ConcentrationDelta delta) {
        concentrationManager.addPotentialDelta(delta);
    }

    @Override
    public void clearPotentialConcentrationDeltas() {
        concentrationManager.clearPotentialDeltas();
    }

    @Override
    public void clearPotentialDeltasBut(UpdateModule module) {
        concentrationManager.clearPotentialDeltasBut(module);
        clearPotentialDisplacementDeltas();
    }

    @Override
    public boolean hasDeltas() {
        return !concentrationManager.getFinalDeltas().isEmpty();
    }

    @Override
    public void shiftDeltas() {
        concentrationManager.shiftDeltas();
    }

    @Override
    public void applyDeltas() {
        concentrationManager.applyDeltas();
    }

    public Circle getCircleRepresentation() {
        return new Circle(displacementManager.getCurrentPosition(), Environment.convertSystemToSimulationScale(radius));
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
        return "Vesicle: " + identifier + " radius = " + radius + " " + " position = " + displacementManager.getCurrentPosition();
    }

}
