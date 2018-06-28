package de.bioforscher.singa.simulation.model.modules.concentration.specifity;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import de.bioforscher.singa.simulation.model.modules.concentration.FieldSupplier;
import de.bioforscher.singa.simulation.model.modules.concentration.functions.EntityDeltaFunction;
import de.bioforscher.singa.simulation.model.sections.CellSubsection;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;

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
                ConcentrationDelta delta = deltaFunction.getFunction().apply(container);
                if (module.deltaIsValid(delta)) {
                    module.handleDelta(new ConcentrationDeltaIdentifier(supply().getCurrentUpdatable(), supply().getCurrentSubsection(), supply().getCurrentEntity()), delta);
                }
            }
        }
    }

    @Override
    public void addDeltaFunction(EntityDeltaFunction entityDeltaFunction) {
        deltaFunctions.add(entityDeltaFunction);
    }

}
