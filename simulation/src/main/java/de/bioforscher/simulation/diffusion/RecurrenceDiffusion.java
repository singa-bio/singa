package de.bioforscher.simulation.diffusion;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.Diffusivity;
import de.bioforscher.units.quantities.MolarConcentration;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.bioforscher.units.UnitDictionary.MOLE_PER_LITRE;


/**
 * The class is used to simulate diffusion as described by Fick.
 *
 * @author Christoph Leberecht
 */
public class RecurrenceDiffusion implements Diffusion {

    private static final Logger log = Logger.getLogger(RecurrenceDiffusion.class.getName());

    private Map<ChemicalEntity, Quantity<Diffusivity>> diffusionCoefficients;

    public RecurrenceDiffusion(Map<String, ChemicalEntity> availableEntities) {
        this.diffusionCoefficients = new HashMap<>();
        initializeDiffusionCoefficients(availableEntities);
    }

    public void initializeDiffusionCoefficients(Map<String, ChemicalEntity> availableSpecies) {
        for (ChemicalEntity species : availableSpecies.values()) {
            Quantity<Diffusivity> diffusionCoefficient = determineDiffusionCoefficient(species);
            this.diffusionCoefficients.put(species, diffusionCoefficient);
        }
        log.log(Level.INFO, "initialized diffusion coefficients");
    }

    @Override
    public Map<ChemicalEntity, Quantity<MolarConcentration>> calculateConcentration(BioNode node) {
        Map<ChemicalEntity, Quantity<MolarConcentration>> currentConcentrations = new HashMap<>();
        // for each compound in the node
        for (ChemicalEntity species : node.getConcentrations().keySet()) {
            // calculate coefficient
            Quantity<Diffusivity> coefficient;
            if (this.diffusionCoefficients.containsKey(species)) {
                coefficient = this.diffusionCoefficients.get(species);
            } else {
                coefficient = determineDiffusionCoefficient(species);
            }

            // sum up concentrations
            double neighbourConcentration = 0.0;
            for (BioNode neighbor : node.getNeighbours()) {
                neighbourConcentration += neighbor.getConcentration(species).getValue().doubleValue();
            }

            // number of neighbors
            int neighbours = node.getNeighbours().size();

            // current concentration of this node
            double currentConcentration = node.getConcentration(species).getValue().doubleValue();

            // calculate next concentration
            double nextConcentration = coefficient.getValue().doubleValue() * neighbourConcentration + (1 -
                    neighbours * coefficient.getValue().doubleValue()) * currentConcentration;

            // update concentrations
            currentConcentrations.put(species, Quantities.getQuantity(nextConcentration, MOLE_PER_LITRE));
        }

        return currentConcentrations;

    }

    /**
     * Determines the diffusivity of the species and scales it to the dimensions
     * of the system.
     *
     * @param species The species.
     * @return The diffusivity of the species.
     */
    private Quantity<Diffusivity> determineDiffusionCoefficient(ChemicalEntity species) {
        Quantity<Diffusivity> diffusivityApproximation = DiffusionUtilities.estimateDiffusivity(species);
        log.log(Level.INFO, "approximated diffusion coefficients for " + species + " to " + diffusivityApproximation);
        return scaleDiffusivity(diffusivityApproximation);
    }

    /**
     * Scales the given diffusivity to the dimensions and features of the
     * system.
     *
     * @param diffusivity The diffusivity to be scaled.
     * @return The scaled diffusivity.
     */
    private Quantity<Diffusivity> scaleDiffusivity(Quantity<Diffusivity> diffusivity) {
        Quantity<Diffusivity> correlatedDiffusivity = UnitScaler.rescaleDiffusivity(diffusivity,
                EnvironmentalVariables.getInstance().getTimeStep(),
                EnvironmentalVariables.getInstance().getNodeDistance());
        // artificially slow if this is a cellular environment
        if (EnvironmentalVariables.getInstance().isCellularEnvironment()) {
            correlatedDiffusivity = correlatedDiffusivity.multiply(DiffusionUtilities.STDF_CELL_WATER.getValue());
        }
        return correlatedDiffusivity;
    }

}
