package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author cl
 */
public abstract class SimulationSetup {

    private static final Logger logger = LoggerFactory.getLogger(SimulationManager.class);

    private Set<ChemicalEntity> chemicalEntities;

    /**
     * Initializes every initialization method in the requires order.
     */
    public void initialize() {
        initializeEnvironmentalParameters();
        initializeChemicalEntities();
        initializeCompartments();
        initializeGraph();
        initializeConcentrations();
        initializeSimulation();
        initializeModules();
    }

    /**
     * Should be called first.
     * Initializes {@link Environment} such as node distance.
     */
    public void initializeEnvironmentalParameters() {
        logger.info("Initializing environmental parameters ...");
    }

    /**
     * Should be called second.
     * Initializes {@link ChemicalEntity}s.
     */
    public void initializeChemicalEntities() {
        logger.info("Initializing chemical entities ...");
    }

    /**
     * Should be called third.
     * Initializes {@link CellSubsection}s.
     */
    public void initializeCompartments() {
        logger.info("Initializing environmental parameters ...");
    }

    /**
     * Should be called fourth.
     * Initializes the {@link AutomatonGraph}.
     */
    public void initializeGraph() {
        logger.info("Initializing cellular automaton graph ...");
    }

    /**
     * Should be called fifth.
     * Initializes the concentrations for each {@link ChemicalEntity} in the {@link AutomatonGraph}.
     */
    public void initializeConcentrations() {
        logger.info("Initializing starting concentrations of chemical entities in graph ...");
    }

    /**
     * Should be called sixth.
     * Initializes the {@link Simulation}.
     */
    public void initializeSimulation() {
        logger.info("Initializing simulation ...");
    }

    /**
     * Should be called seventh.
     * Initializes the {@link de.bioforscher.singa.simulation.modules.model.Module}s.
     */
    public void initializeModules() {
        logger.info("Initializing modules ...");
    }

    /**
     * Starts the simulation.
     */
    public void simulate() {
        logger.info("Starting simulation ...");
    }

    public void addToChemcialEntities(ChemicalEntity entity) {
        chemicalEntities.add(entity);
        logger.debug(entity.getStringForProtocol());
    }

}
