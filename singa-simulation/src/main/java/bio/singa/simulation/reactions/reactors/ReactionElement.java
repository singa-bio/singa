package bio.singa.simulation.reactions.reactors;

import bio.singa.simulation.entities.ComplexEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReactionElement {

    private List<ComplexEntity> substrates;
    private List<ComplexEntity> products;

    public static ReactionElement createOneToOne(ComplexEntity substrate, ComplexEntity product) {
        ReactionElement element = new ReactionElement();
        element.substrates.add(substrate);
        element.products.add(product);
        return element;
    }

    public static ReactionElement createOneToTwo(ComplexEntity substrate, ComplexEntity primaryProduct, ComplexEntity secondaryProduct) {
        ReactionElement element = new ReactionElement();
        element.substrates.add(substrate);
        element.products.add(primaryProduct);
        element.products.add(secondaryProduct);
        return element;
    }

    public static ReactionElement createTwoToOne(ComplexEntity primarySubstrate, ComplexEntity secondarySubstrate, ComplexEntity product) {
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

    public ReactionElement(List<ComplexEntity> substrates, List<ComplexEntity> products) {
        this.substrates = substrates;
        this.products = products;
    }

    public List<ComplexEntity> getSubstrates() {
        return substrates;
    }

    public void setSubstrates(List<ComplexEntity> substrates) {
        this.substrates = substrates;
    }

    public List<ComplexEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ComplexEntity> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        List<String> substrateStrings = substrates.stream()
                .map(ComplexEntity::getIdentifier)
                .collect(Collectors.toList());
        List<String> productStrings = products.stream()
                .map(ComplexEntity::getIdentifier)
                .collect(Collectors.toList());
        return String.join(" + ", substrateStrings) + " -> " + String.join(" + ", productStrings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionElement element = (ReactionElement) o;
        return Objects.equals(substrates, element.substrates) &&
                Objects.equals(products, element.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(substrates, products);
    }
}
