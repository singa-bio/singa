package bio.singa.simulation.model.modules.concentration.scope;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.FieldSupplier;
import bio.singa.simulation.model.modules.concentration.imlementations.Reaction;
import bio.singa.simulation.model.modules.concentration.specifity.UpdateSpecificity;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Quantity;
import java.util.Collection;

/**
 * Independent Updatable {@link ConcentrationBasedModule}s require the integer state of the currently calculated
 * {@link Updatable}. Therefore the update for the current updatable can be calculated and instantly compared with the
 * associated half step delta (e.g. {@link Reaction}).
 *
 * @author cl
 */
public class IndependentUpdate implements UpdateScope {

    /**
     * The storage of the half concentration.
     */
    private ConcentrationContainer halfConcentration;

    /**
     * The associated module.
     */
    private ConcentrationBasedModule module;

    /**
     * Initializes the update scope for the corresponding module.
     * @param module The module.
     */
    public IndependentUpdate(ConcentrationBasedModule module) {
        this.module = module;
    }

    /**
     * Returns a object, managing shared properties of the module.
     * @return The supplier.
     */
    private FieldSupplier supply() {
        return module.getSupplier();
    }

    /**
     * Returns the update specificity behaviour of the module, required for the actual computation of the updates.
     * @return The update specificity behaviour.
     */
    private UpdateSpecificity specify() {
        return module.getSpecificity();
    }

    @Override
    public void processAllUpdatables(Collection<Updatable> updatables) {
        // for each updatable
        for (Updatable updatable : updatables) {
            if (module.getApplicationCondition().test(updatable)) {
                supply().setCurrentUpdatable(updatable);
                processUpdatable(updatable);
            }
        }
    }

    @Override
    public void processUpdatable(Updatable updatable) {
        // calculate full step deltas
        supply().setStrutCalculation(false);
        specify().processContainer(updatable.getConcentrationContainer());
        // explicitly calculate half step concentrations
        determineHalfStepConcentration();
        // calculate half step deltas
        supply().setStrutCalculation(true);
        specify().processContainer(getHalfStepConcentration(updatable));
        // set largest local error
        supply().setLargestLocalError(module.determineLargestLocalError());
        // clear used deltas
        supply().getCurrentFullDeltas().clear();
        supply().getCurrentHalfDeltas().clear();
    }

    @Override
    public void clearPotentialDeltas(Updatable updatable) {
        module.getSimulation().getUpdatables().forEach(Updatable::clearPotentialConcentrationDeltas);
    }

    /**
     * Determines the half step concentrations current full delta.
     */
    private void determineHalfStepConcentration() {
        // initialize the container
        halfConcentration = supply().getCurrentUpdatable().getConcentrationContainer().fullCopy();
        // for each full delta
        for (ConcentrationDelta delta : supply().getCurrentFullDeltas().values()) {
            // get required values
            final CellSubsection currentSubsection = delta.getCellSubsection();
            final ChemicalEntity currentEntity = delta.getChemicalEntity();
            // get full concentration
            Quantity<MolarConcentration> fullConcentration = halfConcentration.get(currentSubsection, currentEntity);
            // add half of the full delta
            Quantity<MolarConcentration> halfStepConcentration = fullConcentration.add(delta.getQuantity().multiply(0.5));
            // update concentration
            halfConcentration.set(currentSubsection, currentEntity, halfStepConcentration);
        }
    }

    @Override
    public ConcentrationContainer getHalfStepConcentration(Updatable updatable) {
        if (!updatable.equals(supply().getCurrentUpdatable())) {
            throw new IllegalStateException("Modules using the independent update scope can only handle one updatable" +
                    " being changed by a single delta function. The updatable " + updatable + " is not the currently" +
                    " referenced updatable " + supply().getCurrentUpdatable() + ".");
        }
        return halfConcentration;
    }

}
