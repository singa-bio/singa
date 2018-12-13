package bio.singa.simulation.trajectories;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationPool;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Unit;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class ConcentrationData {

    private Map<CellSubsection, Map<ChemicalEntity, Double>> concentrations;

    private ConcentrationData() {
        concentrations = new HashMap<>();
    }

    public Map<CellSubsection, Map<ChemicalEntity, Double>> getConcentrations() {
        return concentrations;
    }

    public static ConcentrationData of(Updatable updatable, Unit<MolarConcentration> concentrationUnit) {
        ConcentrationData data = new ConcentrationData();
        for (Map.Entry<CellSubsection, ConcentrationPool> subsectionEntry : updatable.getConcentrationContainer().getConcentrations().entrySet()) {
            // prepare inner map
            Map<ChemicalEntity, Double> values = new HashMap<>();
            for (Map.Entry<ChemicalEntity, Double> quantityEntry : subsectionEntry.getValue().getConcentrations().entrySet()) {
                values.put(quantityEntry.getKey(), UnitRegistry.concentration(quantityEntry.getValue()).to(concentrationUnit).getValue().doubleValue());
            }
            // add to outer map
            data.concentrations.put(subsectionEntry.getKey(), values);
        }
        return data;
    }

}
