package de.bioforscher.simulation.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.mathematics.graphs.model.AbstractNode;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.units.quantities.MolarConcentration;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.bioforscher.units.UnitProvider.MOLE_PER_LITRE;

public class BioNode extends AbstractNode<BioNode, Vector2D> {

    private NodeState state;
    private Map<ChemicalEntity, Quantity<MolarConcentration>> concentrations;
    private boolean isObserved;
    private boolean isSource;

    public BioNode(int identifier) {
        super(identifier);
        this.state = NodeState.AQUEOUS;
        this.concentrations = new HashMap<>();
    }

    public Set<BioNode> getNeighboursInState(NodeState state) {
        return this.getNeighbours().stream()
                .filter(node -> node.getState() == state)
                .collect(Collectors.toSet());
    }

    public void addEntity(ChemicalEntity entity, double concentration) {
        setConcentration(entity, Quantities.getQuantity(concentration, MOLE_PER_LITRE));
    }

    public void addEntity(ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        setConcentration(entity, concentration);
    }

    public void addAllEntities(Quantity<MolarConcentration> concentration, ChemicalEntity... entities) {
        for (ChemicalEntity entity : entities) {
            setConcentration(entity, concentration);
        }
    }

    public void addAllEntities(double concentration, ChemicalEntity... entities) {
        for (ChemicalEntity entity : entities) {
            setConcentration(entity, concentration);
        }
    }

    public Quantity<MolarConcentration> getConcentration(ChemicalEntity entity) {
        if (!this.concentrations.containsKey(entity)) {
            setConcentration(entity, 0.0);
        }
        return this.concentrations.get(entity);
    }

    public Map<ChemicalEntity, Quantity<MolarConcentration>> getConcentrations() {
        return this.concentrations;
    }

    public void setConcentrations(Map<ChemicalEntity, Quantity<MolarConcentration>> concentrations) {
        this.concentrations = concentrations;
    }

    public HashMap<String, ChemicalEntity> getMapOfEntities() {
        HashMap<String, ChemicalEntity> results = new HashMap<>();
        for (ChemicalEntity entity : this.concentrations.keySet()) {
            results.put(entity.getName(), entity);
        }
        return results;
    }

    public boolean isObserved() {
        return this.isObserved;
    }

    public void setObserved(boolean isObserved) {
        this.isObserved = isObserved;
    }

    public boolean isSource() {
        return this.isSource;
    }

    public void setSource(boolean isSource) {
        this.isSource = isSource;
    }

    public void setConcentration(ChemicalEntity entity, Quantity<MolarConcentration> quantity) {
        this.concentrations.put(entity, quantity);
    }

    public void setConcentration(ChemicalEntity entity, double value) {
        setConcentration(entity, Quantities.getQuantity(value, MOLE_PER_LITRE));
    }

    public NodeState getState() {
        return this.state;
    }

    public void setState(NodeState state) {
        this.state = state;
    }

    public double getSteepestConcentrationDifference(ChemicalEntity entity) {
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
