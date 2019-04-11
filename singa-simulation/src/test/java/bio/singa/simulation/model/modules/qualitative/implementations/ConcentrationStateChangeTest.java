package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.AppliedVesicleState;
import bio.singa.simulation.features.Cargoes;
import bio.singa.simulation.features.Ratio;
import bio.singa.simulation.features.RequiredVesicleState;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneTracer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.DynamicChemicalEntity;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.EntityReducer;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tec.units.indriya.ComparableQuantity;
import tec.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;
import java.util.List;

import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.IN_STORAGE;
import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.UNATTACHED;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.fail;
import static tec.units.indriya.unit.MetricPrefix.MICRO;
import static tec.units.indriya.unit.MetricPrefix.NANO;
import static tec.units.indriya.unit.Units.METRE;
import static tec.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class ConcentrationStateChangeTest {

    @Test
    void testModule() {

        // setup simulation
        Simulation simulation = new Simulation();
        final double simulationExtend = 100;
        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);
        simulation.setSimulationRegion(rectangle);
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(2, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        UnitRegistry.setTime(Quantities.getQuantity(1, MICRO(SECOND)));

        // setup snares for fusion
        Protein aqp2 = Protein.create("AQP2").build();
        ChemicalEntity p = SmallMolecule.create("P").build();

        ComplexEntity aqp2p = ComplexEntity.from(aqp2, p);

        DynamicChemicalEntity allAqp2 = DynamicChemicalEntity.create("AQP2*")
                .addPossibleTopology(MEMBRANE)
                .addCompositionCondition(EntityReducer.hasPart(aqp2))
                .build();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setPosition(new Vector2D(50.0, 50.0));
        node.setCellRegion(CellRegion.CYTOSOL_A);
        simulation.setGraph(graph);

        // setup membrane
        List<Membrane> membranes = MembraneTracer.regionsToMembrane(graph);
        MembraneLayer membraneLayer = new MembraneLayer();
        membraneLayer.addMembranes(membranes);
        simulation.setMembraneLayer(membraneLayer);

        // setup vesicle
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        Vesicle vesicle = new Vesicle(new Vector2D(49.0, 49.0), Quantities.getQuantity(100.0, NANO(METRE)));
        vesicle.setState(IN_STORAGE);
        vesicle.getConcentrationContainer().set(MEMBRANE, aqp2, MolarConcentration.moleculesToConcentration(500));
        vesicle.getConcentrationContainer().set(MEMBRANE, aqp2p, MolarConcentration.moleculesToConcentration(200));
        vesicleLayer.addVesicle(vesicle);
        simulation.setVesicleLayer(vesicleLayer);

        // setup fusion module
        ConcentrationStateChange stateChange = new ConcentrationStateChange();
        stateChange.setFeature(new RequiredVesicleState(IN_STORAGE));
        stateChange.setFeature(new AppliedVesicleState(UNATTACHED));
        stateChange.setFeature(new Cargoes(allAqp2, aqp2p));
        stateChange.setFeature(new Ratio(3.0/4.0));

        simulation.addModule(stateChange);

        while (simulation.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(10.0, SECOND))) {
            simulation.nextEpoch();
        }

        fail();

    }
}