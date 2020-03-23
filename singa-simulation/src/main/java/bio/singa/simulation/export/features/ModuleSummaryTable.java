package bio.singa.simulation.export.features;

import bio.singa.features.model.Feature;
import bio.singa.simulation.export.format.FormatReactionEquation;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static bio.singa.simulation.export.TeXFormat.*;

/**
 * @author cl
 */
public class ModuleSummaryTable {

    private FeatureTable featureTable;
    private String teXString;

    public ModuleSummaryTable(FeatureTable featureTable) {
        this.featureTable = featureTable;
    }

    public static ModuleSummaryTable from(Simulation simulation) {
        FeatureTable featureTable = FeatureTable.fromSimulation(simulation);
        ModuleSummaryTable summaryTable = new ModuleSummaryTable(featureTable);
        summaryTable.generate(simulation);
        return summaryTable;
    }

    private void generate(Simulation simulation) {
        StringBuilder sb = new StringBuilder();
        String formatStrings = "M%0" + (int) (Math.log10(simulation.getModules().size()) + 2) + "d";
        for (int i = 0; i < simulation.getModules().size(); i++) {
            UpdateModule module = simulation.getModules().get(i);
            String identifier = String.format(formatStrings, (i + 1));
            String type = module.getClass().getSimpleName();
            String name = module.getIdentifier();
            List<String> reactionStrings = null;
            if (module instanceof Reaction) {
                reactionStrings = FormatReactionEquation.formatTex((Reaction) module);
            }
            String evidence = FeatureTableRow.formatEvidence(module.getEvidence());
            Map<Feature<?>, String> featureIdentifierMap = featureTable.getFeatureIdentifierMap();
            String features = module.getFeatures().stream()
                    .map(featureIdentifierMap::get)
                    .collect(Collectors.joining(", "));

            sb.append(identifier).append(COLUMN_SEPERATOR_SPACED)
                    .append(type).append(COLUMN_SEPERATOR_SPACED)
                    .append(features).append(COLUMN_SEPERATOR_SPACED)
                    .append(evidence).append(COLUMN_END_NON_BREAKING)
                    .append(COLUMN_SEPERATOR_SPACED)
                    .append(formatTableMultiColumn(setCursive(name), 3));

            if (reactionStrings != null) {
                Iterator<String> reactionIterator = reactionStrings.iterator();
                sb.append(COLUMN_END_NON_BREAKING);
                while (reactionIterator.hasNext()) {
                    String reaction = reactionIterator.next();
                    sb.append(COLUMN_SEPERATOR_SPACED).append(formatTableMultiColumn(reaction, 3));
                    if (reactionIterator.hasNext()) {
                        sb.append(COLUMN_END_NON_BREAKING);
                    }
                }

            }
            sb.append("\\\\ [1ex]\n");

        }
        teXString = sb.toString();
    }

    public FeatureTable getFeatureTable() {
        return featureTable;
    }

    public String getTeXString() {
        return teXString;
    }
}
