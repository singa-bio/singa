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
public class ReactionTableConverter {

    private ReactionTable table;

    public static ReactionTable convert(Simulation simulation) {
        ReactionTableConverter converter = new ReactionTableConverter(simulation);
        return converter.table;
    }

    private ReactionTableConverter(Simulation simulation) {
        table = new ReactionTable();
        simulation.getModules()
                .forEach(this::convertModule);
    }

    private void convertModule(UpdateModule module) {
        if (module instanceof Reaction) {
            Reaction reaction = (Reaction) module;
            table.addRow(new ReactionTable.ReactionTableRow(module.getIdentifier(),
                    FormatReactionEquation.formatTex(reaction),
                    FormatReactionKinetics.formatTex(reaction),
                    FormatFeature.formatRates(reaction)));
        }
    }


}
