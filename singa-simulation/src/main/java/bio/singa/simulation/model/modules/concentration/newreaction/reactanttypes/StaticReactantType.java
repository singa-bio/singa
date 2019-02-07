package bio.singa.simulation.model.modules.concentration.newreaction.reactanttypes;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cl
 */
public class StaticReactantType implements ReactantType {

    private List<Reactant> substrates;
    private List<Reactant> products;
    private List<Reactant> catalysts;

    public StaticReactantType() {
        substrates = new ArrayList<>();
        products = new ArrayList<>();
        catalysts = new ArrayList<>();
    }

    public List<Reactant> getSubstrates() {
        return substrates;
    }

    public void setSubstrates(List<Reactant> substrates) {
        this.substrates = substrates;
    }

    public void addSubstrate(Reactant substrate) {
        substrates.add(substrate);
    }

    public List<Reactant> getProducts() {
        return products;
    }

    public void setProducts(List<Reactant> products) {
        this.products = products;
    }

    public void addProduct(Reactant product) {
        products.add(product);
    }

    public List<Reactant> getCatalysts() {
        return catalysts;
    }

    public void setCatalysts(List<Reactant> catalysts) {
        this.catalysts = catalysts;
    }

    public void addCatalyst(Reactant catalyst) {
        catalysts.add(catalyst);
    }

    @Override
    public List<ReactantSet> generateReactantSets() {
        return Collections.singletonList(new ReactantSet(substrates, products, catalysts));
    }

    @Override
    public List<ChemicalEntity> getReferencedEntities() {
        List<ChemicalEntity> entities = new ArrayList<>();
        substrates.stream().map(Reactant::getEntity).forEach(entities::add);
        products.stream().map(Reactant::getEntity).forEach(entities::add);
        catalysts.stream().map(Reactant::getEntity).forEach(entities::add);
        return entities;
    }
}
