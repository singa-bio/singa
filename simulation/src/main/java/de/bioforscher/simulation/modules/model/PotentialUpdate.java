package de.bioforscher.simulation.modules.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.units.quantities.MolarConcentration;

import javax.measure.Quantity;

/**
 * Created by Christoph on 06.07.2016.
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
