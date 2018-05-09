package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.events.EpochUpdateWriter;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import de.bioforscher.singa.simulation.model.rules.AssignmentRule;
import de.bioforscher.singa.simulation.model.rules.AssignmentRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Time;
import java.io.IOException;
import java.util.*;

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
     * The logger for any changes in the simulation.
     */
    private EpochUpdateWriter epochUpdateWriter;

    /**
     * The nodes, that are observed during simulation.
     */
    private Set<AutomatonNode> observedNodes;

    /**
     * The assignment rules.
     */
    private List<AssignmentRule> assignmentRules;

    /**
     * The chemical entities referenced in the graph.
     */
    private Map<SimpleStringIdentifier, ChemicalEntity> chemicalEntities;

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

    private boolean modulesInitialized;

    /**
     * Creates a new plain simulation.
     */
    public Simulation() {
        modules = new HashSet<>();
        chemicalEntities = new HashMap<>();
        observedNodes = new HashSet<>();
        elapsedTime = Quantities.getQuantity(0.0, EnvironmentalParameters.getTimeStep().getUnit());
        modulesInitialized = false;
        epoch = 0;
        harmonizer = new TimeStepHarmonizer(this);
    }

    /**
     * Calculates the next epoch.
     */
    public void nextEpoch() {
        logger.debug("Starting epoch {} ({}).", epoch, elapsedTime);
        if (!modulesInitialized) {
            initializeModules();
            initializeGraph();
            modulesInitialized = true;
        }
        // clear observed nodes if necessary
        if (!observedNodes.isEmpty()) {
            for (AutomatonNode observedNode : observedNodes) {
                observedNode.clearPotentialDeltas();
            }
        }
        // apply all modules
        boolean timeStepChanged = harmonizer.step();
        // apply generated deltas
        logger.debug("Applying deltas.");
        for (AutomatonNode node : getGraph().getNodes()) {
            node.applyDeltas();
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

    private void initializeModules() {
        logger.info("Initializing features required for each module.");
        for (Module module : modules) {
            module.checkFeatures();
        }
    }

    private void initializeGraph() {
        logger.info("Initializing chemical entities.");
        if (graph == null) {
            throw new IllegalStateException("No graph has been assigned to the simulation.");
        }
        // reference all entities
        for (AutomatonNode automatonNode : graph.getNodes()) {
            ConcentrationContainer concentrationContainer = automatonNode.getConcentrationContainer();
            if (concentrationContainer instanceof MembraneContainer) {
                ((MembraneContainer) concentrationContainer).setReferencedEntities(new HashSet<>(chemicalEntities.values()));
            }
        }
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
    public Collection<ChemicalEntity> getChemicalEntities() {
        return chemicalEntities.values();
    }

    public Map<SimpleStringIdentifier, ChemicalEntity> getChemicalEntityMap() {
        return chemicalEntities;
    }

    public ChemicalEntity getChemicalEntity(String primaryIdentifier) {
        return chemicalEntities.get(new SimpleStringIdentifier(primaryIdentifier));
    }

    public void addReferencedEntities(Collection<? extends ChemicalEntity> entities) {
        for (ChemicalEntity entity : entities) {
            addReferencedEntity(entity);
        }
    }

    public CellSection getCellSection(String identifier) {
        return graph.getCellSection(identifier);
    }

    public void addReferencedEntity(ChemicalEntity chemicalEntity) {
        chemicalEntities.put(chemicalEntity.getIdentifier(), chemicalEntity);
    }

    public void setEpochUpdateWriter(EpochUpdateWriter epochUpdateWriter) {
        this.epochUpdateWriter = epochUpdateWriter;
    }

    public void observeNode(AutomatonNode node) {
        observedNodes.add(node);
        node.setObserved(true);
        if (epochUpdateWriter != null) {
            try {
                epochUpdateWriter.addNodeToObserve(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<AutomatonNode> getObservedNodes() {
        return observedNodes;
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
