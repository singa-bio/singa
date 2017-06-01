package de.bioforscher.singa.simulation.modules.model.updates;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.units.quantities.MolarConcentration;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class PotentialUpdate {

    private final BioNode node;
    private final CellSection cellSection;
    private final ChemicalEntity entity;
    private final Quantity<MolarConcentration> quantity;

    public PotentialUpdate(BioNode node, CellSection cellSection, ChemicalEntity entity, Quantity<MolarConcentration> quantity) {
        this.node = node;
        this.entity = entity;
        this.cellSection = cellSection;
        this.quantity = quantity;
    }

    public BioNode getNode() {
        return this.node;
    }

    public CellSection getCellSection() {
        return this.cellSection;
    }

    public ChemicalEntity getEntity() {
        return this.entity;
    }

    public Quantity<MolarConcentration> getQuantity() {
        return this.quantity;
    }

    public void apply() {
        this.node.setAvailableConcentration(this.entity, this.cellSection, this.quantity);
    }

}
