package de.bioforscher.singa.simulation.modules.newmodules.specifity;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.DeltaIdentifier;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;
import de.bioforscher.singa.simulation.modules.newmodules.functions.EntityDeltaFunction;
import de.bioforscher.singa.simulation.modules.newmodules.module.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.modules.newmodules.module.FieldSupplier;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class EntitySpecific implements UpdateSpecificity<EntityDeltaFunction> {

    private List<EntityDeltaFunction> deltaFunctions;
    private ConcentrationBasedModule<EntityDeltaFunction> module;

    public EntitySpecific(ConcentrationBasedModule<EntityDeltaFunction> module) {
        this.module = module;
        deltaFunctions = new ArrayList<>();
    }

    private FieldSupplier supply() {
        return module.getSupplier();
    }

    public void processContainer(ConcentrationContainer container) {
        for (CellSubsection cellSection : supply().getCurrentUpdatable().getAllReferencedSections()) {
            supply().setCurrentSubsection(cellSection);
            for (ChemicalEntity chemicalEntity : module.getReferencedEntities()) {
                supply().setCurrentEntity(chemicalEntity);
                determineDeltas(container);
            }
        }
    }

    @Override
    public void determineDeltas(ConcentrationContainer container) {
        // for each designated function
        for (EntityDeltaFunction deltaFunction : deltaFunctions) {
            // test condition
            if (deltaFunction.getCondition().test(container)) {
                // apply function
                Delta delta = deltaFunction.getFunction().apply(container);
                if (module.deltaIsValid(delta)) {
                    module.handleDelta(new DeltaIdentifier(supply().getCurrentUpdatable(), supply().getCurrentSubsection(), supply().getCurrentEntity()), delta);
                }
            }
        }
    }

    @Override
    public void addDeltaFunction(EntityDeltaFunction entityDeltaFunction) {
        deltaFunctions.add(entityDeltaFunction);
    }

}
