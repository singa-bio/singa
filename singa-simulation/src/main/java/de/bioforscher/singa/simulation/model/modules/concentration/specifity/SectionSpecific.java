package de.bioforscher.singa.simulation.model.modules.concentration.specifity;

import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import de.bioforscher.singa.simulation.model.modules.concentration.FieldSupplier;
import de.bioforscher.singa.simulation.model.modules.concentration.functions.SectionDeltaFunction;
import de.bioforscher.singa.simulation.model.sections.CellSubsection;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class SectionSpecific implements UpdateSpecificity<SectionDeltaFunction> {

    private List<SectionDeltaFunction> deltaFunctions;
    private ConcentrationBasedModule module;

    public SectionSpecific(ConcentrationBasedModule module) {
        this.module = module;
        deltaFunctions = new ArrayList<>();
    }

    private FieldSupplier supply() {
        return module.getSupplier();
    }

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
