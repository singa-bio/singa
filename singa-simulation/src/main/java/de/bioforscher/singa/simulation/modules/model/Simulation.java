package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import de.bioforscher.singa.simulation.model.rules.AssignmentRule;
import de.bioforscher.singa.simulation.model.rules.AssignmentRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Time;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The simulation class encapsulates everything that is needed to perform a Simulation based on cellular graph automata.
 * Each simulation requires a {@link AutomatonGraph}, a set of {@link Module}s and a set of {@link ChemicalEntity}s.
 * Additionally {@link AssignmentRule}s can be used to assign concentrations to chemical entities based on rules
 * (functions). <p> The class {@link SimulationExamples} provides a set of examples where Simulations are being set up.
 * Principally the following steps should be taken:
 * <pre>
 *     // initialize simulation
 *     Simulation simulation = new Simulation();
 *     // initialize a graph
 *     AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(10, 10, defaultBoundingBox));
 *     // add to simulation
 *     simulation.setGraph(graph);
 *     // initialize chemical entities
 *     ChemicalEntity fructosePhosphate = ChEBIParserService.parse("CHEBI:18105");
 *     // initialize their concentrations in the graph
 *     graph.initializeSpeciesWithConcentration(fructosePhosphate, 0.1);
 *     // add to simulation
 *     simulation.getChemicalEntities().add(fructosePhosphate);
 *     // set environmental simulation parameters (e.g. system diameter, temperature, viscosity)
 *     EnvironmentalParameters.getInstance().setNodeSpacingToDiameter(Quantities.getQuantity(2500.0, NANO(METRE)), 10);
 *     // add diffusion module (free diffusion will be simulated)
 *     simulation.getModules().add(new FreeDiffusion(simulation));
 * </pre>
 * Afterwards the simulation can be performed stepwise by calling the {@link Simulation#nextEpoch} method. For each
 * epoch the size of the time step will be adjusted with the {@link TimeStepHarmonizer}, to ensure numerical stability
 * up to a given epsilon. The method ({@link TimeStepHarmonizer#setEpsilon(double)}) sets the epsilon and the error
 * between two time steps will be kept below that threshold. Epsilons approaching 0 naturally result in very small time
 * steps and therefore long simulation times, at default epsilon is at 0.01. The time step will be optimized for each
 * epoch, resulting in many epochs for critical simulation regimes and large ones for stable regimes. <p>
 *
 * @author cl
 */
public class Simulation {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Simulation.class);
    /**
     * The updating modules.
     */
    private final Set<Module> modules;
    /**
     * The manager of time steps sizes.
     */
    private final TimeStepHarmonizer harmonizer;
    /**
     * The graph structure.
     */
    private AutomatonGraph graph;
    /**
     * The assignment rules.
     */
    private List<AssignmentRule> assignmentRules;
    /**
     * The chemical entities referenced in the graph.
     */
    private Set<ChemicalEntity<?>> chemicalEntities;
    /**
     * The globally applied parameters.
     */
    private Set<SimulationParameter> globalParameters;
    /**
     * The current epoch.
     */
    private long epoch;
    /**
     * The currently elapsed time.
     */
    private ComparableQuantity<Time> elapsedTime;

    /**
     * Creates a new plain simulation.
     */
    public Simulation() {
        modules = new HashSet<>();
        chemicalEntities = new HashSet<>();
        elapsedTime = Quantities.getQuantity(0.0, EnvironmentalParameters.getTimeStep().getUnit());
        epoch = 0;
        harmonizer = new TimeStepHarmonizer(this);
    }

    /**
     * Calculates the next epoch.
     */
    public void nextEpoch() {
        logger.debug("Starting epoch {}.", epoch);
        // apply all modules
        boolean timeStepChanged = harmonizer.step();
        // apply generated deltas
        logger.debug("Applying deltas.");
        for (AutomatonNode node : getGraph().getNodes()) {
            node.applyDeltas();
            // emit events to observers
            // if (node.isObserved()) {
            //    emitNextEpochEvent(node);
            // }
        }
        // update epoch and elapsed time
        updateEpoch();
        // if time step did not change
        if (!timeStepChanged) {
            // if error was below tolerance threshold (10 percent of epsilon)
            if (harmonizer.getEpsilon() - harmonizer.getLargestLocalError().getValue() > 0.1 * harmonizer.getEpsilon()) {
                // try larger time step next time
                harmonizer.increaseTimeStep();
            }
        }
    }

    public void initialize() {
        // initialize concentrationsEn
        // for each referenced species set concentration to 0 in each compartment
        // this requires all entities to be present in chemicalEntities
        // this also is true for eventual products of reactions
//        for (AutomatonNode node : graph.getNodes()) {
//            for (CellSection section : node.getAllReferencedSections()) {
//                for (ChemicalEntity<?> entity : chemicalEntities) {
//                }
//            }
//        }

        // for each module
        // get required entity features
        // for each entity
        // assign feature if feature is assignable
        // check featurable modules if features are annotated
    }

    /**
     * Update the epoch counter and elapsed time.
     */
    private void updateEpoch() {
        epoch++;
        elapsedTime = elapsedTime.add(EnvironmentalParameters.getTimeStep());
    }

    /**
     * Apply all referenced assignment rules.
     */
    public void applyAssignmentRules() {
        for (AssignmentRule rule : assignmentRules) {
            for (AutomatonNode bioNode : graph.getNodes()) {
                rule.applyRule(bioNode);
            }
        }
    }

    /**
     * Returns the simulation graph.
     *
     * @return The simulation graph.
     */
    public AutomatonGraph getGraph() {
        return graph;
    }

    /**
     * Sets the simulation graph and adds all entities referenced in the graph to the chemical entities of the
     * simulation.
     *
     * @param graph The simulation graph.
     */
    public void setGraph(AutomatonGraph graph) {
        this.graph = graph;
        chemicalEntities = new HashSet<>(AutomatonGraphs.generateMapOfEntities(graph).values());
    }

    /**
     * Returns the modules.
     *
     * @return The modules.
     */
    public Set<Module> getModules() {
        return modules;
    }

    /**
     * Returns the assignment rules.
     *
     * @return The assignment rules.
     */
    public List<AssignmentRule> getAssignmentRules() {
        return assignmentRules;
    }

    /**
     * Adds a list of assignment rules, sorting them by their dependencies.
     *
     * @param assignmentRules The assignment rules.
     * @see AssignmentRules#sortAssignmentRulesByPriority(List)
     */
    public void setAssignmentRules(List<AssignmentRule> assignmentRules) {
        this.assignmentRules = AssignmentRules.sortAssignmentRulesByPriority(assignmentRules);

    }

    /**
     * Returns the chemical entities.
     *
     * @return The chemical entities.
     */
    public Set<ChemicalEntity<?>> getChemicalEntities() {
        return chemicalEntities;
    }

    /**
     * Sets all chemical entities at once.
     *
     * @param chemicalEntities The chemical entities.
     */
    public void setChemicalEntities(Set<ChemicalEntity<?>> chemicalEntities) {
        this.chemicalEntities = chemicalEntities;
    }

    /**
     * Returns the current epoch number.
     *
     * @return The current epoch number
     */
    public long getEpoch() {
        return epoch;
    }

    /**
     * Returns the elapsed time after the deltas of the current epoch are applied.
     *
     * @return The elapsed time after the deltas of the current epoch are applied.
     */
    public ComparableQuantity<Time> getElapsedTime() {
        return elapsedTime;
    }

    public void setEpsilon(double epsilon) {
        harmonizer.setEpsilon(epsilon);
    }

}
