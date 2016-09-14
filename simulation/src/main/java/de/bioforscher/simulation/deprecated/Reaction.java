package de.bioforscher.simulation.deprecated;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import tec.units.ri.quantity.Quantities;

import java.util.*;

import static de.bioforscher.units.UnitProvider.MOLE_PER_LITRE;

/**
 * A reaction type that calculates the next concentration. How the concentration
 * is updated needs to be implemented.
 *
 * @author Christoph Leberecht
 * @deprecated
 */
public abstract class Reaction {

    private List<ChemicalEntity> substrates;
    private List<ChemicalEntity> products;
    private Map<ChemicalEntity, Integer> stoichiometricCoefficients;
    private double currentVelocity;

    protected Reaction() {
        this.substrates = new ArrayList<>();
        this.products = new ArrayList<>();
        this.stoichiometricCoefficients = new HashMap<>();
    }

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
        return collectEntitiesAsString(this.substrates) +
                " \u27f6 " +
                collectEntitiesAsString(this.products);
    }

    private String collectEntitiesAsString(List<ChemicalEntity> entities) {
        StringBuilder sb = new StringBuilder();
        Iterator<ChemicalEntity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            ChemicalEntity entity = iterator.next();
            if (this.stoichiometricCoefficients.containsKey(entity) &&
                    this.stoichiometricCoefficients.get(entity) > 1) {
                sb.append(this.stoichiometricCoefficients.get(entity)).append(" ");
            }
            sb.append(entity.getName().substring(0, 1).toUpperCase()).append(entity.getName().substring(1));
            if (iterator.hasNext()) {
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
            this.topLevelObject = createObject();
            this.builderObject = getBuilder();
        }

        public BuilderType setSubstrate(List<ChemicalEntity> substrates) {
            this.topLevelObject.setSubstrates(substrates);
            return this.builderObject;
        }

        public BuilderType addSubstrate(ChemicalEntity substrate) {
            this.topLevelObject.addSubstrate(substrate);
            this.topLevelObject.addStoichiometricCoefficient(substrate, 1);
            return this.builderObject;
        }

        public BuilderType addSubstrate(ChemicalEntity substrate, int stoichiometricCoefficient) {
            this.topLevelObject.addSubstrate(substrate);
            this.topLevelObject.addStoichiometricCoefficient(substrate, stoichiometricCoefficient);
            return this.builderObject;
        }

        public BuilderType setProducts(List<ChemicalEntity> products) {
            this.topLevelObject.setProducts(products);
            return this.builderObject;
        }

        public BuilderType addProduct(ChemicalEntity product) {
            this.topLevelObject.addProduct(product);
            this.topLevelObject.addStoichiometricCoefficient(product, 1);
            return this.builderObject;
        }

        public BuilderType addProduct(ChemicalEntity product, int stoichiometricCoefficient) {
            this.topLevelObject.addProduct(product);
            this.topLevelObject.addStoichiometricCoefficient(product, stoichiometricCoefficient);
            return this.builderObject;
        }

        public BuilderType setStoichiometricCoefficients(Map<ChemicalEntity, Integer> coefficients) {
            this.topLevelObject.setStoichiometricCoefficients(coefficients);
            return this.builderObject;
        }

        public BuilderType addStoichiometricCoefficient(ChemicalEntity entity, int coefficient) {
            this.topLevelObject.addStoichiometricCoefficient(entity, coefficient);
            return this.builderObject;
        }

        public TopLevelType build() {
            return this.topLevelObject;
        }

    }

}
