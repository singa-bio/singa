package bio.singa.simulation.export.reactiontable;

import bio.singa.simulation.export.format.FormatFeature;
import bio.singa.simulation.model.modules.UpdateModule;

/**
 * @author cl
 */
public class GeneralModuleRow implements ModuleTableRow {

    public static ModuleTableRow from(UpdateModule module) {
        return new GeneralModuleRow(module);
    }

    private UpdateModule module;

    public GeneralModuleRow(UpdateModule module) {
        this.module = module;
    }

    @Override
    public String toRow() {
        String header = generateHeader(module.getIdentifier());
        String kind = (ModuleTable.appendCount++) + " & " + module.getClass().getSimpleName() + nonBreakingColumnEnd;
        String features = generateFeatureString(FormatFeature.formatFeatures(module));
        return header + kind + features + breakingColumnEnd;
    }

}
