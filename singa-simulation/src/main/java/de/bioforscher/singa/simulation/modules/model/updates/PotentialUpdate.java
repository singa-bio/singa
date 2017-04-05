package de.bioforscher.singa.simulation.modules.model.updates;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.units.quantities.MolarConcentration;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class PotentialUpdate {

    private final ChemicalEntity entity;
    private final Quantity<MolarConcentration> quantity;

    public PotentialUpdate(ChemicalEntity entity, Quantity<MolarConcentration> quantity) {
        this.entity = entity;
        this.quantity = quantity;
    }

    public ChemicalEntity getEntity() {
        return this.entity;
    }

    public Quantity<MolarConcentration> getQuantity() {
        return this.quantity;
    }

}
