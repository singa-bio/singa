package de.bioforscher.singa.simulation.model.concentrations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class Delta {

    private final CellSection cellSection;
    private final ChemicalEntity entity;
    private final Quantity<MolarConcentration> quantity;

    public Delta(CellSection cellSection, ChemicalEntity entity, Quantity<MolarConcentration> quantity) {
        this.entity = entity;
        this.cellSection = cellSection;
        this.quantity = quantity;
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

    public Delta merge(Delta anotherDelta) {
        return new Delta(this.cellSection, this.entity, this.quantity.add(anotherDelta.getQuantity()));
    }

    @Override
    public String toString() {
        return "Delta{" +
                "cellSection=" + cellSection +
                ", entity=" + entity +
                ", quantity=" + quantity +
                '}';
    }
}
