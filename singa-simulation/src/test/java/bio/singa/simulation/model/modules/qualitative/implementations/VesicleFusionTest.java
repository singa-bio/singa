package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneTracer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Length;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
class VesicleFusionTest {

    @Test
    void shouldSimulateFusionTethering() {
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
        Protein vamp2 = new Protein.Builder("VAMP2")
                .assignFeature(new UniProtIdentifier("Q15836"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "R-SNARE"))
                .build();

        Protein vamp3 = new Protein.Builder("VAMP3")
                .assignFeature(new UniProtIdentifier("P63027"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "R-SNARE"))
                .build();

        Protein syntaxin3 = new Protein.Builder("Syntaxin 3")
                .assignFeature(new UniProtIdentifier("Q13277"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qa-SNARE"))
                .build();

        Protein syntaxin4 = new Protein.Builder("Syntaxin 4")
                .assignFeature(new UniProtIdentifier("Q12846"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qa-SNARE"))
                .build();

        Protein snap23 = new Protein.Builder("SNAP23")
                .assignFeature(new UniProtIdentifier("O00161"))
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qbc-SNARE"))
                .build();

        ComplexedChemicalEntity snareComplex1 = ComplexedChemicalEntity.create(syntaxin3.getIdentifier().getContent() + ":" + snap23.getIdentifier().getContent())
                .addAssociatedPart(syntaxin3)
                .addAssociatedPart(snap23)
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qabc-SNARE"))
                .build();

        ComplexedChemicalEntity snareComplex2 = ComplexedChemicalEntity.create(syntaxin4.getIdentifier().getContent() + ":" + snap23.getIdentifier().getContent())
                .addAssociatedPart(syntaxin4)
                .addAssociatedPart(snap23)
                .annotation(new Annotation<>(AnnotationType.NOTE, "SNARE type", "Qabc-SNARE"))
                .build();

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setPosition(new Vector2D(50.0, 50.0));
        node.setCellRegion(CellRegion.MEMBRANE);
        node.getConcentrationContainer().set(CellTopology.MEMBRANE, snareComplex1, MolarConcentration.moleculesToConcentration(10));
        simulation.setGraph(graph);

        // setup membrane
        List<Membrane> membranes = MembraneTracer.regionsToMembrane(graph);
        MembraneLayer membraneLayer = new MembraneLayer();
        membraneLayer.addMembranes(membranes);
        simulation.setMembraneLayer(membraneLayer);

        // setup vesicle
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        Vesicle vesicle = new Vesicle(new Vector2D(49.0, 49.0), Quantities.getQuantity(100.0, NANO(METRE)));
        vesicle.getConcentrationContainer().set(CellTopology.MEMBRANE, vamp3, MolarConcentration.moleculesToConcentration(10));
        vesicleLayer.addVesicle(vesicle);
        simulation.setVesicleLayer(vesicleLayer);

        // setup fusion module
        VesicleFusion fusion = new VesicleFusion();
        List<ChemicalEntity> qSnareEntities = new ArrayList<>();
        qSnareEntities.add(snareComplex1);
        qSnareEntities.add(snareComplex2);
        MatchingQSnares qSnares = new MatchingQSnares(qSnareEntities);
        fusion.setFeature(qSnares);

        List<ChemicalEntity> rSnareEntities = new ArrayList<>();
        rSnareEntities.add(vamp2);
        rSnareEntities.add(vamp3);
        MatchingRSnares rSnares = new MatchingRSnares(rSnareEntities);
        fusion.setFeature(rSnares);
        fusion.setFeature(new SNAREFusionPairs(Quantities.getQuantity(3, ONE)));
        fusion.setFeature(FusionTime.DEFAULT_FUSION_TIME);
        fusion.setFeature(AttachmentDistance.DEFAULT_DYNEIN_ATTACHMENT_DISTANCE);
        simulation.addModule(fusion);

        while (simulation.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(19.0, SECOND))) {
            simulation.nextEpoch();
        }

        assertEquals(7.0, MolarConcentration.concentrationToMolecules(node.getConcentrationContainer().get(CellTopology.MEMBRANE, vamp3)).getValue().doubleValue(), 1e-10);
        assertEquals(7.0, MolarConcentration.concentrationToMolecules(node.getConcentrationContainer().get(CellTopology.MEMBRANE, snareComplex1)).getValue().doubleValue(), 1e-10);

    }


}