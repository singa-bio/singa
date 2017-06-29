package de.bioforscher.singa.simulation.model.concentrations;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author cl
 */
public class DeltaContainer {

    private List<ConcentrationDelta> deltas;

    public DeltaContainer() {
        this.deltas = new ArrayList<>();
    }

    public void addDelta(ConcentrationDelta delta) {
        ListIterator<ConcentrationDelta> iterator = deltas.listIterator();
        while (iterator.hasNext()) {
            ConcentrationDelta currentDelta = iterator.next();
            if (delta.getCellSection().equals(currentDelta.getCellSection()) &&
                    delta.getEntity().equals(currentDelta.getEntity())) {
                iterator.set(currentDelta.merge(delta));
                return;
            }
        }
        deltas.add(delta);
    }

    public List<ConcentrationDelta> getDeltas() {
        return deltas;
    }

    public void clear() {
        this.deltas.clear();
    }
}
