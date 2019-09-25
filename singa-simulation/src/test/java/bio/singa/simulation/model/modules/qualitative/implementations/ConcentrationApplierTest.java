package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.EntityRegistry;
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
import bio.singa.simulation.features.ContainmentRegion;
import bio.singa.simulation.features.WhiteListVesicleStates;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.volumelike.VolumeLayer;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.sections.concentration.ConcentrationBuilder;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import bio.singa.simulation.model.sections.concentration.InitialConcentration;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Collections;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.IN_PERINUCLEAR_STORAGE;
import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.UNATTACHED;
import static bio.singa.simulation.model.sections.CellRegions.CYTOPLASM_REGION;
import static bio.singa.simulation.model.sections.CellRegions.VESICLE_REGION;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.unit.MetricPrefix.*;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class ConcentrationApplierTest {

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

        InitialConcentration aqp2Initial = ConcentrationBuilder.regular()
                .entity(EntityRegistry.matchExactly("AQP2"))
                .subsection(VESICLE_REGION.getMembraneSubsection())
                .concentrationValue(10)
                .microMolar()
                .build();

        InitialConcentration aqp2pInitial = ConcentrationBuilder.regular()
                .entity(EntityRegistry.matchExactly("AQP2", "P"))
                .subsection(VESICLE_REGION.getMembraneSubsection())
                .concentrationValue(20)
                .microMolar()
                .build();

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(aqp2Initial);
        ci.addInitialConcentration(aqp2pInitial);
        simulation.setConcentrationInitializer(ci);

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setPosition(new Vector2D(50.0, 50.0));
        node.setCellRegion(CYTOPLASM_REGION);
        simulation.setGraph(graph);

        // setup vesicle
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        Vesicle vesicle = new Vesicle(new Vector2D(49.0, 49.0), Quantities.getQuantity(100.0, NANO(METRE)));
        vesicle.setState(IN_PERINUCLEAR_STORAGE);
        vesicleLayer.addVesicle(vesicle);
        simulation.setVesicleLayer(vesicleLayer);

        VolumeLayer volumeLayer = new VolumeLayer();
        VolumeLikeAgent releaseArea = new VolumeLikeAgent(rectangle, VESICLE_REGION);
        volumeLayer.addAgent(releaseArea);
        simulation.setVolumeLayer(volumeLayer);

        // setup state change based on concentration
        ConcentrationApplier stateChange = new ConcentrationApplier();
        stateChange.setIdentifier("vesicle state change and concentration application");
        stateChange.setFeature(new WhiteListVesicleStates(Collections.singletonList(IN_PERINUCLEAR_STORAGE)));
        stateChange.setFeature(new ContainmentRegion(VESICLE_REGION));
        stateChange.setFeature(new AppliedVesicleState(UNATTACHED));

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

        simulation.nextEpoch();

        Quantity<MolarConcentration> caqp2p = UnitRegistry.concentration(vesicle.getConcentrationContainer().get(MEMBRANE, aqp2p)).to(MICRO_MOLE_PER_LITRE);
        Quantity<MolarConcentration> caqp2 = UnitRegistry.concentration(vesicle.getConcentrationContainer().get(MEMBRANE, aqp2)).to(MICRO_MOLE_PER_LITRE);

        assertEquals(10.0, caqp2.getValue().doubleValue());
        assertEquals(20.0, caqp2p.getValue().doubleValue());
        assertEquals(UNATTACHED, vesicle.getState());

    }

}