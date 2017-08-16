package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.timing.LocalError;
import tec.units.ri.quantity.Quantities;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public abstract class AbstractNeighbourDependentModule implements Module {

    class DeltaIdentifier {

        private final BioNode node;
        private final CellSection section;
        private final ChemicalEntity<?> entity;

        public DeltaIdentifier(BioNode node, CellSection section, ChemicalEntity<?> entity) {
            this.node = node;
            this.section = section;
            this.entity = entity;
        }

        public BioNode getNode() {
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

    protected boolean halfTime;

    private final Simulation simulation;
    private List<Function<ConcentrationContainer, Delta>> deltaFunctions;

    private Predicate<BioNode> conditionalApplication;

    private LocalError largestLocalError;

    private BioNode currentNode;
    private ChemicalEntity currentChemicalEntity;
    private CellSection currentCellSection;

    private Map<DeltaIdentifier, Delta> currentFullDeltas;
    private Map<DeltaIdentifier, Delta> currentHalfDeltas;
    private Map<BioNode, ConcentrationContainer> halfConcentrations;


    public AbstractNeighbourDependentModule(Simulation simulation) {
        this.simulation = simulation;
        this.deltaFunctions = new ArrayList<>();
        this.currentFullDeltas = new HashMap<>();
        this.currentHalfDeltas = new HashMap<>();
        this.halfConcentrations = new HashMap<>();
        this.largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public void addDeltaFunction(Function<ConcentrationContainer, Delta> deltaFunction) {
        this.deltaFunctions.add(deltaFunction);
    }

    public BioNode getCurrentNode() {
        return this.currentNode;
    }

    public ChemicalEntity getCurrentChemicalEntity() {
        return this.currentChemicalEntity;
    }

    public CellSection getCurrentCellSection() {
        return this.currentCellSection;
    }

    public void onlyApplyIf(Predicate<BioNode> predicate) {
        this.conditionalApplication = predicate;
    }

    public void applyAlways() {
        this.conditionalApplication = bioNode -> true;
    }

    protected <FeatureContent> FeatureContent getFeature(Featureable entity, Class<? extends ScalableFeature<FeatureContent>> featureClass) {
        ScalableFeature<FeatureContent> feature = entity.getFeature(featureClass);
        if (this.halfTime) {
            return feature.getHalfScaledQuantity();
        }
        return feature.getScaledQuantity();
    }

    public void determineAllDeltas() {
        AutomatonGraph graph = this.simulation.getGraph();
        // determine full deltas
        this.halfTime = false;
        for (BioNode node : graph.getNodes()) {
            if (this.conditionalApplication.test(node)) {
                this.currentNode = node;
                node.clearPotentialDeltas();
                determineFullDeltas(node.getConcentrations());
            }
        }
        // half step concentrations
        determineHalfStepConcentration();
        // half step deltas
        this.halfTime = true;
        for (Map.Entry<BioNode, ConcentrationContainer> entry : halfConcentrations.entrySet()) {
                this.currentNode = entry.getKey();
                determineHalfStepDeltas(entry.getValue());

        }

        // examine local errors
        examineLocalError();

    }

    public LocalError determineDeltasForNode(BioNode node) {
        // using neighbor dependent modules you need to calculate all changes
        determineAllDeltas();
        return this.largestLocalError;
    }

    public void determineFullDeltas(ConcentrationContainer concentrationContainer) {
        for (CellSection cellSection : this.currentNode.getAllReferencedSections()) {
            this.currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : this.currentNode.getAllReferencedEntities()) {
                this.currentChemicalEntity = chemicalEntity;
                // determine full step deltas and half step concentrations
                for (Function<ConcentrationContainer, Delta> deltaFunction : this.deltaFunctions) {
                    Delta fullDelta = deltaFunction.apply(concentrationContainer);
                    if (fullDelta.getQuantity().getValue().doubleValue() != 0.0) {
                        this.currentFullDeltas.put(new DeltaIdentifier(this.currentNode, this.currentCellSection, this.currentChemicalEntity), fullDelta);
                    }
                }
            }
        }
    }

    private void determineHalfStepDeltas(ConcentrationContainer concentrationContainer) {
        for (CellSection cellSection : this.currentNode.getAllReferencedSections()) {
            this.currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : this.currentNode.getAllReferencedEntities()) {
                this.currentChemicalEntity = chemicalEntity;
                // determine half step deltas and half step concentrations
                for (Function<ConcentrationContainer, Delta> deltaFunction : this.deltaFunctions) {
                    Delta halfDelta = deltaFunction.apply(concentrationContainer).multiply(2.0);
                    this.currentHalfDeltas.put(new DeltaIdentifier(this.currentNode, this.currentCellSection, this.currentChemicalEntity), halfDelta);
                    // and register potential deltas at node
                    this.currentNode.addPotentialDelta(halfDelta);
                }
            }
        }
    }

    private void determineHalfStepConcentration() {
        for (Map.Entry<DeltaIdentifier, Delta> entry : this.currentFullDeltas.entrySet()) {
            DeltaIdentifier key = entry.getKey();
            Delta value = entry.getValue();
            // determine half step deltas
            final double fullConcentration = key.getNode().getAvailableConcentration(key.getEntity(), key.getSection()).getValue().doubleValue();
            final double halfStepConcentration = fullConcentration + 0.5 * value.getQuantity().getValue().doubleValue();
            ConcentrationContainer halfConcentration;
            if (!this.halfConcentrations.containsKey(key.getNode())) {
                halfConcentration = key.getNode().getConcentrations().copy();
                halfConcentration.setAvailableConcentration(key.getSection(), key.getEntity(), Quantities.getQuantity(halfStepConcentration, MOLE_PER_LITRE));
                this.halfConcentrations.put(key.getNode(), halfConcentration);
            } else {
                halfConcentration = halfConcentrations.get(key.getNode());
                halfConcentration.setAvailableConcentration(key.getSection(), key.getEntity(), Quantities.getQuantity(halfStepConcentration, MOLE_PER_LITRE));
            }
        }
    }

    private void examineLocalError() {
        double largestLocalError = -Double.MAX_VALUE;
        DeltaIdentifier largestIdentifier = null;
        for (DeltaIdentifier identifier : this.currentFullDeltas.keySet()) {
            double fullDelta = this.currentFullDeltas.get(identifier).getQuantity().getValue().doubleValue();
            double halfDelta = this.currentHalfDeltas.get(identifier).getQuantity().getValue().doubleValue();
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
        return this.largestLocalError;
    }

    @Override
    public void resetLargestLocalError() {
        this.largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

}
