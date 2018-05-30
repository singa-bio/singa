package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.ComplexedChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Receptor;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import static de.bioforscher.singa.features.parameters.Environment.getTransformedMolarConcentration;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.newsections.CellSubsection.SECTION_A;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.INNER;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.MEMBRANE;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.*;

/**
 * @author cl
 */
public class MonovalentReceptorBindingTest {

    private static final Logger logger = LoggerFactory.getLogger(MonovalentReceptorBindingTest.class);

    public static void main(String[] args) {
        Environment.setNodeDistance(Quantities.getQuantity(1.0, MILLI(METRE)));
        logger.info("Testing Monovalent Receptor Binding.");

        // see Receptors (Lauffenburger) p. 30
        // prazosin, CHEBI:8364
        ChemicalEntity ligand = new SmallMolecule.Builder("ligand")
                .name("prazosin")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:8364"))
                .build();

        // the corresponding rate constants
        RateConstant forwardsRate = RateConstant.create(2.4e7).forward().secondOder().concentrationUnit(MOLE_PER_LITRE).timeUnit(MINUTE).build();
        RateConstant backwardsRate = RateConstant.create(0.18).backward().firstOrder().timeUnit(MINUTE).build();

        // alpha-1 adrenergic receptor, P35348
        Receptor receptor = new Receptor.Builder("receptor")
                .name("alpha-1 adrenergic receptor")
                .additionalIdentifier(new UniProtIdentifier("P35348"))
                .build();

        // create simulation
        Simulation simulation = new Simulation();

        // setup graph
        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);
        // concentrations
        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setCellRegion(CellRegion.MEMBRANE);
        membraneNode.getConcentrationContainer().set(SECTION_A, ligand, Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));
        membraneNode.getConcentrationContainer().set(CellSubsection.MEMBRANE, receptor, Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(getTransformedMolarConcentration()));

        // create and add module
        ComplexBuildingReaction reaction = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("binding reaction")
                .of(ligand, forwardsRate)
                .in(INNER)
                .by(receptor, backwardsRate)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity complex = reaction.getComplex();

        // checkpoints
        Quantity<Time> currentTime;
        Quantity<Time> firstCheckpoint = Quantities.getQuantity(0.05, MILLI(SECOND));
        boolean firstCheckpointPassed = false;
        Quantity<Time> secondCheckpoint = Quantities.getQuantity(2.0, MILLI(SECOND));
        // run simulation
        while ((currentTime = simulation.getElapsedTime().to(MILLI(SECOND))).getValue().doubleValue() < secondCheckpoint.getValue().doubleValue()) {
            simulation.nextEpoch();
            if (!firstCheckpointPassed && currentTime.getValue().doubleValue() > firstCheckpoint.getValue().doubleValue()) {
                logger.info("First checkpoint reached at {}.", simulation.getElapsedTime().to(MILLI(SECOND)));
                assertEquals(0.00821, membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, receptor).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.00821, membraneNode.getConcentrationContainer().get(INNER, ligand).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                assertEquals(0.01678, membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, complex).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
                firstCheckpointPassed = true;
            }
        }

        // check final values
        assertEquals(0.0001, membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, receptor).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.0001, membraneNode.getConcentrationContainer().get(INNER, ligand).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        assertEquals(0.0243, membraneNode.getConcentrationContainer().get(CellSubsection.MEMBRANE, complex).to(MOLE_PER_LITRE).getValue().doubleValue(), 1e-3);
        logger.info("Second and final checkpoint (at {}) reached successfully.", simulation.getElapsedTime().to(MILLI(SECOND)));
        Environment.reset();
    }

}