package de.bioforscher.simulation.deprecated;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.units.quantities.MolarConcentration;

import javax.measure.Quantity;
import java.util.Map;

/**
 * Diffusion is the net movement of molecules or atoms from a region of high
 * concentration to a region of low concentration. This interface can be used to
 * implement a method for the calculation of concentrations of species in the
 * given node.
 *
 * @author Christoph Leberecht
 * @deprecated
 */
public interface Diffusion {

    /**
     * Calculates the new value of concentrations of species in the given node.
     *
     * @param node The node of interest.
     * @return A mapping of compounds and their next concentrations.
     */
    public Map<ChemicalEntity, Quantity<MolarConcentration>> calculateConcentration(BioNode node);

}
