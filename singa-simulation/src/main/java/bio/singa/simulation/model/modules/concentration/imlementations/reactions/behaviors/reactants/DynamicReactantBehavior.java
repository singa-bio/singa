package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.ComplexModification;
import bio.singa.mathematics.combinatorics.StreamPermutations;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.*;

import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.PRODUCT;
import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.SUBSTRATE;

/**
 * @author cl
 */
public class DynamicReactantBehavior implements ReactantBehavior {

    private List<Reactant> staticSubstrates;
    private List<Reactant> staticProducts;
    private List<Reactant> staticCatalysts;

    private List<DynamicChemicalEntity> dynamicSubstrates;
    private Map<DynamicChemicalEntity, List<ComplexModification>> dynamicProducts;

    public DynamicReactantBehavior() {
        staticSubstrates = new ArrayList<>();
        staticProducts = new ArrayList<>();
        staticCatalysts = new ArrayList<>();
        dynamicSubstrates = new ArrayList<>();
        dynamicProducts = new HashMap<>();
    }

    @Override
    public List<Reactant> getSubstrates() {
        return staticSubstrates;
    }

    public void setStaticSubstrates(List<Reactant> staticSubstrates) {
        this.staticSubstrates = staticSubstrates;
    }

    public void addStaticSubstrate(Reactant substrate) {
        staticSubstrates.add(substrate);
    }

    @Override
    public List<Reactant> getProducts() {
        return staticProducts;
    }

    public void setStaticProducts(List<Reactant> staticProducts) {
        this.staticProducts = staticProducts;
    }

    public void addStaticProduct(Reactant product) {
        staticProducts.add(product);
    }

    @Override
    public List<Reactant> getCatalysts() {
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

    public void addDynamicSubstrate(DynamicChemicalEntity dynamicSubstrate) {
        dynamicSubstrates.add(dynamicSubstrate);
    }

    public void addDynamicProduct(DynamicChemicalEntity dynamicSubstrate, ComplexModification modification) {
        if (!dynamicProducts.containsKey(dynamicSubstrate)) {
            dynamicProducts.put(dynamicSubstrate, new ArrayList<>());
        }
        dynamicProducts.get(dynamicSubstrate).add(modification);
    }

    @Override
    public void addReactant(Reactant reactant) {
        switch (reactant.getRole()) {
            case SUBSTRATE:
                staticSubstrates.add(reactant);
                break;
            case PRODUCT:
                staticProducts.add(reactant);
                break;
            case CATALYTIC:
                staticCatalysts.add(reactant);
                break;
        }
    }

    @Override
    public List<ReactantSet> generateReactantSets(Updatable updatable) {
        List<List<Reactant>> possibleSubstrates = new ArrayList<>();
        List<List<Reactant>> possibleProducts = new ArrayList<>();
        if (updatable instanceof Vesicle) {
            throw new IllegalArgumentException("not implemented yet");
        } else {
            // each dynamic substrate can result in a number of reactions
            for (DynamicChemicalEntity dynamicSubstrate : dynamicSubstrates) {
                List<Reactant> matchingSubstrates = collectPossibleSubstrates(updatable, dynamicSubstrate);
                // if any substrate cannot be found the reaction cannot happen
                if (matchingSubstrates.isEmpty()) {
                    return Collections.emptyList();
                }
                possibleSubstrates.add(matchingSubstrates);
                // prepare corresponding products
                List<Reactant> resultingProducts = collectPossibleProducts(matchingSubstrates, dynamicSubstrate);
                possibleProducts.add(resultingProducts);
            }
            // generate reaction sets
            for (Reactant staticSubstrate : staticSubstrates) {
                possibleSubstrates.add(Collections.singletonList(staticSubstrate));
            }
            for (Reactant staticProduct : staticProducts) {
                possibleProducts.add(Collections.singletonList(staticProduct));
            }
        }
        // create all possible combinations of substrate
        List<List<Reactant>> allSubstrates = StreamPermutations.permutations(possibleSubstrates);
        // create resulting products
        List<List<Reactant>> allProducts = StreamPermutations.permutations(possibleProducts);
        // combine substrates and products to reactant sets
        List<ReactantSet> sets = new ArrayList<>();
        for (int i = 0; i < allSubstrates.size(); i++) {
            sets.add(new ReactantSet(allSubstrates.get(i), allProducts.get(i)));
        }
        return sets;
    }

    private List<Reactant> collectPossibleSubstrates(Updatable updatable, DynamicChemicalEntity dynamicSubstrate) {
        List<Reactant> possibleSubstrates = new ArrayList<>();
        // check each allowed topology
        for (CellTopology possibleTopology : dynamicSubstrate.getPossibleTopologies()) {
            // and filter by composition of the entity
            List<ChemicalEntity> substrateEntities = dynamicSubstrate.getMatchingEntities(updatable, possibleTopology);
            // add remaining entities as possible substrates
            for (ChemicalEntity substrateEntity : substrateEntities) {
                possibleSubstrates.add(new Reactant(substrateEntity, SUBSTRATE, possibleTopology));
            }
        }
        return possibleSubstrates;
    }

    private List<Reactant> collectPossibleProducts(List<Reactant> matchingSubstrates, DynamicChemicalEntity dynamicSubstrate) {
        List<Reactant> resultingProducts = new ArrayList<>();
        // for each substrate
        for (Reactant matchingSubstrate : matchingSubstrates) {
            ChemicalEntity entity = matchingSubstrate.getEntity();
            // apply all modifications
            for (ComplexModification modification : dynamicProducts.get(dynamicSubstrate)) {
                if (entity instanceof ComplexEntity) {
                    entity = ((ComplexEntity) entity).apply(modification);
                }
            }
            // add to products
            resultingProducts.add(new Reactant(entity, PRODUCT, matchingSubstrate.getPreferredTopology()));
        }
        return resultingProducts;
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
