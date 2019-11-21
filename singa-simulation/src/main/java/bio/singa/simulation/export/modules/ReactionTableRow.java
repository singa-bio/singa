package bio.singa.simulation.export.modules;

import java.util.List;
import java.util.Map;

import static bio.singa.simulation.export.TeXTableSyntax.*;

/**
 * @author cl
 */
public class ReactionTableRow implements ModuleTableRow {

    private String identifier;
    private List<String> equations;
    private List<String> kinetics;
    private Map<String, List<String>> rates;

    public ReactionTableRow(String identifier, List<String> equations, List<String> kinetics, Map<String, List<String>> rates) {
        this.identifier = identifier;
        this.equations = equations;
        this.kinetics = kinetics;
        this.rates = rates;
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

    public Map<String, List<String>> getRates() {
        return rates;
    }

    public void setRates(Map<String, List<String>> rates) {
        this.rates = rates;
    }

    public String toRow() {
        StringBuilder kineticsBody = new StringBuilder();
        for (int i = 0; i < equations.size(); i++) {
            kineticsBody.append(assembleRowsSet(i));
        }
        return generateHeader(identifier) + kineticsBody.toString() + generateFeatureString(rates) + COLUMN_END_BREAKING;
    }

    public String assembleRowsSet(int identifier) {
        return (ModuleTable.appendCount++) + COLUMN_SEPERATOR_SPACED +
                equations.get(identifier) + COLUMN_END_NON_BREAKING +
                COLUMN_SEPERATOR_SPACED +
                kinetics.get(identifier) + COLUMN_END_NON_BREAKING;
    }

}
