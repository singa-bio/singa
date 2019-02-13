package bio.singa.simulation.export.reactiontable;

import bio.singa.simulation.model.modules.UpdateModule;
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

    }

//    private void addContentOfModule(Reaction reaction) {
//        String identifier = reaction.getIdentifier().replace("_", " ");
//        String equation = FormatReactionEquation.formatTex(reaction);
//        String kinetics = FormatReactionKinetics.formatTex(reaction);
//        ReactionTable.ReactionTableRow reactionTableRow = new ReactionTable.ReactionTableRow(identifier, equation, kinetics, null);
//        table.addRow(reactionTableRow);
//    }

//    private void addContentOfModule(SectionDependentReaction reaction) {
//        String identifier = reaction.getIdentifier().replace("_", " ");
//        String equation = FormatReactionEquation.formatTex(reaction);
//        String kinetics = FormatReactionKinetics.formatTex(reaction);
//        ReactionTable.ReactionTableRow reactionTableRow = new ReactionTable.ReactionTableRow(identifier, equation, kinetics, null);
//        table.addRow(reactionTableRow);
//    }

//    private void addContentOfModule(DynamicReaction reaction) {
//        String identifier = reaction.getIdentifier().replace("_", " ");
//        String equation = FormatReactionEquation.formatTex(reaction);
//        String kinetics = FormatReactionKinetics.formatTex(reaction);
//        ReactionTable.ReactionTableRow reactionTableRow = new ReactionTable.ReactionTableRow(identifier, equation, kinetics, null);
//        table.addRow(reactionTableRow);
//    }

}
