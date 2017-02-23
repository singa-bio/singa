package de.bioforscher.simulation.model.compartments;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.graphs.AutomatonGraph;
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
     * Fills a {@link Compartment} with the given concentration of a {@link ChemicalEntity}.
     *
     * @param compartment The {@link Compartment} to fill
     * @param chemicalEntity The {@link ChemicalEntity}
     * @param concentration The concentration.
     */
    public static void fillCompartmentWithEntity(Compartment compartment, ChemicalEntity<?> chemicalEntity, Quantity<MolarConcentration> concentration) {
        for (BioNode node: compartment.getContent()) {
            node.setConcentration(chemicalEntity, concentration);
        }
    }


}
