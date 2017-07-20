package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.timing.LocalError;
import tec.units.ri.quantity.Quantities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public abstract class AbstractModule implements Module {

    protected boolean halfTime;

    private final Simulation simulation;
    private List<Function<ConcentrationContainer, Delta>> deltaFunctions;

    private LocalError largestLocalError;

    private BioNode currentNode;
    private ChemicalEntity currentChemicalEntity;
    private CellSection currentCellSection;
    private List<Delta> currentFullDeltas;
    private List<Delta> currentHalfDeltas;
    private ConcentrationContainer currentHalfConcentrations;

    public AbstractModule(Simulation simulation) {
        this.simulation = simulation;
        this.currentFullDeltas = new ArrayList<>();
        this.currentHalfDeltas = new ArrayList<>();
        this.largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public void addDeltaFunction(Function<ConcentrationContainer, Delta> deltaFunction) {
        this.deltaFunctions.add(deltaFunction);
    }

    public Simulation getSimulation() {
        return this.simulation;
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

    public void determineAllDeltas() {
        AutomatonGraph graph = this.simulation.getGraph();
        // determine deltas
        for (BioNode node : graph.getNodes()) {
            determineDeltasForNode(node);
        }
    }

    public LocalError determineDeltasForNode(BioNode node) {
        this.currentNode = node;
        node.clearPotentialDeltas();
        ConcentrationContainer fullConcentrations = node.getConcentrations();
        this.currentHalfConcentrations = fullConcentrations.copy();
        return determineDeltas(fullConcentrations);
    }

    public LocalError determineDeltas(ConcentrationContainer concentrationContainer) {
        LocalError temporaryLargestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
        for (CellSection cellSection : this.currentNode.getAllReferencedSections()) {
            this.currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : this.currentNode.getAllReferencedEntities()) {
                this.currentChemicalEntity = chemicalEntity;
                // determine full step deltas and half step concentrations
                this.halfTime = false;
                for (Function<ConcentrationContainer, Delta> deltaFunction : this.deltaFunctions) {
                    Delta fullDelta = deltaFunction.apply(concentrationContainer);
                    setHalfStepConcentration(fullDelta);
                    this.currentFullDeltas.add(fullDelta);
                }
                // determine half step deltas
                this.halfTime = true;
                for (Function<ConcentrationContainer, Delta> deltaFunction : this.deltaFunctions) {
                    Delta halfDelta = deltaFunction.apply(this.currentHalfConcentrations).multiply(2.0);
                    this.currentHalfDeltas.add(halfDelta);
                }
                // and register potential deltas at node
                this.currentNode.addPotentialDeltas(this.currentHalfDeltas);
                // examine local errors
                LocalError currentLocalError = examineLocalError();
                // compare to current maximum
                if (temporaryLargestLocalError.getValue() < currentLocalError.getValue()) {
                    // set if this is larger
                    this.largestLocalError = currentLocalError;
                }
            }
        }
        return temporaryLargestLocalError;
    }

    private LocalError examineLocalError() {
        // only if there is any change there can be a local error
        // careful we rely on putting the deltas in the same order as they are referenced in the list of delta functions
        double temporaryLargestLocalError = -Double.MAX_VALUE;
        for (int i = 0; i < this.currentFullDeltas.size(); i++) {
            double fullDelta = this.currentFullDeltas.get(i).getQuantity().getValue().doubleValue();
            double halfDelta = this.currentHalfDeltas.get(i).getQuantity().getValue().doubleValue();
            double localError = 0.0;
            // if there is no change, there is no error
            if (fullDelta != 0.0 && halfDelta != 0) {
                // calculate error
                localError = Math.abs(1 - (fullDelta / halfDelta));
            }
            // determine the largest error in the current deltas
            if (temporaryLargestLocalError < localError) {
                temporaryLargestLocalError = localError;
            }
        }
        // compare to current maximum
        if (this.largestLocalError.getValue() < temporaryLargestLocalError) {
            // set if this is larger
            this.largestLocalError = new LocalError(this.currentNode, this.currentChemicalEntity, temporaryLargestLocalError);
        }
        // return current local error
        return new LocalError(this.currentNode, this.currentChemicalEntity, temporaryLargestLocalError);
    }

    private void setHalfStepConcentration(Delta fullDelta) {
        final double fullConcentration = this.currentNode.getAvailableConcentration(this.currentChemicalEntity, this.currentCellSection).getValue().doubleValue();
        final double halfStepConcentration = fullConcentration + 0.5 * fullDelta.getQuantity().getValue().doubleValue();
        this.currentHalfConcentrations.setAvailableConcentration(this.currentCellSection, this.currentChemicalEntity, Quantities.getQuantity(halfStepConcentration, MOLE_PER_LITRE));
    }

    @Override
    public LocalError getLargestLocalError() {
        return this.largestLocalError;
    }
}
