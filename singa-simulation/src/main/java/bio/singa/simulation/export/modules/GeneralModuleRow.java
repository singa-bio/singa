package bio.singa.simulation.export.modules;

import bio.singa.simulation.export.format.FormatFeature;
import bio.singa.simulation.model.modules.UpdateModule;

import static bio.singa.simulation.export.TeXTableSyntax.*;

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
        String kind = (ModuleTable.appendCount++) + COLUMN_SEPERATOR_SPACED + module.getClass().getSimpleName() + COLUMN_END_NON_BREAKING;
        String features = generateFeatureString(FormatFeature.formatFeatures(module));
        return header + kind + features + COLUMN_END_BREAKING;
    }

}
