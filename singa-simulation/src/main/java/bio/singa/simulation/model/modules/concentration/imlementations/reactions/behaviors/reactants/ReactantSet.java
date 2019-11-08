package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ReactantSet {

    private final List<Reactant> substrates;
    private final List<Reactant> products;
    private final List<Reactant> catalysts;

    public ReactantSet(List<Reactant> substrates, List<Reactant> products, List<Reactant> catalysts) {
        this.substrates = substrates;
        this.products = products;
        this.catalysts = catalysts;
    }

    public ReactantSet(List<Reactant> substrates, List<Reactant> products) {
        this.substrates = substrates;
        this.products = products;
        catalysts = Collections.emptyList();
    }

    public List<Reactant> getSubstrates() {
        return substrates;
    }

    public List<Reactant> getProducts() {
        return products;
    }

    public List<Reactant> getCatalysts() {
        return catalysts;
    }

    @Override
    public String toString() {
        List<String> substrateStrings = substrates.stream()
                .map(reactant -> reactant.getEntity().getIdentifier())
                .collect(Collectors.toList());
        List<String> productStrings = products.stream()
                .map(reactant -> reactant.getEntity().getIdentifier())
                .collect(Collectors.toList());
        return String.join(" + ", substrateStrings) + " -> " + String.join(" + ", productStrings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactantSet that = (ReactantSet) o;
        return Objects.equals(substrates, that.substrates) &&
                Objects.equals(catalysts, that.catalysts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(substrates, catalysts);
    }

}
