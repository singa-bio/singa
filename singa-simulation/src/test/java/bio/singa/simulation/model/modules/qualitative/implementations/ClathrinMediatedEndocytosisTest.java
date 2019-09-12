package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.features.reactions.FirstOrderRate;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.linelike.MicrotubuleOrganizingCentre;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneTracer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.List;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.simulation.features.SpawnRate.PER_SQUARE_NANOMETRE_PER_SECOND;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.AbstractUnit.ONE;
import static tech.units.indriya.unit.MetricPrefix.*;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class ClathrinMediatedEndocytosisTest {

    @Test
    void testEndocytosis() {

        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(0.5, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(100);
        Environment.setNodeSpacingToDiameter(systemExtend, 1);

        ComparableQuantity<Time> timeStep = Quantities.getQuantity(1, MILLI(SECOND));
        UnitRegistry.setTime(timeStep);

        ChemicalEntity aqp = Protein.create("AQP").build();
        ChemicalEntity other = Protein.create("OTHER").build();

        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(5, MILLI(SECOND)));

        // define graphs
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setPosition(new Vector2D(50.0, 50.0));
        node.setCellRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION);
        ComparableQuantity<MolarConcentration> initialConcentration = Quantities.getQuantity(10, MICRO_MOLE_PER_LITRE);
        node.getConcentrationContainer().initialize(MEMBRANE, aqp, initialConcentration);
        node.getConcentrationContainer().initialize(MEMBRANE, other, initialConcentration);
//        System.out.println("initial membrane concentration: " + MolarConcentration.concentrationToMolecules(UnitRegistry.convert(initialConcentration).getValue().doubleValue()).getValue().doubleValue());
        simulation.setGraph(graph);

        // add vesicle layer
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        simulation.setVesicleLayer(vesicleLayer);

        // setup membrane
        List<Membrane> membranes = MembraneTracer.regionsToMembrane(graph);
        MembraneLayer membraneLayer = new MembraneLayer();
        membraneLayer.addMembranes(membranes);
        simulation.setMembraneLayer(membraneLayer);
        // microtubule organizing centre
        MicrotubuleOrganizingCentre moc = new MicrotubuleOrganizingCentre(membraneLayer, new Circle(new Vector2D(50, 90),
                Environment.convertSystemToSimulationScale(Quantities.getQuantity(50, NANO(METRE)))));
        membraneLayer.setMicrotubuleOrganizingCentre(moc);

        ComparableQuantity<FirstOrderRate> kf_endoAddition = Quantities.getQuantity(0.03, ONE.divide(SECOND)
                .asType(FirstOrderRate.class));

        // clathrin mediated endocytosis
        ClathrinMediatedEndocytosis endocytosis = new ClathrinMediatedEndocytosis();
        endocytosis.limitPitsToOneAtATime();
        endocytosis.setIdentifier("endocytosis: aqp2 vesicle endocytosis");
        endocytosis.setFeature(new AffectedRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION));
        endocytosis.setFeature(new PitFormationRate(Quantities.getQuantity(4, PER_SQUARE_NANOMETRE_PER_SECOND)));
        endocytosis.setFeature(VesicleRadius.DEFAULT_VESICLE_RADIUS);
        endocytosis.setFeature(new CargoAdditionRate(kf_endoAddition));
        endocytosis.setFeature(new EndocytosisCheckpointTime(Quantities.getQuantity(30.0, SECOND)));
        Quantity<MolarConcentration> checkpointConcentration = UnitRegistry.concentration(MolarConcentration.moleculesToConcentration(400));
        endocytosis.setFeature(new EndocytosisCheckpointConcentration(checkpointConcentration));
        endocytosis.setFeature(new Cargoes(EntityRegistry.matchExactly("AQP")));
        endocytosis.setFeature(new MaturationTime(Quantities.getQuantity(50.0, SECOND)));
        simulation.addModule(endocytosis);

        simulation.nextEpoch();
        ClathrinMediatedEndocytosis clathrinMediatedEndocytosis = simulation.getModules().stream()
                .filter(module -> (module instanceof ClathrinMediatedEndocytosis))
                .findAny()
                .map(ClathrinMediatedEndocytosis.class::cast)
                .orElseThrow(IllegalStateException::new);

        ClathrinMediatedEndocytosis.Pit pit = clathrinMediatedEndocytosis.getAspiringPits().get(0);
//        System.out.println("initial concentration: " + MolarConcentration.concentrationToMolecules(pit.getConcentrationDeltaManager().getConcentrationContainer()
//                .get(CellRegions.VESICLE_REGION.getMembraneSubsection(), EntityRegistry.matchExactly("AQP"))));

        while (simulation.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(31, SECOND))) {
            simulation.nextEpoch();
//            System.out.println("concentration: " + MolarConcentration.concentrationToMolecules(pit.getConcentrationDeltaManager().getConcentrationContainer()
//                    .get(CellRegions.VESICLE_REGION.getMembraneSubsection(), EntityRegistry.matchExactly("AQP"))));
        }

        // check if threshold has been reached
        assertTrue(UnitRegistry.concentration(pit.getConcentrationDeltaManager().getConcentrationContainer()
                .get(MEMBRANE, EntityRegistry.matchExactly("AQP"))).getValue().doubleValue() >= checkpointConcentration.getValue().doubleValue());

        while (simulation.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(85, SECOND))) {
            simulation.nextEpoch();
        }

        List<Vesicle> vesicles = vesicleLayer.getVesicles();
        assertEquals(1, vesicles.size());

        Vesicle vesicle = vesicles.get(0);
        assertTrue(400 < MolarConcentration.concentrationToMolecules(vesicle.getConcentrationContainer()
                    .get(CellRegions.VESICLE_REGION.getMembraneSubsection(), EntityRegistry.matchExactly("AQP"))).getValue().intValue());
    }
}