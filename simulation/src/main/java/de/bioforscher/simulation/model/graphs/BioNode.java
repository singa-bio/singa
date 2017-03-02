package de.bioforscher.simulation.model.graphs;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.mathematics.graphs.model.AbstractNode;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.model.compartments.NodeState;
import de.bioforscher.units.quantities.MolarConcentration;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.bioforscher.units.UnitProvider.MOLE_PER_LITRE;

public class BioNode extends AbstractNode<BioNode, Vector2D> {

    private NodeState state;
    private ConcentrationContainer concentrations;
    private boolean isObserved;

    private String compartmentIdentifier;

    public BioNode(int identifier) {
        super(identifier);
        this.state = NodeState.AQUEOUS;
        this.compartmentIdentifier = "default";
        this.concentrations = new MultiConcentrationContainer(this.compartmentIdentifier);
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
        setConcentration(entity, Quantities.getQuantity(value, MOLE_PER_LITRE));
    }

    public Map<ChemicalEntity, Quantity<MolarConcentration>> getAllConcentrations() {
        return this.concentrations.getAllConcentrations();
    }

    public Quantity<MolarConcentration> getConcentration(ChemicalEntity entity) {
        return this.concentrations.getConcentration(entity);
    }

    public Set<ChemicalEntity> getAllReferencedEntities() {
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

    public String getCompartmentIdentifier() {
        return this.compartmentIdentifier;
    }

    public void setCompartmentIdentifier(String compartmentIdentifier) {
        this.compartmentIdentifier = compartmentIdentifier;
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
