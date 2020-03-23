package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.complex.ComplexEntity;
import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.features.reactions.RateConstant;
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
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;

import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.IN_PERINUCLEAR_STORAGE;
import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.UNATTACHED;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.unit.MetricPrefix.*;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class ConcentrationStateChangeTest {

    @Test
    void testModuleInContext() {

        // setup simulation
        Simulation simulation = new Simulation();
        final double simulationExtend = 100;
        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);
        simulation.setSimulationRegion(rectangle);
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(2, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        UnitRegistry.setTime(Quantities.getQuantity(1, MILLI(SECOND)));

        // setup trigger entity
        Protein aqp2 = Protein.create("AQP2").build();
        ChemicalEntity p = SmallMolecule.create("P").build();
        ComplexEntity aqp2p = ComplexEntity.from(aqp2, p);

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setPosition(new Vector2D(50.0, 50.0));
        node.setCellRegion(CellRegions.CYTOPLASM_REGION);
        simulation.setGraph(graph);

        // setup vesicle
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        Vesicle vesicle = new Vesicle(new Vector2D(49.0, 49.0), Quantities.getQuantity(100.0, NANO(METRE)));
        vesicle.setState(IN_PERINUCLEAR_STORAGE);
        vesicle.getConcentrationContainer().set(MEMBRANE, aqp2p, MolarConcentration.moleculesToConcentration(200));
        vesicle.getConcentrationContainer().set(MEMBRANE, aqp2, MolarConcentration.moleculesToConcentration(400));
        vesicleLayer.addVesicle(vesicle);
        simulation.setVesicleLayer(vesicleLayer);

        // setup state change based on concentration
        ConcentrationStateChange stateChange = new ConcentrationStateChange();
        stateChange.setIdentifier("vesicle state change");
        stateChange.setFeature(new RequiredVesicleState(IN_PERINUCLEAR_STORAGE));
        stateChange.setFeature(new AppliedVesicleState(UNATTACHED));
        stateChange.setFeature(Cargoes.of(aqp2p, aqp2).build());
        stateChange.setFeature(new Ratio(3.0/4.0));

        // setup reaction to introduce entity change
        ReactionBuilder.staticReactants(simulation)
                .addSubstrate(aqp2, MEMBRANE)
                .addProduct(aqp2p, MEMBRANE)
                .irreversible()
                .rate(RateConstant.create(1.0)
                        .forward().firstOrder()
                        .timeUnit(MILLI(SECOND))
                        .build())
                .identifier("aqp2 phosphorylation")
                .build();

        simulation.addModule(stateChange);

        while (simulation.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(200, MICRO(SECOND)))) {
            simulation.nextEpoch();
        }

        double caqp2p = vesicle.getConcentrationContainer().get(MEMBRANE, aqp2p);
        double caqp2 = vesicle.getConcentrationContainer().get(MEMBRANE, aqp2);

        assertTrue(3.0/4.0 < caqp2p/caqp2);
        assertEquals(UNATTACHED, vesicle.getState());

    }
}