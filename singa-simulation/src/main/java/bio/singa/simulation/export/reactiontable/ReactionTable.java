package bio.singa.simulation.export.reactiontable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ReactionTable {

    public static class ReactionTableRow {

        private String identifier;
        private String equation;
        private String kinetics;
        private String parameters;

        public ReactionTableRow(String identifier, String equation, String kinetics, String parameters) {
            this.identifier = identifier;
            this.equation = equation;
            this.kinetics = kinetics;
            this.parameters = parameters;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getEquation() {
            return equation;
        }

        public void setEquation(String equation) {
            this.equation = equation;
        }

        public String getKinetics() {
            return kinetics;
        }

        public void setKinetics(String kinetics) {
            this.kinetics = kinetics;
        }

        public String getParameters() {
            return parameters;
        }

        public void setParameters(String parameters) {
            this.parameters = parameters;
        }

        public String toTexTableRow() {
            return identifier+" & "+ equation + " & " + kinetics + "\\\\";
        }

    }

    private List<ReactionTableRow> rows;

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
                .collect(Collectors.joining(System.lineSeparator()));
    }

}
