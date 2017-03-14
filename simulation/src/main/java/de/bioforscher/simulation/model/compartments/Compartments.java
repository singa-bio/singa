package de.bioforscher.simulation.model.compartments;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.graphs.BioNode;
import de.bioforscher.units.quantities.MolarConcentration;

import javax.measure.Quantity;

/**
 * @author cl
 */
public final class Compartments {

    private Compartments() {

    }

    /**
     * Fills a {@link EnclosedCompartment} with the given concentration of a {@link ChemicalEntity}.
     *
     * @param enclosedCompartment The {@link EnclosedCompartment} to fill
     * @param chemicalEntity The {@link ChemicalEntity}
     * @param concentration The concentration.
     */
    public static void fillCompartmentWithEntity(EnclosedCompartment enclosedCompartment, ChemicalEntity<?> chemicalEntity, Quantity<MolarConcentration> concentration) {
        for (BioNode node: enclosedCompartment.getContent()) {
            node.setConcentration(chemicalEntity, concentration);
        }
    }

}
