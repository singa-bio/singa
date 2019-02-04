package bio.singa.simulation.export.reactiontable;

import java.util.List;

/**
 * @author cl
 */
public class ReactionTable {

    public static class ReactionTableRow {

        private String identifier;
        private String reaction;
        private String kinetics;
        private String parameters;

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getReaction() {
            return reaction;
        }

        public void setReaction(String reaction) {
            this.reaction = reaction;
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

    }

    private List<ReactionTableRow> rows;

    public ReactionTable() {

    }

    public List<ReactionTableRow> getRows() {
        return rows;
    }

    public void setRows(List<ReactionTableRow> rows) {
        this.rows = rows;
    }
}
