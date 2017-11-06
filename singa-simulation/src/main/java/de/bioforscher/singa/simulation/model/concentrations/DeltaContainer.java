package de.bioforscher.singa.simulation.model.concentrations;


import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * The container for the final {@link Delta}s that are applied to a {@link AutomatonNode}. Deltas that are added are
 * automatically summed up for each chemical entity.
 *
 * @author cl
 */
public class DeltaContainer {

    /**
     * The deltas that are to be applied.
     */
    private List<Delta> deltas;

    /**
     * Creates a new empty delta container.
     */
    public DeltaContainer() {
        this.deltas = new ArrayList<>();
    }

    /**
     * Adds a delta to the container.
     *
     * @param delta The delta.
     */
    public void addDelta(Delta delta) {
        ListIterator<Delta> iterator = deltas.listIterator();
        while (iterator.hasNext()) {
            Delta currentDelta = iterator.next();
            if (delta.getCellSection().equals(currentDelta.getCellSection()) &&
                    delta.getChemicalEntity().equals(currentDelta.getChemicalEntity())) {
                iterator.set(currentDelta.merge(delta));
                return;
            }
        }
        deltas.add(delta);
    }

    /**
     * Returns all deltas.
     *
     * @return All deltas.
     */
    public List<Delta> getDeltas() {
        return deltas;
    }

    /**
     * Removes all deltas from this container.
     */
    public void clear() {
        this.deltas.clear();
    }
}
