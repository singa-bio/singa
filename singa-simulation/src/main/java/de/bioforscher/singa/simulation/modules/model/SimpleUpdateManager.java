package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author cl
 */
public class SimpleUpdateManager {

    /**
     * Deltas that are to be applied to the node.
     */
    private final List<Delta> finalDeltas;

    /**
     * A list of potential deltas.
     */
    private final List<Delta> potentialDeltas;

    /**
     * A flag signifying if this node is observed.
     */
    private boolean observed;

    /**
     * A flag signifying if this node has a fixed concentration.
     */
    private boolean concentrationFixed;

    private ConcentrationContainer concentrations;

    public SimpleUpdateManager(ConcentrationContainer concentrationContainer) {
        finalDeltas = new ArrayList<>();
        potentialDeltas = new ArrayList<>();
        observed = false;
        concentrationFixed = false;
        concentrations = concentrationContainer;
    }

    public ConcentrationContainer getConcentrationContainer() {
        return concentrations;
    }

    public void setConcentrationContainer(ConcentrationContainer concentrations) {
        this.concentrations = concentrations;
    }

    public boolean isObserved() {
        return observed;
    }

    public void setObserved(boolean observed) {
        this.observed = observed;
    }

    public boolean isConcentrationFixed() {
        return concentrationFixed;
    }

    public void setConcentrationFixed(boolean concentrationFixed) {
        this.concentrationFixed = concentrationFixed;
    }

    /**
     * Returns all deltas that are going to be applied to this node.
     *
     * @return All deltas that are going to be applied to this node.
     */
    public List<Delta> getFinalDeltas() {
        return finalDeltas;
    }

    /**
     * Adds a list of potential deltas to this node.
     *
     * @param potentialDeltas The potential deltas.
     */
    public void addPotentialDeltas(Collection<Delta> potentialDeltas) {
        this.potentialDeltas.addAll(potentialDeltas);
    }

    public List<Delta> getPotentialDeltas() {
        return potentialDeltas;
    }

    /**
     * Adds a potential delta to this node.
     *
     * @param potentialDelta The potential delta.
     */
    public void addPotentialDelta(Delta potentialDelta) {
        potentialDeltas.add(potentialDelta);
    }

    /**
     * Clears the list of potential deltas. Usually done after {@link AutomatonNode#shiftDeltas()} or after rejecting a
     * time step.
     */
    public void clearPotentialDeltas() {
        potentialDeltas.clear();
    }

    /**
     * Shifts the deltas from the potential delta list to the final delta list.
     */
    public void shiftDeltas() {
        finalDeltas.addAll(potentialDeltas);
        if (!observed) {
            potentialDeltas.clear();
        }
    }

    /**
     * Applies all final deltas and clears the delta list.
     */
    public void applyDeltas() {
        if (!concentrationFixed) {
            for (Delta delta : finalDeltas) {
                Quantity<MolarConcentration> updatedConcentration = concentrations.get(delta.getCellSubsection(), delta.getChemicalEntity()).add(delta.getQuantity());
                if (updatedConcentration.getValue().doubleValue() < 0.0) {
                    // FIXME updated concentration should probably not be capped
                    // FIXME the the delta that resulted in the decrease probably had a corresponding increase
                    updatedConcentration = Environment.emptyConcentration();
                }
                concentrations.set(delta.getCellSubsection(), delta.getChemicalEntity(), updatedConcentration);
            }
        }
        finalDeltas.clear();
    }
}
