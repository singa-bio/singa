package bio.singa.simulation.trajectories.nested;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.qualitative.implementations.EndocytoticPit;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
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
public class TrajectoryDataPoint {

    private Map<CellSubsection, SubsectionDataPoint> subsectionData;
    private String state;

    public TrajectoryDataPoint() {
        subsectionData = new HashMap<>();
    }

    public static TrajectoryDataPoint of(Updatable updatable, Unit<MolarConcentration> concentrationUnit) {
        TrajectoryDataPoint data = new TrajectoryDataPoint();
        for (CellSubsection subsection : updatable.getConcentrationContainer().getReferencedSubsections()) {
            Map.Entry<CellTopology, ConcentrationPool> poolEntry = updatable.getConcentrationContainer().getPool(subsection);
            // prepare inner map
            Map<ChemicalEntity, Double> concentrations = new HashMap<>();
            for (Map.Entry<ChemicalEntity, Double> quantityEntry : poolEntry.getValue().getConcentrations().entrySet()) {
                concentrations.put(quantityEntry.getKey(), UnitRegistry.concentration(quantityEntry.getValue()).to(concentrationUnit).getValue().doubleValue());
            }
            List<Vector2D> positions = new ArrayList<>();
            if (updatable instanceof AutomatonNode) {
                AutomatonNode node = (AutomatonNode) updatable;
                if (subsection.isMembrane()) {
                    positions.addAll(node.getMembraneVectors());
                } else {
                    positions.addAll(node.getSubsectionRepresentations().get(subsection).getVertices());
                }
            } else if (updatable instanceof Vesicle) {
                Vesicle vesicle = (Vesicle) updatable;
                // current subsection is no membrane - add centroid of subsection polygon
                if (subsection.isMembrane()) {
                    positions.add(vesicle.getPosition().add(new Vector2D(0, 1).multiply(Environment.convertSystemToSimulationScale(vesicle.getRadius()))));
                } else {
                    positions.add(vesicle.getPosition());
                }
            } else if (updatable instanceof EndocytoticPit) {
                EndocytoticPit pit = (EndocytoticPit) updatable;
                positions.add(pit.getPosition());
            }
            // add to outer map
            data.subsectionData.put(subsection, new SubsectionDataPoint(concentrations, positions));
            if (updatable instanceof Vesicle) {
                data.setState(((Vesicle) updatable).getState());
            }
        }
        return data;
    }

    public void put(CellSubsection cellSection, SubsectionDataPoint dataPoint) {
        subsectionData.put(cellSection, dataPoint);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Map<CellSubsection, SubsectionDataPoint> getSubsectionData() {
        return subsectionData;
    }

    public static class SubsectionDataPoint {

        private Map<ChemicalEntity, Double> concentrations;
        private List<Vector2D> positions;

        public SubsectionDataPoint(Map<ChemicalEntity, Double> concentrations, List<Vector2D> positions) {
            this.concentrations = concentrations;
            this.positions = positions;
        }

        public SubsectionDataPoint() {
            concentrations = new HashMap<>();
            positions = new ArrayList<>();
        }

        public void addConcentration(ChemicalEntity entity, double concentration) {
            concentrations.put(entity, concentration);
        }

        public void addPosition(Vector2D postion) {
            positions.add(postion);
        }

        public Map<ChemicalEntity, Double> getConcentrations() {
            return concentrations;
        }

        public List<Vector2D> getPositions() {
            return positions;
        }

    }


}
