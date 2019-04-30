package bio.singa.simulation.trajectories.nested;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationPool;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Unit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class TrajactoryDataPoint {

    private Map<CellSubsection, SubsectionDatapoint> subsectionData;

    private TrajactoryDataPoint() {
        subsectionData = new HashMap<>();
    }

    public static TrajactoryDataPoint of(Updatable updatable, Unit<MolarConcentration> concentrationUnit) {
        TrajactoryDataPoint data = new TrajactoryDataPoint();
        for (Map.Entry<CellSubsection, ConcentrationPool> subsectionEntry : updatable.getConcentrationContainer().getConcentrations().entrySet()) {
            // prepare inner map
            Map<ChemicalEntity, Double> concentrations = new HashMap<>();
            for (Map.Entry<ChemicalEntity, Double> quantityEntry : subsectionEntry.getValue().getConcentrations().entrySet()) {
                concentrations.put(quantityEntry.getKey(), UnitRegistry.concentration(quantityEntry.getValue()).to(concentrationUnit).getValue().doubleValue());
            }

            List<Vector2D> positions = new ArrayList<>();
            CellSubsection currentSubsection = subsectionEntry.getKey();
            if (updatable instanceof AutomatonNode) {
                AutomatonNode node = (AutomatonNode) updatable;
                if (currentSubsection.isMembrane()) {
                    // current subsection is membrane subsection - add points of membrane
                    for (MembraneSegment membraneSegment : node.getMembraneSegments()) {
                        Vector2D startingPoint = membraneSegment.getStartingPoint();
                        if (!positions.contains(startingPoint)) {
                            positions.add(startingPoint);
                        }
                        Vector2D endingPoint = membraneSegment.getEndingPoint();
                        if (!positions.contains(endingPoint)) {
                            positions.add(endingPoint);
                        }
                    }
                } else {
                    positions.add(node.getSubsectionRepresentations().get(currentSubsection).getCentroid());
                }
            } else if (updatable instanceof Vesicle) {
                Vesicle vesicle = (Vesicle) updatable;
                // current subsection is no membrane - add centroid of subsection polygon
                if (currentSubsection.isMembrane()) {
                    positions.add(vesicle.getPosition().add(new Vector2D(0,1).multiply(Environment.convertSystemToSimulationScale(vesicle.getRadius()))));
                } else {
                    positions.add(vesicle.getPosition());
                }
            }

            // add to outer map
            data.subsectionData.put(currentSubsection, new SubsectionDatapoint(concentrations, positions));
        }
        return data;
    }

    public Map<CellSubsection, SubsectionDatapoint> getSubsectionData() {
        return subsectionData;
    }

    public static class SubsectionDatapoint {

        final Map<ChemicalEntity, Double> concentration;
        final List<Vector2D> positions;

        public SubsectionDatapoint(Map<ChemicalEntity, Double> concentration, List<Vector2D> positions) {
            this.concentration = concentration;
            this.positions = positions;
        }

        public Map<ChemicalEntity, Double> getConcentration() {
            return concentration;
        }

        public List<Vector2D> getPositions() {
            return positions;
        }

    }


}
