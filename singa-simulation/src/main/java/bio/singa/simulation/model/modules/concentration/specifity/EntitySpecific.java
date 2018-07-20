package bio.singa.simulation.model.modules.concentration.specifity;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.FieldSupplier;
import bio.singa.simulation.model.modules.concentration.functions.AbstractDeltaFunction;
import bio.singa.simulation.model.modules.concentration.functions.EntityDeltaFunction;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity specific {@link ConcentrationBasedModule}s calculate their {@link AbstractDeltaFunction} for every chemical
 * entity and every subsection of an updatable.
 *
 * @author cl
 */
public class EntitySpecific implements UpdateSpecificity<EntityDeltaFunction> {

    /**
     * The delta functions to be calculated.
     */
    private List<EntityDeltaFunction> deltaFunctions;

    /**
     * The associated module.
     */
    private ConcentrationBasedModule<EntityDeltaFunction> module;

    /**
     * Initializes the update specificity for the corresponding module.
     */
    public EntitySpecific(ConcentrationBasedModule<EntityDeltaFunction> module) {
        this.module = module;
        deltaFunctions = new ArrayList<>();
    }

    /**
     * Returns a object, managing shared properties of the module.
     * @return The supplier.
     */
    private FieldSupplier supply() {
        return module.getSupplier();
    }

    @Override
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
