package bio.singa.simulation.model.modules;

import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.UpdateScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static bio.singa.simulation.model.modules.concentration.ModuleState.*;

/**
 * @author cl
 */
public abstract class AbstractUpdateModule implements UpdateModule {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractUpdateModule.class);

    /**
     * The referenced simulation.
     */
    private Simulation simulation;

    /**
     * The current state of this module.
     */
    private ModuleState state;

    /**
     * The identifier of this module.
     */
    private String identifier;

    public AbstractUpdateModule() {
        state = PENDING;
        identifier = getClass().getSimpleName();
    }

    @Override
    public void run() {
        UpdateScheduler scheduler = getSimulation().getScheduler();
        while (state == PENDING || state == REQUIRING_RECALCULATION) {
            switch (state) {
                case PENDING:
                    // calculate update
                    logger.debug("calculating updates for {}.", Thread.currentThread().getName());
                    calculateUpdates();
                    break;
                case REQUIRING_RECALCULATION:
                    // optimize time step
                    logger.debug("{} requires recalculation.", Thread.currentThread().getName());
                    boolean prioritizedModule = scheduler.interrupt();
                    if (prioritizedModule) {
                        optimizeTimeStep();
                    } else {
                        state = INTERRUPTED;
                    }
                    break;
            }
        }
        scheduler.getCountDownLatch().countDown();
        logger.debug("Module finished {}, latch at {}.", Thread.currentThread().getName(), scheduler.getCountDownLatch().getCount());
    }

    @Override
    public void reset() {
        state = ModuleState.PENDING;
        onReset();
    }

    /**
     * Returns the referenced simulation.
     *
     * @return The referenced simulation.
     */
    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * References the simulation to this module.
     *
     * @param simulation The simulation.
     */
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Returns the identifier of this module.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier of this module.
     *
     * @param identifier The identifier.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ModuleState getState() {
        return state;
    }

    public void setState(ModuleState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractUpdateModule that = (AbstractUpdateModule) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

}
