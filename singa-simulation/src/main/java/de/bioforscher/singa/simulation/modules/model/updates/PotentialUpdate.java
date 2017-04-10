package de.bioforscher.singa.simulation.modules.model.updates;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.units.quantities.MolarConcentration;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class PotentialUpdate {

    private final CellSection cellSection;
    private final ChemicalEntity entity;
    private final Quantity<MolarConcentration> quantity;

    public PotentialUpdate(CellSection cellSection, ChemicalEntity entity, Quantity<MolarConcentration> quantity) {
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

}
