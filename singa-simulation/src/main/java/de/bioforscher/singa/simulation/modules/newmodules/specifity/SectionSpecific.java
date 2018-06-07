package de.bioforscher.singa.simulation.modules.newmodules.specifity;

import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.DeltaIdentifier;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;
import de.bioforscher.singa.simulation.modules.newmodules.functions.SectionDeltaFunction;
import de.bioforscher.singa.simulation.modules.newmodules.module.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.modules.newmodules.module.FieldSupplier;

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
                List<Delta> deltas = deltaFunction.getFunction().apply(container);
                // for each resulting delta
                for (Delta delta : deltas) {
                    supply().setCurrentEntity(delta.getChemicalEntity());
                    if (module.deltaIsValid(delta)) {
                        module.handleDelta(new DeltaIdentifier(supply().getCurrentUpdatable(), supply().getCurrentSubsection(), supply().getCurrentEntity()), delta);
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
