package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import tec.units.ri.quantity.Quantities;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public abstract class AbstractNeighbourDependentModule implements Module {

    private final Simulation simulation;
    protected boolean halfTime;
    private List<Function<ConcentrationContainer, Delta>> deltaFunctions;
    private Predicate<AutomatonNode> conditionalApplication;
    private LocalError largestLocalError;
    private AutomatonNode currentNode;
    private ChemicalEntity currentChemicalEntity;
    private CellSection currentCellSection;
    private Map<DeltaIdentifier, Delta> currentFullDeltas;
    private Map<DeltaIdentifier, Delta> currentHalfDeltas;
    private Map<AutomatonNode, ConcentrationContainer> halfConcentrations;

    public AbstractNeighbourDependentModule(Simulation simulation) {
        this.simulation = simulation;
        deltaFunctions = new ArrayList<>();
        currentFullDeltas = new HashMap<>();
        currentHalfDeltas = new HashMap<>();
        halfConcentrations = new HashMap<>();
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public void addDeltaFunction(Function<ConcentrationContainer, Delta> deltaFunction) {
        deltaFunctions.add(deltaFunction);
    }

    public AutomatonNode getCurrentNode() {
        return currentNode;
    }

    public ChemicalEntity getCurrentChemicalEntity() {
        return currentChemicalEntity;
    }

    public CellSection getCurrentCellSection() {
        return currentCellSection;
    }

    public void onlyApplyIf(Predicate<AutomatonNode> predicate) {
        conditionalApplication = predicate;
    }

    public void applyAlways() {
        conditionalApplication = bioNode -> true;
    }

    protected <FeatureContent> FeatureContent getFeature(Featureable entity, Class<? extends ScalableFeature<FeatureContent>> featureClass) {
        ScalableFeature<FeatureContent> feature = entity.getFeature(featureClass);
        if (halfTime) {
            return feature.getHalfScaledQuantity();
        }
        return feature.getScaledQuantity();
    }

    public void determineAllDeltas() {
        AutomatonGraph graph = simulation.getGraph();
        // determine full deltas
        halfTime = false;
        for (AutomatonNode node : graph.getNodes()) {
            if (conditionalApplication.test(node)) {
                currentNode = node;
                determineFullDeltas(node.getConcentrationContainer());
            }
        }
        // half step concentrations
        determineHalfStepConcentration();
        // half step deltas
        halfTime = true;
        for (Map.Entry<AutomatonNode, ConcentrationContainer> entry : halfConcentrations.entrySet()) {
            currentNode = entry.getKey();
            determineHalfStepDeltas(entry.getValue());

        }

        // examine local errors
        examineLocalError();

    }

    public LocalError determineDeltasForNode(AutomatonNode node) {
        // using neighbor dependent modules you need to calculate all changes
        determineAllDeltas();
        return largestLocalError;
    }

    public void determineFullDeltas(ConcentrationContainer concentrationContainer) {
        for (CellSection cellSection : currentNode.getAllReferencedSections()) {
            currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : currentNode.getAllReferencedEntities()) {
                currentChemicalEntity = chemicalEntity;
                // determine full step deltas and half step concentrations
                for (Function<ConcentrationContainer, Delta> deltaFunction : deltaFunctions) {
                    Delta fullDelta = deltaFunction.apply(concentrationContainer);
                    if (fullDelta.getQuantity().getValue().doubleValue() != 0.0) {
                        currentFullDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, currentChemicalEntity), fullDelta);
                    }
                }
            }
        }
    }

    private void determineHalfStepDeltas(ConcentrationContainer concentrationContainer) {
        for (CellSection cellSection : currentNode.getAllReferencedSections()) {
            currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : currentNode.getAllReferencedEntities()) {
                currentChemicalEntity = chemicalEntity;
                // determine half step deltas and half step concentrations
                for (Function<ConcentrationContainer, Delta> deltaFunction : deltaFunctions) {
                    Delta halfDelta = deltaFunction.apply(concentrationContainer).multiply(2.0);
                    currentHalfDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, currentChemicalEntity), halfDelta);
                    // and register potential deltas at node
                    currentNode.addPotentialDelta(halfDelta);
                }
            }
        }
    }

    private void determineHalfStepConcentration() {
        for (Map.Entry<DeltaIdentifier, Delta> entry : currentFullDeltas.entrySet()) {
            DeltaIdentifier key = entry.getKey();
            Delta value = entry.getValue();
            // determine half step deltas
            final double fullConcentration = key.getNode().getAvailableConcentration(key.getEntity(), key.getSection()).getValue().doubleValue();
            final double halfStepConcentration = fullConcentration + 0.5 * value.getQuantity().getValue().doubleValue();
            ConcentrationContainer halfConcentration;
            if (!halfConcentrations.containsKey(key.getNode())) {
                halfConcentration = key.getNode().getConcentrationContainer().getCopy();
                halfConcentration.setAvailableConcentration(key.getSection(), key.getEntity(), Quantities.getQuantity(halfStepConcentration, MOLE_PER_LITRE));
                halfConcentrations.put(key.getNode(), halfConcentration);
            } else {
                halfConcentration = halfConcentrations.get(key.getNode());
                halfConcentration.setAvailableConcentration(key.getSection(), key.getEntity(), Quantities.getQuantity(halfStepConcentration, MOLE_PER_LITRE));
            }
        }
    }

    private void examineLocalError() {
        // no deltas mean this module did not change anything in the course of this simulation step
        if (currentFullDeltas.isEmpty()) {
            return;
        }
        double largestLocalError = -Double.MAX_VALUE;
        DeltaIdentifier largestIdentifier = null;
        for (DeltaIdentifier identifier : currentFullDeltas.keySet()) {
            double fullDelta = currentFullDeltas.get(identifier).getQuantity().getValue().doubleValue();
            double halfDelta = currentHalfDeltas.get(identifier).getQuantity().getValue().doubleValue();
            double localError = 0.0;
            // if there is no change, there is no error
            if (fullDelta != 0.0 && halfDelta != 0) {
                // calculate error
                localError = Math.abs(1 - (fullDelta / halfDelta));
            }
            // determine the largest error in the current deltas
            if (largestLocalError < localError) {
                largestIdentifier = identifier;
                largestLocalError = localError;
            }
        }
        Objects.requireNonNull(largestIdentifier);
        // set local error and return local error
        this.largestLocalError = new LocalError(largestIdentifier.getNode(), largestIdentifier.getEntity(), largestLocalError);
    }

    @Override
    public LocalError getLargestLocalError() {
        return largestLocalError;
    }

    @Override
    public void resetLargestLocalError() {
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    class DeltaIdentifier {

        private final AutomatonNode node;
        private final CellSection section;
        private final ChemicalEntity<?> entity;

        public DeltaIdentifier(AutomatonNode node, CellSection section, ChemicalEntity<?> entity) {
            this.node = node;
            this.section = section;
            this.entity = entity;
        }

        public AutomatonNode getNode() {
            return node;
        }

        public CellSection getSection() {
            return section;
        }

        public ChemicalEntity<?> getEntity() {
            return entity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DeltaIdentifier that = (DeltaIdentifier) o;

            if (node != null ? !node.equals(that.node) : that.node != null) return false;
            if (section != null ? !section.equals(that.section) : that.section != null) return false;
            return entity != null ? entity.equals(that.entity) : that.entity == null;
        }

        @Override
        public int hashCode() {
            int result = node != null ? node.hashCode() : 0;
            result = 31 * result + (section != null ? section.hashCode() : 0);
            result = 31 * result + (entity != null ? entity.hashCode() : 0);
            return result;
        }
    }

}
