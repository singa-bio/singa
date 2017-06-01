package de.bioforscher.singa.chemistry.descriptive.entities;

import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.core.identifier.SimpleStringIdentifier;
import de.bioforscher.singa.units.features.model.FeatureOrigin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author cl
 */
public class ComplexedChemicalEntity extends ChemicalEntity<SimpleStringIdentifier> {

    private static final FeatureOrigin computedMassOrigin = new FeatureOrigin(FeatureOrigin.OriginType.PREDICTION,
            "Computed by the Sum of parts",
            "none");

    private Map<ChemicalEntity<?>, Integer> associatedParts;

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

    public Map<ChemicalEntity<?>, Integer> getAssociatedParts() {
        return this.associatedParts;
    }

    public Set<ChemicalEntity<?>> getAssociatedChemicalEntities() {
        return this.associatedParts.keySet();
    }

    private void computeMolarMass() {
        double sum = this.associatedParts.keySet().stream()
                .mapToDouble(entity -> entity.getFeature(MolarMass.class).getFeatureContent()
                        .multiply(this.associatedParts.get(entity))
                        .getValue().doubleValue())
                .sum();
        setFeature(new MolarMass(sum, computedMassOrigin));
    }

    @Override
    public String toString() {
        return "ComplexedChemicalEntity "+super.getIdentifier()+" {" +
                "associatedParts=" + this.associatedParts +
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
