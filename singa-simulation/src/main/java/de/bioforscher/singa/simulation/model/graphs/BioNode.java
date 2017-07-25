package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.graphs.model.AbstractNode;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.concentrations.DeltaContainer;
import de.bioforscher.singa.simulation.model.concentrations.SimpleConcentrationContainer;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.compartments.NodeState.AQUEOUS;
import static de.bioforscher.singa.simulation.model.compartments.NodeState.MEMBRANE;

public class BioNode extends AbstractNode<BioNode, Vector2D> {

    private NodeState state;
    private CellSection cellSection;
    private ConcentrationContainer concentrations;
    private DeltaContainer deltas;
    private List<Delta> potentialDeltas;
    private boolean isObserved;

    public BioNode(int identifier) {
        super(identifier);
        this.state = AQUEOUS;
        this.cellSection = new EnclosedCompartment("default", "Default Compartment");
        this.concentrations = new SimpleConcentrationContainer(cellSection);
        this.deltas = new DeltaContainer();
        this.potentialDeltas = new ArrayList<>();
    }

    public void setConcentrations(double concentration, ChemicalEntity... entities) {
        for (ChemicalEntity entity : entities) {
            setConcentration(entity, concentration);
        }
    }

    public void setConcentration(ChemicalEntity entity, Quantity<MolarConcentration> quantity) {
        this.concentrations.setConcentration(entity, quantity);
    }

    public void setConcentration(ChemicalEntity entity, double value) {
        // FIXME This is ignored if no compartment is set
        setConcentration(entity, Quantities.getQuantity(value, MOLE_PER_LITRE));
    }

    public Map<ChemicalEntity<?>, Quantity<MolarConcentration>> getAllConcentrations() {
        return this.concentrations.getAllConcentrations();
    }

    public Map<ChemicalEntity<?>, Quantity<MolarConcentration>> getAllConcentrationsForSection(CellSection cellSection) {
        return this.concentrations.getAllConcentrationsForSection(cellSection);
    }

    public Quantity<MolarConcentration> getConcentration(ChemicalEntity entity) {
        return this.concentrations.getConcentration(entity);
    }

    public void setAvailableConcentration(ChemicalEntity entity, CellSection cellSection, Quantity<MolarConcentration> quantity) {
        this.concentrations.setAvailableConcentration(cellSection, entity, quantity);
    }

    public Quantity<MolarConcentration> getAvailableConcentration(ChemicalEntity entity, CellSection cellSection) {
        return this.concentrations.getAvailableConcentration(cellSection, entity);
    }

    public List<Delta> getDeltas() {
        return this.deltas.getDeltas();
    }

    public void addPotentialDeltas(List<Delta> potentialDeltas) {
        this.potentialDeltas.addAll(potentialDeltas);
    }

    public void addPotentialDelta(Delta potentialDelta) {
        this.potentialDeltas.add(potentialDelta);
    }

    public void clearPotentialDeltas() {
        this.potentialDeltas.clear();
    }

    public void addDelta(Delta delta) {
        this.deltas.addDelta(delta);
    }

    public void shiftDeltas() {
        this.potentialDeltas.forEach(this::addDelta);
        this.potentialDeltas.clear();
    }

    public void applyDeltas() {
        for (Delta delta : this.deltas.getDeltas()) {
            setAvailableConcentration(delta.getEntity(), delta.getCellSection(),
                    getAvailableConcentration(delta.getEntity(), delta.getCellSection()).add(delta.getQuantity()));
        }
        this.deltas.clear();
    }

    public Set<CellSection> getAllReferencedSections() {
        return this.concentrations.getAllReferencedSections();
    }

    public Set<ChemicalEntity<?>> getAllReferencedEntities() {
        return this.concentrations.getAllReferencedEntities();
    }

    public NodeState getState() {
        return this.state;
    }

    public void setState(NodeState state) {
        this.state = state;
    }

    public boolean isObserved() {
        return this.isObserved;
    }

    public void setObserved(boolean isObserved) {
        this.isObserved = isObserved;
    }

    public CellSection getCellSection() {
        return this.cellSection;
    }

    public void setCellSection(CellSection cellSection) {
        if (cellSection instanceof Membrane) {
            setState(MEMBRANE);
        }
        this.cellSection = cellSection;
        this.concentrations = new SimpleConcentrationContainer(cellSection);
        this.cellSection.addNode(this);
    }

    public ConcentrationContainer getConcentrations() {
        return this.concentrations;
    }

    public void setConcentrations(ConcentrationContainer concentrations) {
        this.concentrations = concentrations;
    }

    double getSteepestConcentrationDifference(ChemicalEntity entity) {
        return this.getNeighbours().stream()
                .mapToDouble(neighbour ->
                        Math.abs(this.getConcentration(entity).getValue().doubleValue() -
                                neighbour.getConcentration(entity).getValue().doubleValue()))
                .max().orElse(0.0);
    }

    @Override
    public String toString() {
        return "BioNode [id=" + this.getIdentifier() + "]";
    }

}
