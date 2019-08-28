package bio.singa.chemistry.entities.graphcomplex.reactors;

import bio.singa.chemistry.entities.graphcomplex.GraphComplex;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReactionElement {

    private List<GraphComplex> substrates;
    private List<GraphComplex> products;

    public static ReactionElement createOneToOne(GraphComplex substrate, GraphComplex product) {
        ReactionElement element = new ReactionElement();
        element.substrates.add(substrate);
        element.products.add(product);
        return element;
    }

    public static ReactionElement createOneToTwo(GraphComplex substrate, GraphComplex primaryProduct, GraphComplex secondaryProduct) {
        ReactionElement element = new ReactionElement();
        element.substrates.add(substrate);
        element.products.add(primaryProduct);
        element.products.add(secondaryProduct);
        return element;
    }

    public static ReactionElement createTwoToOne(GraphComplex primarySubstrate, GraphComplex secondarySubstrate, GraphComplex product) {
        ReactionElement element = new ReactionElement();
        element.substrates.add(primarySubstrate);
        element.substrates.add(secondarySubstrate);
        element.products.add(product);
        return element;
    }

    public ReactionElement() {
        substrates = new ArrayList<>();
        products = new ArrayList<>();
    }

    public ReactionElement(List<GraphComplex> substrates, List<GraphComplex> products) {
        this.substrates = substrates;
        this.products = products;
    }

    public List<GraphComplex> getSubstrates() {
        return substrates;
    }

    public void setSubstrates(List<GraphComplex> substrates) {
        this.substrates = substrates;
    }

    public List<GraphComplex> getProducts() {
        return products;
    }

    public void setProducts(List<GraphComplex> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        List<String> substrateStrings = substrates.stream()
                .map(GraphComplex::getIdentifier)
                .collect(Collectors.toList());
        List<String> productStrings = products.stream()
                .map(GraphComplex::getIdentifier)
                .collect(Collectors.toList());
        return String.join(" + ", substrateStrings) + " -> " + String.join(" + ", productStrings);
    }
}
