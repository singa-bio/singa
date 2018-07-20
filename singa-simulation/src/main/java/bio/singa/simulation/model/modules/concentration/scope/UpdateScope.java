package bio.singa.simulation.model.modules.concentration.scope;

import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.Collection;

/**
 * The Update Scope determines the modules dependence on the rest of the simulation.
 * <ul>
 *     <li> Modules depending only on the integer state of a single {@link Updatable} are {@link IndependentUpdate}ble.
 *     <li> Modules depending on all Updatables (or a specific subsection of updatables) are {@link DependentUpdate}ble.
 *     <li> Modules depending on few (mostly surrounding updatables) are {@link SemiDependentUpdate}ble.
 * </ul>
 * The scope then determines half concentration storage behaviour used during numerical calculation and in which order
 * deltas are computed.
 *
 * @author cl
 */
public interface UpdateScope {

    /**
     * Processes all given updatables. This includes calculating the delta functions of the corresponding module,
     * calculating the current numerical error and pushing potential Deltas to the updatables, ready to be applied.
     * @param updatables The updatables to be processed.
     */
    void processAllUpdatables(Collection<Updatable> updatables);

    /**
     * Calculates the given updatable. If it is required to calculate other updatables to evaluate the state of this
     * updatable this will be done depending on the implementation of the scope.
     */
    void processUpdatable(Updatable updatable);

    /**
     * Clears all concentration deltas that may be assigned to the given updatable.
     * @param updatable The updatable.
     */
    void clearPotentialDeltas(Updatable updatable);

    /**
     * Return the half step concentration of a node after the full update has been calculated for the full step and the
     * resulting delta is known.
     * @param updatable The updatable.
     * @return A concentration container with the concentrations at the half time step.
     */
    ConcentrationContainer getHalfStepConcentration(Updatable updatable);

}
