package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.chemistry.entities.ComplexModification;
import bio.singa.mathematics.combinatorics.StreamPermutations;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.*;

import static bio.singa.chemistry.entities.ComplexModification.Operation.SPLIT;
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
    private Map<String, List<ComplexModification>> dynamicProducts;
    private List<ChemicalEntity> previousProducts;

    private Map<Set<ChemicalEntity>, List<ReactantSet>> compositionCache;
    private Map<ChemicalEntity, CellTopology> targetTopologies;
    private boolean splitEntity = false;

    private boolean dynamicComplex;

    public DynamicReactantBehavior() {
        staticSubstrates = new ArrayList<>();
        staticProducts = new ArrayList<>();
        staticCatalysts = new ArrayList<>();
        dynamicSubstrates = new ArrayList<>();
        dynamicProducts = new HashMap<>();
        previousProducts = new ArrayList<>();
        compositionCache = new HashMap<>();
        targetTopologies = new HashMap<>();
        dynamicComplex = false;
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

    public void setDynamicSubstrates(List<DynamicChemicalEntity> dynamicSubstrates) {
        this.dynamicSubstrates = dynamicSubstrates;
    }

    public boolean isDynamicComplex() {
        return dynamicComplex;
    }

    public void setDynamicComplex(boolean dynamicComplex) {
        this.dynamicComplex = dynamicComplex;
    }

    public Map<String, List<ComplexModification>> getDynamicProducts() {
        return dynamicProducts;
    }

    public void addDynamicProduct(String dynamicSubstrate, ComplexModification modification) {
        if (!dynamicProducts.containsKey(dynamicSubstrate)) {
            dynamicProducts.put(dynamicSubstrate, new ArrayList<>());
        }
        dynamicProducts.get(dynamicSubstrate).add(modification);
    }

    public void addDynamicProduct(String dynamicSubstrate, List<ComplexModification> modifications) {
        dynamicProducts.put(dynamicSubstrate, modifications);
    }

    public void addTargetTopology(ChemicalEntity splitTargetEntity, CellTopology targetTopology) {
        this.targetTopologies.put(splitTargetEntity, targetTopology);
    }

    public Map<ChemicalEntity, CellTopology> getTargetTopologies() {
        return targetTopologies;
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
        Set<ChemicalEntity> referencedEntities = updatable.getConcentrationContainer().getReferencedEntities();
        if (compositionCache.containsKey(referencedEntities)) {
            return compositionCache.get(referencedEntities);
        } else {
            List<ReactantSet> sets = new ArrayList<>();
            if (dynamicComplex) {
                // get binder and bindee
                List<Reactant> binders = collectPossibleSubstrates(updatable, dynamicSubstrates.get(0));
                clearPreviousProducts(binders);
                List<Reactant> bindees = collectPossibleSubstrates(updatable, dynamicSubstrates.get(1));
                clearPreviousProducts(bindees);
                // combine them using permutation
                List<List<Reactant>> rawSubstrates = new ArrayList<>();
                rawSubstrates.add(bindees);
                rawSubstrates.add(binders);
                List<List<Reactant>> permutations = StreamPermutations.permutations(rawSubstrates);
                // create corresponding products and resulting reaction sets
                for (List<Reactant> substrates : permutations) {
                    Reactant leftPart = substrates.get(0);
                    Reactant rightPart = substrates.get(1);
                    ComplexEntity complexEntity = ComplexEntity.from(leftPart.getEntity(), rightPart.getEntity());
                    // add to previous products to prevent using any product again as a substrate in the same reaction
                    previousProducts.add(complexEntity);
                    Reactant complexReactant = createTargetedReactant(leftPart, complexEntity);
                    List<Reactant> products = Collections.singletonList(complexReactant);
                    sets.add(new ReactantSet(substrates, products));
                }
            } else {
                List<List<Reactant>> possibleSubstrates = new ArrayList<>();
                List<List<Reactant>> possibleProducts = new ArrayList<>();
                if (updatable instanceof Vesicle) {
                    throw new IllegalArgumentException("not implemented yet");
                } else {
                    // each dynamic substrate can result in a number of reactions
                    splitEntity = false;
                    for (DynamicChemicalEntity dynamicSubstrate : dynamicSubstrates) {
                        List<Reactant> matchingSubstrates = collectPossibleSubstrates(updatable, dynamicSubstrate);
                        // if any substrate cannot be found the reaction cannot happen
                        if (matchingSubstrates.isEmpty()) {
                            compositionCache.put(referencedEntities, Collections.emptyList());
                            return Collections.emptyList();
                        }
                        // prepare corresponding products
                        List<Reactant> resultingProducts = collectPossibleProducts(matchingSubstrates, dynamicSubstrate);
                        if (splitEntity) {
                            // resulting products has to be separated if any splitting took place
                            // this hurts to look at
                            // count substrate
                            int j = 0;
                            for (int i = 0; i < resultingProducts.size(); i = i + 2) {
                                List<Reactant> splitProducts = new ArrayList<>();
                                splitProducts.add(resultingProducts.get(i));
                                splitProducts.add(resultingProducts.get(i + 1));
                                possibleProducts.add(splitProducts);
                                possibleSubstrates.add(Collections.singletonList(matchingSubstrates.get(j)));
                                j++;
                            }
                        } else {
                            possibleSubstrates.add(matchingSubstrates);
                            possibleProducts.add(resultingProducts);
                        }
                    }
                    if (splitEntity) {
                        for (Reactant staticSubstrate : staticSubstrates) {
                            for (List<Reactant> possibleSubstrate : possibleSubstrates) {
                                possibleSubstrate.add(staticSubstrate);
                            }
                        }
                        for (Reactant staticProduct : staticProducts) {
                            for (List<Reactant> possibleProduct : possibleProducts) {
                                possibleProduct.add(staticProduct);
                            }
                        }
                    } else {
                        // generate reaction sets
                        for (Reactant staticSubstrate : staticSubstrates) {
                            possibleSubstrates.add(Collections.singletonList(staticSubstrate));
                        }
                        for (Reactant staticProduct : staticProducts) {
                            possibleProducts.add(Collections.singletonList(staticProduct));
                        }
                    }
                }
                // create all possible combinations of substrate

                if (splitEntity) {
                    for (int i = 0; i < possibleSubstrates.size(); i++) {
                        sets.add(new ReactantSet(possibleSubstrates.get(i), possibleProducts.get(i)));
                    }
                } else {
                    List<List<Reactant>> allSubstrates = StreamPermutations.permutations(possibleSubstrates);
                    // create resulting products
                    List<List<Reactant>> allProducts = StreamPermutations.permutations(possibleProducts);
                    // combine substrates and products to reactant sets
                    for (int i = 0; i < allSubstrates.size(); i++) {
                        sets.add(new ReactantSet(allSubstrates.get(i), allProducts.get(i)));
                    }
                }
            }
            // cache results
            compositionCache.put(referencedEntities, sets);
            return sets;
        }
    }

    private void clearPreviousProducts(List<Reactant> reactants) {
        reactants.removeIf(next -> previousProducts.contains(next.getEntity()));
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
            boolean split = false;
            for (ComplexModification modification : dynamicProducts.get(dynamicSubstrate.getIdentifier().getContent())) {
                if (entity instanceof ComplexEntity) {
                    if (modification.getOperation().equals(SPLIT)) {
                        split = true;
                    } else {
                        entity = ((ComplexEntity) entity).apply(modification);
                    }
                }
            }
            // add to products
            if (split) {
                splitEntity = true;
                ComplexEntity complexEntity = (ComplexEntity) entity;
                // handle left split
                resultingProducts.add(createTargetedReactant(matchingSubstrate, complexEntity.getLeft().getData()));
                resultingProducts.add(createTargetedReactant(matchingSubstrate, complexEntity.getRight().getData()));
            } else {
                resultingProducts.add(new Reactant(entity, PRODUCT, matchingSubstrate.getPreferredTopology()));
            }
        }
        return resultingProducts;
    }

    /**
     * Creates a reactant from an entity the should be split. Mainly determines topology where the split result should
     * be placed.
     *
     * @param originalSubstrate The substrate the split resulted from.
     * @param productEntity The entity that should be converted to a reactant.
     * @return The resulting reactant
     */
    private Reactant createTargetedReactant(Reactant originalSubstrate, ChemicalEntity productEntity) {
        if (productEntity instanceof ComplexEntity) {
            // if complex
            ComplexEntity complex = ((ComplexEntity) productEntity);
            for (ChemicalEntity targetEntity : targetTopologies.keySet()) {
                // check if the complex contains any of the split targets
                if (complex.find(targetEntity) != null) {
                    ChemicalEntity entity = complex.getData();
                    return new Reactant(entity, PRODUCT, targetTopologies.get(targetEntity));
                }
            }
        } else {
            // if regular entity
            for (ChemicalEntity targetEntity : targetTopologies.keySet()) {
                // check inf the entity is defined as a split target
                if (productEntity.equals(targetEntity)) {
                    return new Reactant(productEntity, PRODUCT, targetTopologies.get(targetEntity));
                }
            }
        }
        // if no target was defined us the topology of the substrate
        return new Reactant(productEntity, PRODUCT, originalSubstrate.getPreferredTopology());
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
