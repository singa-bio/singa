package de.bioforscher.singa.simulation.features.chemicalpotential;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.NaturalConstants;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class ChemicalPotentialGradient {

    public static Quantity calcualteChemcialPotentialConstantFor(ChemicalEntity entity) {

        Quantity constant = NaturalConstants.BOLTZMANN_CONSTANT.multiply(-1).multiply(EnvironmentalParameters.getInstance().getSystemTemperature()).multiply(entity.getFeature())

    }

}
