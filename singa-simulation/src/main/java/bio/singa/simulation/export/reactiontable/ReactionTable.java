package bio.singa.simulation.export.reactiontable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ReactionTable {

    public static class ReactionTableRow {

        private static int reactionCount = 1;

        private String identifier;
        private List<String> equations;
        private List<String> kinetics;
        private List<String> parameters;

        private static String fullSpace = "\\addlinespace[1em]\n";
        private static String nonBreakingColumnEnd = "\\\\*\n";
        private static String breakingColumnEnd = "\\\\\n";

        public ReactionTableRow(String identifier, List<String> equations, List<String> kinetics, List<String> parameters) {
            this.identifier = identifier;
            this.equations = equations;
            this.kinetics = kinetics;
            this.parameters = parameters;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public List<String> getEquations() {
            return equations;
        }

        public void setEquations(List<String> equations) {
            this.equations = equations;
        }

        public List<String> getKinetics() {
            return kinetics;
        }

        public void setKinetics(List<String> kinetics) {
            this.kinetics = kinetics;
        }

        public List<String> getParameters() {
            return parameters;
        }

        public void setParameters(List<String> parameters) {
            this.parameters = parameters;
        }

        private String getParameterString() {
            if (parameters.size() == 1) {
                return "rates & " + parameters.get(0) + " & ";
            }
            if (parameters.size() == 2) {
                return "rates & " + parameters.get(0) + " & " + parameters.get(1);
            }
            return "rates & implement me & ";
        }

        private static String multiCols(String content, int number) {
            return "\\multicolumn{" + number + "}{l}{" + content + "}";
        }

        public String toTexTableRow() {
            String identiferHeader = multiCols("\\textbf{" + identifier + "}", 3) + nonBreakingColumnEnd;
            StringBuilder kineticsBody = new StringBuilder();
            for (int i = 0; i < equations.size(); i++) {
                kineticsBody.append(assambleRowsSet(i));
            }
            return identiferHeader + kineticsBody.toString() + getParameterString() + breakingColumnEnd;
        }

        public String assambleRowsSet(int identifier) {
            return (reactionCount++) + " & " +multiCols(equations.get(identifier), 2) + nonBreakingColumnEnd + "& "+multiCols(kinetics.get(identifier), 2) + nonBreakingColumnEnd;
        }

    }

    private List<ReactionTableRow> rows;
    private int appendCount = 1;

    public ReactionTable() {
        rows = new ArrayList<>();
    }

    public List<ReactionTableRow> getRows() {
        return rows;
    }

    public void setRows(List<ReactionTableRow> rows) {
        this.rows = rows;
    }

    public void addRow(ReactionTableRow row) {
        rows.add(row);
    }

    public String toTex() {
        return rows.stream()
                .map(ReactionTableRow::toTexTableRow)
                .collect(Collectors.joining(ReactionTableRow.fullSpace));
    }

}
