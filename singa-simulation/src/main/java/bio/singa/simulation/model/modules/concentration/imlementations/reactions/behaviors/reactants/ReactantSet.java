package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import java.util.Collections;
import java.util.List;

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

}
