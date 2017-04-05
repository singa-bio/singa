package de.bioforscher.chemistry.descriptive;

import de.bioforscher.core.identifier.SimpleStringIdentifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Christoph on 04/11/2016.
 */
public class ComplexedChemicalEntity extends ChemicalEntity<SimpleStringIdentifier> {

    private Map<ChemicalEntity, Integer> associatedParts;

    /**
     * Creates a new Chemical Entity with the given pdbIdentifier.
     *
     * @param identifier The pdbIdentifier.
     */
    protected ComplexedChemicalEntity(SimpleStringIdentifier identifier) {
        super(identifier);
        this.associatedParts = new HashMap<>();
    }

    public void addAssociatedPart(ChemicalEntity chemicalEntity) {
        this.associatedParts.computeIfPresent(chemicalEntity, (key, value) -> value + 1);
        this.associatedParts.putIfAbsent(chemicalEntity, 1);
        computeMolarMass();
    }

    public Map<ChemicalEntity, Integer> getAssociatedParts() {
        return this.associatedParts;
    }

    public Set<ChemicalEntity> getAssociatedChemicalEntities() {
        return this.associatedParts.keySet();
    }

    private void computeMolarMass() {
        setMolarMass(this.associatedParts.keySet().stream()
                .mapToDouble(entity -> entity.getMolarMass()
                        .multiply(this.associatedParts.get(entity))
                        .getValue().doubleValue())
                .sum());
    }

    @Override
    public String toString() {
        return "ComplexedChemicalEntity "+super.getIdentifier()+" {" +
                "associatedParts=" + associatedParts +
                '}';
    }

    public static class Builder extends ChemicalEntity.Builder<ComplexedChemicalEntity, Builder, SimpleStringIdentifier> {

        public Builder(SimpleStringIdentifier identifier) {
            super(identifier);
        }

        public Builder(String identifier) {
            this(new SimpleStringIdentifier(identifier));
        }

        @Override
        protected ComplexedChemicalEntity createObject(SimpleStringIdentifier primaryIdentifer) {
            return new ComplexedChemicalEntity(primaryIdentifer);
        }

        @Override
        protected ComplexedChemicalEntity.Builder getBuilder() {
            return this;
        }

        public ComplexedChemicalEntity.Builder addAssociatedPart(ChemicalEntity chemicalEntity) {
            if (chemicalEntity != null) {
                this.topLevelObject.addAssociatedPart(chemicalEntity);
            }
            return this;
        }

    }


}
