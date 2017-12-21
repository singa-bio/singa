package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author cl
 */
public abstract class AbstractModule implements Module {

    protected Simulation simulation;
    protected boolean halfTime;
    protected LocalError largestLocalError;
    protected AutomatonNode currentNode;
    protected CellSection currentCellSection;
    protected Predicate<AutomatonNode> conditionalApplication;

    protected Map<DeltaIdentifier, Delta> currentFullDeltas;
    protected Map<DeltaIdentifier, Delta> currentHalfDeltas;

    public AbstractModule(Simulation simulation) {
        this.simulation = simulation;
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
        // apply always
        conditionalApplication = automatonNode -> true;
        currentFullDeltas = new HashMap<>();
        currentHalfDeltas = new HashMap<>();
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public AutomatonNode getCurrentNode() {
        return currentNode;
    }

    public CellSection getCurrentCellSection() {
        return currentCellSection;
    }

    public void onlyApplyIf(Predicate<AutomatonNode> predicate) {
        conditionalApplication = predicate;
    }

    protected <FeatureContent> FeatureContent getFeature(Featureable entity, Class<? extends ScalableFeature<FeatureContent>> featureClass) {
        ScalableFeature<FeatureContent> feature = entity.getFeature(featureClass);
        if (halfTime) {
            return feature.getHalfScaledQuantity();
        }
        return feature.getScaledQuantity();
    }

    @Override
    public LocalError getLargestLocalError() {
        return largestLocalError;
    }

    @Override
    public void resetLargestLocalError() {
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

}
