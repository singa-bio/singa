package de.bioforscher.singa.simulation.model.concentrations;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author cl
 */
public class DeltaContainer {

    private List<Delta> deltas;

    public DeltaContainer() {
        this.deltas = new ArrayList<>();
    }

    public void addDelta(Delta delta) {
        ListIterator<Delta> iterator = deltas.listIterator();
        while (iterator.hasNext()) {
            Delta currentDelta = iterator.next();
            if (delta.getCellSection().equals(currentDelta.getCellSection()) &&
                    delta.getEntity().equals(currentDelta.getEntity())) {
                iterator.set(currentDelta.merge(delta));
                return;
            }
        }
        deltas.add(delta);
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public void clear() {
        this.deltas.clear();
    }
}
