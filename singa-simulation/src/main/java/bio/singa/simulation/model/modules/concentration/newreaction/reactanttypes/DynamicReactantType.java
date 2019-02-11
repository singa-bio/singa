package bio.singa.simulation.model.modules.concentration.newreaction.reactanttypes;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.mathematics.combinatorics.StreamPermutations;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.modules.concentration.reactants.DynamicChemicalEntity;

import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.*;

/**
 * @author cl
 */
public class DynamicReactantType implements ReactantType {

    private List<Reactant> staticSubstrates;
    private List<Reactant> staticProducts;
    private List<Reactant> staticCatalysts;

    private List<DynamicChemicalEntity> dynamicSubstrates;
    private List<DynamicChemicalEntity> dynamicProducts;
    private List<DynamicChemicalEntity> dynamicCatalysts;

    public DynamicReactantType() {
        staticSubstrates = new ArrayList<>();
        staticProducts = new ArrayList<>();
        staticCatalysts = new ArrayList<>();
        dynamicSubstrates = new ArrayList<>();
        dynamicProducts = new ArrayList<>();
        dynamicCatalysts = new ArrayList<>();
    }

    public List<Reactant> getStaticSubstrates() {
        return staticSubstrates;
    }

    public void setStaticSubstrates(List<Reactant> staticSubstrates) {
        this.staticSubstrates = staticSubstrates;
    }

    public void addStaticSubstrate(Reactant substrate) {
        staticSubstrates.add(substrate);
    }

    public List<Reactant> getStaticProducts() {
        return staticProducts;
    }

    public void setStaticProducts(List<Reactant> staticProducts) {
        this.staticProducts = staticProducts;
    }

    public void addStaticProduct(Reactant product) {
        staticProducts.add(product);
    }

    public List<Reactant> getStaticCatalysts() {
        return staticCatalysts;
    }

    public void setStaticCatalysts(List<Reactant> staticCatalysts) {
        this.staticCatalysts = staticCatalysts;
    }

    public void addStaticCatalyst(Reactant catalyst) {
        staticCatalysts.add(catalyst);
    }

    public List<DynamicChemicalEntity> getDynamicSubstrates() {
        return dynamicSubstrates;
    }

    public void setDynamicSubstrates(List<DynamicChemicalEntity> dynamicSubstrates) {
        this.dynamicSubstrates = dynamicSubstrates;
    }

    public void addDynamicSubstrate(DynamicChemicalEntity dynamicSubstrate) {
        dynamicSubstrates.add(dynamicSubstrate);
    }

    public List<DynamicChemicalEntity> getDynamicProducts() {
        return dynamicProducts;
    }

    public void setDynamicProducts(List<DynamicChemicalEntity> dynamicProducts) {
        this.dynamicProducts = dynamicProducts;
    }

    public void addDynamicProduct(DynamicChemicalEntity dynamicProduct) {
        dynamicProducts.add(dynamicProduct);
    }

    public List<DynamicChemicalEntity> getDynamicCatalysts() {
        return dynamicCatalysts;
    }

    public void setDynamicCatalysts(List<DynamicChemicalEntity> dynamicCatalysts) {
        this.dynamicCatalysts = dynamicCatalysts;
    }

    public void addDynamicCatalyst(DynamicChemicalEntity dynamicCatalyst) {
        dynamicCatalysts.add(dynamicCatalyst);
    }

    @Override
    public List<ReactantSet> generateReactantSets(Updatable updatable) {
        List<List<ChemicalEntity>> possibleSubstrates = new ArrayList<>();
        if (updatable instanceof Vesicle) {
            throw new IllegalArgumentException("not implemented yet");
        } else {
            // collect possible substrates
            for (DynamicChemicalEntity dynamicSubstrate : dynamicSubstrates) {
                possibleSubstrates.add(dynamicSubstrate.getMatchingEntities(updatable));
            }
            for (Reactant staticSubstrate : staticSubstrates) {
                possibleSubstrates.add(Collections.singletonList(staticSubstrate.getEntity()));
            }
        }
        // create possible combinations
        List<List<ChemicalEntity>> substratePermutation = StreamPermutations.permutations(possibleSubstrates);
        return null;
    }

    @Override
    public List<ChemicalEntity> getReferencedEntities() {
        List<ChemicalEntity> entities = new ArrayList<>();
        staticSubstrates.stream()
                .map(Reactant::getEntity)
                .forEach(entities::add);
        staticProducts.stream()
                .map(Reactant::getEntity)
                .forEach(entities::add);
        staticCatalysts.stream()
                .map(Reactant::getEntity)
                .forEach(entities::add);
        return entities;
    }

}
