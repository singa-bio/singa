package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaManager;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellSubsections;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;

/**
 * @author cl
 */
public class EndocytoticPit implements Updatable {

    public static AtomicInteger pitCounter = new AtomicInteger();

    private int identifier;
    private CellRegion cellRegion;
    private Quantity<Length> radius;
    private Quantity<Time> checkpointTime;
    private Vector2D spawnSite;
    private ConcentrationDeltaManager concentrationDeltaManager;
    private AutomatonNode associatedNode;

    private boolean isCollecting;

    public EndocytoticPit(AutomatonNode associatedNode) {
        this.associatedNode = associatedNode;
        identifier = pitCounter.getAndIncrement();
        initializeSubsections();
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    private void initializeSubsections() {
        ConcentrationContainer container = new ConcentrationContainer();
        container.initializeSubsection(CellSubsections.PIT_MEMBRANE, MEMBRANE);
        concentrationDeltaManager = new ConcentrationDeltaManager(container);
    }

    public void initializeConcentrations(List<ChemicalEntity> entities, UpdateModule creator) {
        // determine area of the pit
        double pitArea = radius.multiply(radius).multiply(Math.PI).getValue().doubleValue();
        // determine total area of the membrane
        double totalArea = associatedNode.getMembraneArea().to(UnitRegistry.getAreaUnit()).getValue().doubleValue();
        for (ChemicalEntity entity : entities) {
            // determine cargoes that are in the pit upon creation
            double membraneConcentration = associatedNode.getConcentrationContainer().get(MEMBRANE, entity);
            double concentration = pitArea * membraneConcentration / totalArea;
            // add to pit
            getConcentrationDeltaManager().getFinalDeltas().add(new ConcentrationDelta(creator, getCellRegion().getMembraneSubsection(), entity, concentration));
            // remove from membrane
            associatedNode.getConcentrationManager().getFinalDeltas().add(new ConcentrationDelta(creator, associatedNode.getCellRegion().getMembraneSubsection(), entity, -concentration));
        }
    }

    public Quantity<Time> getCheckpointTime() {
        return checkpointTime;
    }

    public void setCheckpointTime(Quantity<Time> checkpointTime) {
        this.checkpointTime = checkpointTime;
    }

    public Vector2D getSpawnSite() {
        return spawnSite;
    }

    public void setSpawnSite(Vector2D spawnSite) {
        this.spawnSite = spawnSite;
    }

    public ConcentrationDeltaManager getConcentrationDeltaManager() {
        return concentrationDeltaManager;
    }

    public void setConcentrationDeltaManager(ConcentrationDeltaManager concentrationDeltaManager) {
        this.concentrationDeltaManager = concentrationDeltaManager;
    }

    public AutomatonNode getAssociatedNode() {
        return associatedNode;
    }

    @Override
    public String getStringIdentifier() {
        return "p" + identifier;
    }

    @Override
    public Vector2D getPosition() {
        return spawnSite;
    }

    @Override
    public ConcentrationDeltaManager getConcentrationManager() {
        return concentrationDeltaManager;
    }

    @Override
    public ConcentrationContainer getConcentrationContainer() {
        return concentrationDeltaManager.getConcentrationContainer();
    }

    @Override
    public void addPotentialDelta(ConcentrationDelta potentialDelta) {
        concentrationDeltaManager.addPotentialDelta(potentialDelta);
    }

    @Override
    public CellRegion getCellRegion() {
        return cellRegion;
    }

    public void setCellRegion(CellRegion cellRegion) {
        this.cellRegion = cellRegion;
    }

    @Override
    public Set<CellSubsection> getAllReferencedSections() {
        return concentrationDeltaManager.getConcentrationContainer().getReferencedSubsections();
    }

    public Quantity<Length> getRadius() {
        return radius;
    }

    public void setRadius(Quantity<Length> radius) {
        this.radius = radius;
    }

    public boolean isCollecting() {
        return isCollecting;
    }

    public void setCollecting(boolean collecting) {
        isCollecting = collecting;
    }
}
