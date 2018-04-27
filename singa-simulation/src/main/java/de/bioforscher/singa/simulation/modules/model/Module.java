package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

import java.util.Set;

/**
 * A Module encapsulates each model element that applies updates to the simulation.
 *
 * @author cl
 */
public interface Module {

    SimpleStringIdentifier getIdentifier();

    String getStringForProtocol();

    Set<Class<? extends Feature>> getRequiredFeatures();

    Set<ChemicalEntity>  getReferencedEntities();

    /**
     * Calculates all potential updates to the system. The updates are referenced in each node and not applied until
     * their local error has been verified.
     */
    void determineAllDeltas();

    /**
     * Determines the updates for a specific node and returns the {@link LocalError} of the calculation.
     *
     * @param node The node to calculate the update for.
     * @return The error of the calculation.
     */
    LocalError determineDeltasForNode(AutomatonNode node);

    /**
     * Returns the largest local error that has been calculated after calling {@link #determineAllDeltas()}. If the
     * method has linear character, or produces constant results {@link LocalError#MINIMAL_EMPTY_ERROR} is returned.
     * @return The largest local error for the current epoch.
     */
    LocalError getLargestLocalError();

    /**
     * Resets the largest local error in between epochs.
     */
    void resetLargestLocalError();
}
