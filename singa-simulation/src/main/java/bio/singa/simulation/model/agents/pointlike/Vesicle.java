package bio.singa.simulation.model.agents.pointlike;

import bio.singa.chemistry.features.ChemistryFeatureContainer;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureContainer;
import bio.singa.features.model.Featureable;
import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.linelike.LineLikeAgent;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaManager;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.modules.displacement.DisplacementDelta;
import bio.singa.simulation.model.modules.displacement.DisplacementDeltaManager;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Volume;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static bio.singa.simulation.features.MotorPullDirection.Direction;
import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.VesicleState;

/**
 * @author cl
 */
public class Vesicle implements Updatable, Featureable {

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

    private CellRegion region;
    private Map<AutomatonNode, Double> associatedNodes;

    private VesicleState vesicleState;
    private Direction targetDirection;
    private LineLikeAgent attachedFilament;
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
        vesicleState = VesicleStateRegistry.UNATTACHED;
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

    public VesicleState getVesicleState() {
        return vesicleState;
    }

    public void setVesicleState(VesicleState vesicleState) {
        this.vesicleState = vesicleState;
    }

    public LineLikeAgent getAttachedFilament() {
        return attachedFilament;
    }

    public void setAttachedFilament(LineLikeAgent attachedFilament) {
        this.attachedFilament = attachedFilament;
    }

    public Direction getTargetDirection() {
        return targetDirection;
    }

    public void setTargetDirection(Direction targetDirection) {
        this.targetDirection = targetDirection;
    }

    public ListIterator<Vector2D> getSegmentIterator() {
        return segmentIterator;
    }

    public void setSegmentIterator(ListIterator<Vector2D> segmentIterator) {
        this.segmentIterator = segmentIterator;
    }

    public CellRegion getRegion() {
        return region;
    }

    public void setRegion(CellRegion region) {
        this.region = region;
    }

    public void addPotentialSpatialDelta(DisplacementDelta spatialDelta) {
        displacementManager.addPotentialDisplacementDelta(spatialDelta);
    }

    public DisplacementDelta getSpatialDelta(DisplacementBasedModule module) {
        return displacementManager.getPotentialDisplacementDelta(module);
    }

    public void setRadius(Quantity<Length> radius) {
        this.radius = radius;
        area = calculateArea(radius);
        volume = calculateVolume(radius);
        setFeature(Diffusivity.calculate(radius));
    }

    public Map<AutomatonNode, Double> getAssociatedNodes() {
        return associatedNodes;
    }

    public void addAssociatedNode(AutomatonNode node, double relativeArea) {
        associatedNodes.put(node, relativeArea);
        // CellSubsection subsection = node.getConcentrationContainer().getSubsection(CellTopology.INNER);
        // getConcentrationContainer().putSubsectionPool(subsection, CellTopology.INNER, node.getConcentrationContainer().getPool(CellTopology.INNER).getValue());
    }

    public void clearAssociatedNodes() {
        getConcentrationContainer().removeSubsection(CellTopology.INNER);
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
    public CellRegion getCellRegion() {
        return region;
    }

    @Override
    public Set<CellSubsection> getAllReferencedSections() {
        return concentrationManager.getConcentrationContainer().getReferencedSubSections();
    }

    @Override
    public List<ConcentrationDelta> getPotentialConcentrationDeltas() {
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
