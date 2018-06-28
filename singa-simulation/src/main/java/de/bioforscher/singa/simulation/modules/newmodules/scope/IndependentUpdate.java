package de.bioforscher.singa.simulation.modules.newmodules.scope;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.LocalError;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;
import de.bioforscher.singa.simulation.modules.newmodules.module.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.modules.newmodules.module.FieldSupplier;
import de.bioforscher.singa.simulation.modules.newmodules.specifity.UpdateSpecificity;

import javax.measure.Quantity;
import java.util.Collection;

/**
 * @author cl
 */
public class IndependentUpdate implements UpdateScope {

    private ConcentrationContainer halfConcentration;
    private ConcentrationBasedModule module;

    public IndependentUpdate(ConcentrationBasedModule module) {
        this.module = module;
    }

    private FieldSupplier supply() {
        return module.getSupplier();
    }

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
    public LocalError processUpdatable(Updatable updatable) {
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
        return supply().getLargestLocalError();
    }

    @Override
    public void clearPotentialDeltas(Updatable updatable) {
        module.getSimulation().getUpdatables().forEach(Updatable::clearPotentialDeltas);
    }

    private void determineHalfStepConcentration() {
        halfConcentration = supply().getCurrentUpdatable().getConcentrationContainer().fullCopy();
        for (Delta delta : supply().getCurrentFullDeltas().values()) {
            CellSubsection currentSubsection = delta.getCellSubsection();
            ChemicalEntity currentEntity = delta.getChemicalEntity();
            Quantity<MolarConcentration> fullConcentration = halfConcentration.get(currentSubsection, currentEntity);
            Quantity<MolarConcentration> halfStepConcentration = fullConcentration.add(delta.getQuantity().multiply(0.5));
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
