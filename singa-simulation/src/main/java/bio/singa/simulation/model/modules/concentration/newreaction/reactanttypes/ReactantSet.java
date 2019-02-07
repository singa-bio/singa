package bio.singa.simulation.model.modules.concentration.newreaction.reactanttypes;

import bio.singa.simulation.model.modules.concentration.reactants.Reactant;

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
