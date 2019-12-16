package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.features.reactions.FirstOrderRate;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.chemistry.reactions.conditions.CandidateConditionBuilder;
import bio.singa.chemistry.reactions.reactors.ReactionChainBuilder;
import bio.singa.features.formatter.TimeFormatter;
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
import bio.singa.simulation.model.concentrations.ConcentrationBuilder;
import bio.singa.simulation.model.concentrations.InitialConcentration;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.ReactionBuilder;
import bio.singa.simulation.model.modules.concentration.imlementations.transport.EndocytoticPitAbsorption;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationPool;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.simulation.features.SpawnRate.PER_SQUARE_NANOMETRE_PER_SECOND;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
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

        ChemicalEntity primaryCargo = Protein.create("PRIMARY_CARGO")
                .membraneBound()
                .build();

        ChemicalEntity otherCargo = Protein.create("OTHER_CARGO")
                .membraneBound()
                .build();

        ChemicalEntity inhibitor = Protein.create("INHIBITOR")
                .membraneBound()
                .build();
        ChemicalEntity accelerator = Protein.create("ACCELERATOR")
                .membraneBound()
                .build();

        ChemicalEntity initializedCargo = Protein.create("INITIALIZED_CARGO")
                .membraneBound()
                .build();

        ChemicalEntity substrate = Protein.create("SUBSTRATE").build();
        ChemicalEntity product = Protein.create("PRODUCT").build();
        ChemicalEntity enzyme = Protein.create("ENZYME")
                .membraneBound()
                .build();

        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(5, MILLI(SECOND)));

        // define graphs
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setPosition(new Vector2D(50.0, 50.0));
        node.setCellRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION);
        simulation.setGraph(graph);

        ConcentrationBuilder.create(simulation)
                .entity(primaryCargo)
                .topology(MEMBRANE)
                .concentrationValue(10)
                .microMolar()
                .onlyNodes()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(otherCargo)
                .topology(MEMBRANE)
                .concentrationValue(1)
                .microMolar()
                .onlyNodes()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(accelerator)
                .topology(MEMBRANE)
                .concentrationValue(1)
                .microMolar()
                .onlyNodes()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(inhibitor)
                .topology(MEMBRANE)
                .concentrationValue(0.05)
                .microMolar()
                .onlyNodes()
                .build();

        InitialConcentration initializedConcentration = ConcentrationBuilder.create()
                .entity(initializedCargo)
                .topology(MEMBRANE)
                .concentrationValue(10)
                .microMolar()
                .build();

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

        Quantity<MolarConcentration> checkpointConcentration = UnitRegistry.concentration(MolarConcentration.moleculesToConcentration(400));

        RateConstant k_on = RateConstant.create(0.1)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        RateConstant k_off = RateConstant.create(1)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        RateConstant k_cat = RateConstant.create(1)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();

        ReactionBuilder.FinalStep binding = ReactionBuilder.ruleBased(simulation)
                .rule(ReactionChainBuilder.bind(substrate)
                        .to(enzyme)
                        .considerInversion()
                        .identifier("binding")
                        .build())
                .reversible()
                .forwardReactionRate(k_on)
                .backwardReactionRate(k_off);

        ReactionBuilder.FinalStep catalysis = ReactionBuilder.ruleBased(simulation)
                .rule(ReactionChainBuilder.add(product)
                        .to(enzyme)
                        .condition(CandidateConditionBuilder.hasOneOfEntity(substrate))
                        .and()
                        .remove(substrate)
                        .from(enzyme)
                        .and()
                        .release(product)
                        .from(enzyme)
                        .identifier("catalysis")
                        .build())
                .irreversible()
                .rate(k_cat);

        ReactionBuilder.generateNetwork();

        // clathrin mediated endocytosis
        ClathrinMediatedEndocytosis endocytosis = new ClathrinMediatedEndocytosis(vesicleLayer);
        endocytosis.limitPitsToOneAtATime();
        endocytosis.setIdentifier("endocytosis");
        endocytosis.setFeature(new AffectedRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION));
        endocytosis.setFeature(new PitFormationRate(Quantities.getQuantity(4, PER_SQUARE_NANOMETRE_PER_SECOND)));
        endocytosis.setFeature(VesicleRadius.DEFAULT_VESICLE_RADIUS);
        endocytosis.setFeature(new EndocytosisCheckpointTime(Quantities.getQuantity(30.0, SECOND)));
        endocytosis.setFeature(new EndocytosisCheckpointConcentration(checkpointConcentration));
        endocytosis.setFeature(new Cargo(primaryCargo));
        Cargoes cargoes = new Cargoes(primaryCargo, otherCargo, EntityRegistry.matchExactly("ENZYME"));
        endocytosis.setFeature(cargoes);
        endocytosis.setFeature(new MaturationTime(Quantities.getQuantity(50.0, SECOND)));
        endocytosis.setFeature(new InitialConcentrations(Collections.singletonList(initializedConcentration)));
        simulation.addModule(endocytosis);

        // pit addition
        EndocytoticPitAbsorption.inSimulation(simulation)
                .forAllEntities(cargoes.getContent())
                .acceleratingEntity(accelerator)
                .inhibitingEntity(inhibitor)
                .rate(kf_endoAddition)
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(EntityRegistry.matchExactly("SUBSTRATE"))
                .topology(INNER)
                .concentrationValue(10)
                .microMolar()
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(EntityRegistry.matchExactly("ENZYME"))
                .topology(MEMBRANE)
                .concentrationValue(1)
                .microMolar()
                .build();

        binding.build();
        catalysis.build();

        simulation.nextEpoch();

        EndocytoticPit pit = vesicleLayer.getAspiringPits().get(0);
        System.out.println("initialized concentrations");
        printConcentrations(pit);
        System.out.println();
        printConcentrations(node);

        while (simulation.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(31, SECOND))) {
            simulation.nextEpoch();
//            System.out.println(TimeFormatter.formatTime(simulation.getElapsedTime()));
//            printConcentrations(pit);
//            System.out.println();
//            printConcentrations(node);
        }

        // check if threshold has been reached
        System.out.println(MolarConcentration.concentrationToMolecules(pit.getConcentrationContainer().get(MEMBRANE, primaryCargo)));
        assertTrue(UnitRegistry.concentration(pit.getConcentrationContainer()
                .get(MEMBRANE, primaryCargo)).getValue().doubleValue() >= checkpointConcentration.getValue().doubleValue());

        while (simulation.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(85, SECOND))) {
            simulation.nextEpoch();
        }

        List<Vesicle> vesicles = vesicleLayer.getVesicles();
        assertEquals(1, vesicles.size());

        Vesicle vesicle = vesicles.get(0);
        System.out.println(TimeFormatter.formatTime(simulation.getElapsedTime()));
        printConcentrations(vesicle);
        System.out.println();
        printConcentrations(node);

        assertTrue(400 < MolarConcentration.concentrationToMolecules(vesicle.getConcentrationContainer()
                .get(CellRegions.VESICLE_REGION.getMembraneSubsection(), initializedCargo)).getValue().intValue());

        assertEquals(10, UnitRegistry.concentration(vesicle.getConcentrationContainer()
                .get(MEMBRANE, initializedCargo)).to(MICRO_MOLE_PER_LITRE).getValue().doubleValue());
    }

    private void printConcentrations(Updatable updatable) {
        for (CellSubsection referencedSubsection : updatable.getConcentrationContainer().getReferencedSubsections()) {
            Map.Entry<CellTopology, ConcentrationPool> pool = updatable.getConcentrationContainer().getPool(referencedSubsection);
            Map<ChemicalEntity, Double> concentrations = pool.getValue().getConcentrations();
            System.out.println(pool.getKey());
            for (Map.Entry<ChemicalEntity, Double> entry : concentrations.entrySet()) {
                System.out.println("  " + entry.getKey() + " : " + UnitRegistry.humanReadable(entry.getValue()));
            }
        }
        System.out.println();
    }

}