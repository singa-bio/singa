package de.bioforscher.singa.simulation.model.layer;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.features.quantities.NaturalConstants;
import de.bioforscher.singa.mathematics.geometry.faces.Circle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.SimpleUpdateManager;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Volume;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
public class Vesicle implements Updatable {

    private static final FeatureOrigin EINSTEIN1905 = new FeatureOrigin(FeatureOrigin.OriginType.PREDICTION, "Strokes-Einstein Equation", "Einstein, Albert. \"Über die von der molekularkinetischen Theorie der Wärme geforderte Bewegung von in ruhenden Flüssigkeiten suspendierten Teilchen.\" Annalen der physik 322.8 (1905): 549-560.");

    /**
     * The diffusivity can be calculated according to the Stokes–Einstein equation:
     * D = (k_B * T) / (6 * pi * nu * radius)
     * k_B is the {@link NaturalConstants#BOLTZMANN_CONSTANT} (in (N * m) / K),
     * T is the Temperature (in K),
     * nu is the dynamic viscosity (in (N * s) / m^2 ) and,
     *
     * @param radius the radius of the vesicle
     * @return The diffusivity.
     */
    private static Diffusivity calculateDiffusivity(Quantity<Length> radius) {
        final double upper = NaturalConstants.BOLTZMANN_CONSTANT.getValue().doubleValue() * Environment.getTemperature().getValue().doubleValue();
        final double lower = 6 * Math.PI * Environment.getViscosity().getValue().doubleValue() * radius.to(METRE).getValue().doubleValue();
        Diffusivity diffusivity = new Diffusivity(Quantities.getQuantity(upper / lower, Diffusivity.SQUARE_METRE_PER_SECOND), EINSTEIN1905);
        diffusivity.scale(Environment.getTimeStep(), Environment.getSystemScale());
        return diffusivity;
    }

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

    private String identifier;
    private Quantity<Length> radius;
    private Quantity<Area> area;
    private Quantity<Volume> volume;
    private Diffusivity diffusivity;

    private MembraneContainer concentrations;
    private SimpleUpdateManager updateManager;
    private EnclosedCompartment inside;

    private Vector2D position;
    private List<SpatialDelta> potentialSpatialDeltas;
    private Vector2D potentialUpdate;


    public Vesicle(String identifier, Vector2D position, Quantity<Length> radius, EnclosedCompartment outsideCompartment) {
        this.identifier = identifier;
        this.position = position;
        potentialSpatialDeltas = new ArrayList<>();
        setRadius(radius);
        inside = new EnclosedCompartment("c-" + identifier, "compartment of vesicle " + identifier);
        Membrane membrane = Membrane.forCompartment(inside);
        concentrations = new MembraneContainer(outsideCompartment, inside, membrane);
        updateManager = new SimpleUpdateManager(concentrations);
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

    public void setRadius(Quantity<Length> radius) {
        this.radius = radius;
        area = calculateArea(radius);
        volume = calculateVolume(radius);
        diffusivity = calculateDiffusivity(radius);
    }

    public Diffusivity getDiffusivity() {
        return diffusivity;
    }

    public void setConcentration(ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        concentrations.setAvailableConcentration(inside, entity, Environment.transformToVolume(concentration, volume));
    }

    public Quantity<MolarConcentration> getConcentration(ChemicalEntity entity) {
        return concentrations.getAvailableConcentration(inside, entity);
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
        System.out.println(potentialUpdate);
        position = potentialUpdate;
    }

    @Override
    public String getStringIdentifier() {
        return identifier;
    }

    @Override
    public ConcentrationContainer getConcentrationContainer() {
        return concentrations;
    }

    @Override
    public Quantity<MolarConcentration> getAvailableConcentration(ChemicalEntity chemicalEntity, CellSection cellSection) {
        return concentrations.getAvailableConcentration(cellSection, chemicalEntity);
    }

    @Override
    public Set<CellSection> getAllReferencedSections() {
        return concentrations.getAllReferencedSections();
    }

    @Override
    public Set<ChemicalEntity> getAllReferencedEntities() {
        return concentrations.getAllReferencedEntities();
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

    public void rescaleDiffusivity() {
        diffusivity.scale();
    }

    @Override
    public String toString() {
        return "Vesicle{" +
                "radius=" + radius +
                ", position=" + position +
                '}';
    }
}
