package de.bioforscher.simulation.reactions;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.model.ImmediateUpdate;
import tec.units.ri.quantity.Quantities;

import java.util.*;

import static de.bioforscher.units.UnitDictionary.MOLE_PER_LITRE;

/**
 * A reaction type that calculates the next concentration. How the concentration
 * is updated needs to be implemented.
 *
 * @author Christoph Leberecht
 */
public abstract class Reaction implements ImmediateUpdate {

    private List<ChemicalEntity> substrates;
    private List<ChemicalEntity> products;
    private Map<ChemicalEntity, Integer> stoichiometricCoefficients;
    private double currentVelocity;

    protected Reaction() {
        this.substrates = new ArrayList<>();
        this.products = new ArrayList<>();
        this.stoichiometricCoefficients = new HashMap<>();
    }

    @Override
    public abstract void updateConcentrations(BioNode node);

    public List<ChemicalEntity> getSubstrates() {
        return this.substrates;
    }

    public void setSubstrates(List<ChemicalEntity> substrates) {
        this.substrates = substrates;
    }

    public void addSubstrate(ChemicalEntity entity) {
        this.substrates.add(entity);
    }

    public List<ChemicalEntity> getProducts() {
        return this.products;
    }

    public void setProducts(List<ChemicalEntity> products) {
        this.products = products;
    }

    public void addProduct(ChemicalEntity entity) {
        this.products.add(entity);
    }

    public Map<ChemicalEntity, Integer> getStoichiometricCoefficients() {
        return this.stoichiometricCoefficients;
    }

    public int getStoichiometricCoefficient(ChemicalEntity entity) {
        return this.stoichiometricCoefficients.get(entity);
    }

    public void setStoichiometricCoefficients(Map<ChemicalEntity, Integer> stoichiometricCoefficients) {
        this.stoichiometricCoefficients = stoichiometricCoefficients;
    }

    public void addStoichiometricCoefficient(ChemicalEntity entity, int coefficient) {
        this.stoichiometricCoefficients.put(entity, coefficient);
    }

    public double getCurrentVelocity() {
        return this.currentVelocity;
    }

    public void setCurrentVelocity(double currentVelocity) {
        this.currentVelocity = currentVelocity;
    }

    protected void limitReactionRate(BioNode node) {
        double maxVelocity = this.currentVelocity;
        // TODO: this may need some work TEST ME!!!
        if (this.currentVelocity > 0) {
            for (ChemicalEntity substrate : this.substrates) {
                if (this.currentVelocity * this.stoichiometricCoefficients.get(substrate) > node
                        .getConcentration(substrate).getValue().doubleValue()) {
                    maxVelocity = node.getConcentration(substrate).getValue().doubleValue();
                }
            }
        } else {
            for (ChemicalEntity product : this.products) {
                if (this.currentVelocity * this.stoichiometricCoefficients.get(product) < -node
                        .getConcentration(product).getValue().doubleValue()) {
                    maxVelocity = -node.getConcentration(product).getValue().doubleValue();
                }
            }
        }

        this.currentVelocity = maxVelocity;
    }

    protected void decreaseSubstrates(BioNode node) {
        consumeSpecies(node, this.substrates);
    }

    protected void decreaseProducts(double velocity, BioNode node) {
        consumeSpecies(node, this.products);
    }

    private void consumeSpecies(BioNode node, List<ChemicalEntity> entities) {
        for (ChemicalEntity entity : entities) {
            node.setConcentration(entity, Quantities.getQuantity(node.getConcentration(entity).getValue().doubleValue()
                    - this.currentVelocity * this.stoichiometricCoefficients.get(entity), MOLE_PER_LITRE));
        }
    }

    protected void increaseSubstrates(BioNode node) {
        createEntity(node, this.substrates);
    }

    protected void increaseProducts(BioNode node) {
        createEntity(node, this.products);
    }

    private void createEntity(BioNode node, List<ChemicalEntity> entities) {
        for (ChemicalEntity entity : entities) {
            node.setConcentration(entity, Quantities.getQuantity(node.getConcentration(entity).getValue().doubleValue()
                    + this.currentVelocity * this.stoichiometricCoefficients.get(entity), MOLE_PER_LITRE));
        }
    }

    public String getReactionString() {
        // #fancy
        StringBuilder sb = new StringBuilder();
        Iterator<ChemicalEntity> sIterator = this.substrates.iterator();
        while (sIterator.hasNext()) {
            ChemicalEntity substrate = sIterator.next();
            sb.append(substrate.getName().substring(0, 1).toUpperCase()).append(substrate.getName().substring(1));
            if (sIterator.hasNext()) {
                sb.append(" + ");
            }
        }
        sb.append(" -> ");
        Iterator<ChemicalEntity> pIterator = this.products.iterator();
        while (pIterator.hasNext()) {
            ChemicalEntity product = pIterator.next();
            sb.append(product.getName().substring(0, 1).toUpperCase()).append(product.getName().substring(1));
            if (pIterator.hasNext()) {
                sb.append(" + ");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Reaction [ " +
                this.getReactionString() +
                " ]";
    }

    public static abstract class Builder<TopLevelType extends Reaction, BuilderType extends Builder> {

        protected TopLevelType topLevelObject;
        protected BuilderType builderObject;

        protected abstract TopLevelType createObject();

        protected abstract BuilderType getBuilder();

        public Builder() {
            topLevelObject = createObject();
            builderObject = getBuilder();
        }

        public BuilderType setSubstrate(List<ChemicalEntity> substrates) {
            topLevelObject.setSubstrates(substrates);
            return builderObject;
        }

        public BuilderType addSubstrate(ChemicalEntity substrate) {
            topLevelObject.addSubstrate(substrate);
            return builderObject;
        }

        public BuilderType setProducts(List<ChemicalEntity> products) {
            topLevelObject.setProducts(products);
            return builderObject;
        }

        public BuilderType addProduct(ChemicalEntity product) {
            topLevelObject.addProduct(product);
            return builderObject;
        }

        public BuilderType setStoichiometricCoefficients(Map<ChemicalEntity, Integer> coefficients) {
            topLevelObject.setStoichiometricCoefficients(coefficients);
            return builderObject;
        }

        public BuilderType addStoichiometricCoefficient(ChemicalEntity entity, int coefficient) {
            topLevelObject.addStoichiometricCoefficient(entity, coefficient);
            return builderObject;
        }

        public TopLevelType build() {
            return this.topLevelObject;
        }

    }

}
