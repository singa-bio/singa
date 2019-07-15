package bio.singa.simulation.trajectories.nested;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationPool;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Unit;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class TrajactoryDataPoint {

    private Map<CellSubsection, Map<ChemicalEntity, Double>> concentrations;
    private Vector2D position;

    private TrajactoryDataPoint() {
        concentrations = new HashMap<>();
    }

    public Map<CellSubsection, Map<ChemicalEntity, Double>> getConcentrations() {
        return concentrations;
    }

    public Vector2D getPosition() {
        return position;
    }

    public static TrajactoryDataPoint of(Updatable updatable, Unit<MolarConcentration> concentrationUnit) {
        TrajactoryDataPoint data = new TrajactoryDataPoint();
        for (Map.Entry<CellSubsection, ConcentrationPool> subsectionEntry : updatable.getConcentrationContainer().getConcentrations().entrySet()) {
            // prepare inner map
            Map<ChemicalEntity, Double> values = new HashMap<>();
            for (Map.Entry<ChemicalEntity, Double> quantityEntry : subsectionEntry.getValue().getConcentrations().entrySet()) {
                values.put(quantityEntry.getKey(), UnitRegistry.concentration(quantityEntry.getValue()).to(concentrationUnit).getValue().doubleValue());
            }
            // add to outer map
            data.concentrations.put(subsectionEntry.getKey(), values);
        }
        data.position = updatable.getPosition();
        return data;
    }

}
