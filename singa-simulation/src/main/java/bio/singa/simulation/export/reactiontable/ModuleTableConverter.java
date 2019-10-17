package bio.singa.simulation.export.reactiontable;

import bio.singa.simulation.export.format.FormatFeature;
import bio.singa.simulation.export.format.FormatReactionEquation;
import bio.singa.simulation.export.format.FormatReactionKinetics;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.simulation.Simulation;

/**
 * @author cl
 */
public class ModuleTableConverter {

    private ModuleTable table;

    public static ModuleTable convert(Simulation simulation) {
        ModuleTableConverter converter = new ModuleTableConverter(simulation);
        return converter.table;
    }

    private ModuleTableConverter(Simulation simulation) {
        table = new ModuleTable();
        simulation.getModules()
                .forEach(this::convertModule);
    }

    private void convertModule(UpdateModule module) {
        if (module instanceof Reaction) {
            Reaction reaction = (Reaction) module;
            table.addRow(new ReactionTableRow(module.getIdentifier(),
                    FormatReactionEquation.formatTex(reaction),
                    FormatReactionKinetics.formatTex(reaction),
                    FormatFeature.formatRates(reaction)));
        } else {
            table.addRow(GeneralModuleRow.from(module));
        }
    }


}
