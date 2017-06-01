package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.mathematics.graphs.model.AbstractNode;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.units.quantities.MolarConcentration;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.Map;
import java.util.Set;

import static de.bioforscher.singa.units.UnitProvider.MOLE_PER_LITRE;

public class BioNode extends AbstractNode<BioNode, Vector2D> {

    private NodeState state;
    private CellSection cellSection;
    private ConcentrationContainer concentrations;
    private boolean isObserved;


    public BioNode(int identifier) {
        super(identifier);
        this.state = NodeState.AQUEOUS;
        this.cellSection = new EnclosedCompartment("default", "Default Compartment");
        this.concentrations = new MultiConcentrationContainer(this.cellSection);
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

    public Map<ChemicalEntity, Quantity<MolarConcentration>> getAllConcentrations() {
        return this.concentrations.getAllConcentrations();
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

    public Set<CellSection> getAllReferencedSections() {
        return this.concentrations.getAllReferencedSections();
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

    public CellSection getCellSection() {
        return this.cellSection;
    }

    public void setCellSection(CellSection cellSection) {
        this.cellSection = cellSection;
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
