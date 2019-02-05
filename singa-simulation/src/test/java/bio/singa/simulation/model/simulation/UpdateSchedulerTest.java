package bio.singa.simulation.model.simulation;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneTracer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.ComplexBuildingReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.NthOrderReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.SectionDependentReaction;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellSubsections;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import bio.singa.simulation.model.sections.concentration.MembraneConcentration;
import bio.singa.simulation.model.sections.concentration.SectionConcentration;
import org.junit.jupiter.api.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Area;
import java.util.List;

import static bio.singa.features.model.Evidence.NO_EVIDENCE;
import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.features.units.UnitProvider.MILLI_MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
class UpdateSchedulerTest {


    @Test
    void testParallelExecution() {

        EntitySupplier entities = new EntitySupplier();
        RegionSupplier regions = new RegionSupplier();

        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(0.1, SECOND));

        // create graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(regions.apicalMembraneRegion);

        MembraneLayer membraneLayer = new MembraneLayer();
        List<Membrane> membranes = MembraneTracer.regionsToMembrane(graph);
        membraneLayer.addMembranes(membranes);
        simulation.setMembraneLayer(membraneLayer);

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ComparableQuantity<Area> area = Quantities.getQuantity(1, MICRO(METRE).pow(2)).asType(Area.class);
        // basal permeability about 1/3 of the active permeability -> 1/3 of the transporters -> 6000/3 == 2000 is equilibrium
        ci.addInitialConcentration(new MembraneConcentration(regions.apicalMembraneRegion, entities.aqp2pp, area, 2000, NO_EVIDENCE));
        ci.addInitialConcentration(regions.cytoplasm, entities.atp, UnitRegistry.concentration(0.5, MILLI_MOLE_PER_LITRE));
        // to be determined
        ci.addInitialConcentration(new SectionConcentration(regions.cytoplasm, entities.pp1, UnitRegistry.concentration(MolarConcentration.moleculesToConcentration(100))));
        ci.addInitialConcentration(new SectionConcentration(regions.cytoplasm, entities.pkaC, UnitRegistry.concentration(MolarConcentration.moleculesToConcentration(20))));
        ci.addInitialConcentration(new SectionConcentration(regions.cytoplasm, entities.pp1R, UnitRegistry.concentration(MolarConcentration.moleculesToConcentration(50))));
        simulation.setConcentrationInitializer(ci);

        // (3) dephosphorylation of AQP2pp to AQP2p by PP1

        // value with no foundation!
        RateConstant phosphoFwd1 = RateConstant.create(10)
                .forward().secondOrder()
                .concentrationUnit(MILLI_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .evidence(NO_EVIDENCE)
                .build();

        // value with no foundation!
        RateConstant phosphoBwd1 = RateConstant.create(1)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .evidence(NO_EVIDENCE)
                .build();

        // complex building
        ComplexBuildingReaction.inSimulation(simulation)
                .identifier("aqp2pp pp1 complex building")
                .of(entities.pp1, phosphoFwd1)
                .in(INNER)
                .by(entities.aqp2pp, phosphoBwd1)
                .to(MEMBRANE)
                .formingComplex(entities.aqp2ppPP1)
                .build();

        // from 10.1073/pnas.94.8.3530
        RateConstant phosphoKcat = RateConstant.create(39.0)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .build();
        RateConstant zero = RateConstant.create(0.0)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // catalysis
        // PPi is ignored!
        SectionDependentReaction.inSimulation(simulation)
                .identifier("aqp2pp dephosphorylation")
                .addSubstrate(entities.aqp2ppPP1, MEMBRANE)
                .addProduct(entities.aqp2p, MEMBRANE)
                .addProduct(entities.pp1, INNER)
                .forwardsRate(phosphoKcat)
                .backwardsRate(zero)
                .build();

        // (2) inhibition of PP1 by PP1Rp

        // value with no foundation!
        // testing criteria binding
        RateConstant bindingFwd1 = RateConstant.create(0.5)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .evidence(NO_EVIDENCE)
                .build();

        // value with no foundation!
        RateConstant bindingBwd1 = RateConstant.create(0.001)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .evidence(NO_EVIDENCE)
                .build();

        // complex formation
        // 10.1002/prot.21438
        ComplexBuildingReaction.inSimulation(simulation)
                .identifier("pp1 inhibition by pp1r")
                .of(entities.pp1, bindingFwd1)
                .in(INNER)
                .by(entities.pp1Rp, bindingBwd1)
                .to(INNER)
                .formingComplex(entities.pp1RpPP1)
                .build();

        // (1) phosphorylation of PP1R to PP1Rp

        RateConstant kf1 = RateConstant.create(240)
                .forward().secondOrder()
                .concentrationUnit(MILLI_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .evidence(NO_EVIDENCE)
                .build();

        RateConstant kb1 = RateConstant.create(50)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .evidence(NO_EVIDENCE)
                .build();

        // estimated from Kd = 0.004 mmol together with kb2
        RateConstant kf2 = RateConstant.create(100)
                .forward().secondOrder()
                .concentrationUnit(MILLI_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .evidence(NO_EVIDENCE)
                .build();

        // estimated from Kd = 0.004 mmol together with kf2
        RateConstant kb2 = RateConstant.create(4)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .evidence(NO_EVIDENCE)
                .build();

        RateConstant kCat = RateConstant.create(500)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .evidence(NO_EVIDENCE)
                .build();

        RateConstant kRel = RateConstant.create(50)
                .forward().firstOrder()
                .timeUnit(SECOND)
                .evidence(NO_EVIDENCE)
                .build();

        // reactions
        ComplexBuildingReaction.inSimulation(simulation)
                .identifier("phosphorylation_atp_binding")
                .of(entities.atp, kf1)
                .in(INNER)
                .by(entities.pkaC, kb1)
                .to(INNER)
                .formingComplex(entities.pkaCatp)
                .build();

        ComplexBuildingReaction.inSimulation(simulation)
                .identifier("phosphorylation_substrate_binding")
                .of(entities.pkaCatp, kf2)
                .in(INNER)
                .by(entities.pp1R, kb2)
                .to(INNER)
                .formingComplex(entities.pkaCatpPp1R)
                .build();

        NthOrderReaction.inSimulation(simulation)
                .identifier("phosphorylation_transfer")
                .addSubstrate(entities.pkaCatpPp1R)
                .addProduct(entities.pkaCadpPp1Rp)
                .rateConstant(kCat)
                .build();

        NthOrderReaction.inSimulation(simulation)
                .identifier("phosphorylation_release")
                .addSubstrate(entities.pkaCadpPp1Rp)
                .addProduct(entities.adp)
                .addProduct(entities.pp1Rp)
                .addProduct(entities.pkaC)
                .rateConstant(kRel)
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
        }

    }


    @Test
    void testAccuracyGainCalculation() {


        Simulation simulation = new Simulation();
        simulation.setMaximalTimeStep(Quantities.getQuantity(10, SECOND));

        // create graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);

        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CellRegions.CYTOPLASM_REGION);

        SmallMolecule firstSubstrate = SmallMolecule.create("S1").build();
        SmallMolecule secondSubstrate = SmallMolecule.create("S2").build();
        SmallMolecule product = SmallMolecule.create("P").build();

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CellSubsections.CYTOPLASM, firstSubstrate, Quantities.getQuantity(100, MICRO_MOLE_PER_LITRE));
        ci.addInitialConcentration(CellSubsections.CYTOPLASM, secondSubstrate, Quantities.getQuantity(50, MICRO_MOLE_PER_LITRE));
        simulation.setConcentrationInitializer(ci);

        RateConstant kf = RateConstant.create(1000)
                .forward().secondOrder()
                .concentrationUnit(MICRO_MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        RateConstant kb = RateConstant.create(1)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        SectionDependentReaction.inSimulation(simulation)
                .addSubstrate(firstSubstrate)
                .addSubstrate(secondSubstrate)
                .addProduct(product)
                .forwardsRate(kf)
                .backwardsRate(kb)
                .build();

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
        }
        System.out.println(simulation.getScheduler().getTimestepsDecreased()+"/"+simulation.getScheduler().getTimestepsIncreased()+" (d/i)");
        System.out.println(simulation.getElapsedTime());

    }
}