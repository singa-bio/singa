package bio.singa.simulation.model.modules.concentration.specifity;

import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.FieldSupplier;
import bio.singa.simulation.model.modules.concentration.functions.AbstractDeltaFunction;
import bio.singa.simulation.model.modules.concentration.functions.SectionDeltaFunction;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Section specific {@link ConcentrationBasedModule}s calculate their {@link AbstractDeltaFunction} for every subsection
 * of an updatable.
 *
 * @author cl
 */
public class SectionSpecific implements UpdateSpecificity<SectionDeltaFunction> {

    /**
     * The delta functions to be calculated.
     */
    private List<SectionDeltaFunction> deltaFunctions;

    /**
     * The associated module.
     */
    private ConcentrationBasedModule module;

    /**
     * Initializes the update specificity for the corresponding module.
     * @param module The module.
     */
    public SectionSpecific(ConcentrationBasedModule module) {
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
            determineDeltas(container);
        }
    }

    @Override
    public void determineDeltas(ConcentrationContainer container) {
        // for each designated function
        for (SectionDeltaFunction deltaFunction : deltaFunctions) {
            // test condition
            if (deltaFunction.getCondition().test(container)) {
                // apply function
                List<ConcentrationDelta> deltas = deltaFunction.getFunction().apply(container);
                // for each resulting delta
                for (ConcentrationDelta delta : deltas) {
                    supply().setCurrentEntity(delta.getChemicalEntity());
                    if (module.deltaIsValid(delta)) {
                        module.handleDelta(new ConcentrationDeltaIdentifier(supply().getCurrentUpdatable(), supply().getCurrentSubsection(), supply().getCurrentEntity()), delta);
                    }
                }
            }
        }
    }

    @Override
    public void addDeltaFunction(SectionDeltaFunction sectionDeltaFunction) {
        deltaFunctions.add(sectionDeltaFunction);
    }
}
