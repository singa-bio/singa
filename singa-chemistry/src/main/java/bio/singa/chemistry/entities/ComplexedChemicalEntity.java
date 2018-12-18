package bio.singa.chemistry.entities;

import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.features.model.Evidence;
import bio.singa.structure.features.molarmass.MolarMass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author cl
 */
public class ComplexedChemicalEntity extends ChemicalEntity {

    private static final Logger logger = LoggerFactory.getLogger(ComplexedChemicalEntity.class);

    private static final Evidence computedMassOrigin = new Evidence(Evidence.SourceType.PREDICTION,
            "computed by sum of components",
            "none");

    public static Builder create(String identifier) {
        return new Builder(identifier);
    }

    public static Builder create(SimpleStringIdentifier identifier) {
        return new Builder(identifier);
    }

    private final Map<ChemicalEntity, Integer> associatedParts;

    /**
     * Creates a new Chemical Entity with the given pdbIdentifier.
     *
     * @param identifier The pdbIdentifier.
     */
    protected ComplexedChemicalEntity(SimpleStringIdentifier identifier) {
        super(identifier);
        associatedParts = new HashMap<>();
    }

    public void addAssociatedPart(ChemicalEntity chemicalEntity) {
        associatedParts.computeIfPresent(chemicalEntity, (key, value) -> value + 1);
        associatedParts.putIfAbsent(chemicalEntity, 1);
        if (chemicalEntity.hasFeature(MolarMass.class)) {
            computeMolarMass();
        }
    }

    public void addAssociatedPart(ChemicalEntity chemicalEntity, int stochiometry) {
        associatedParts.put(chemicalEntity, stochiometry);
        if (chemicalEntity.hasFeature(MolarMass.class)) {
            computeMolarMass();
        }
    }

    public Map<ChemicalEntity, Integer> getAssociatedParts() {
        return associatedParts;
    }

    public Set<ChemicalEntity> getAssociatedChemicalEntities() {
        return associatedParts.keySet();
    }

    private void computeMolarMass() {
        double sum = 0.0;
        for (ChemicalEntity entity : associatedParts.keySet()) {
            if (entity.hasFeature(MolarMass.class)) {
                Quantity<MolarMass> featureContent = entity.getFeature(MolarMass.class);
                if (featureContent != null) {
                    double v = featureContent
                            .multiply(associatedParts.get(entity))
                            .getValue().doubleValue();
                    sum += v;
                } else {
                    logger.warn("Could not calculate mass of {}, since not all complexed parts have an associated molar mass.", this);
                    return;
                }
            }
        }
        setFeature(new MolarMass(sum, computedMassOrigin));
    }

    @Override
    public String toString() {
        return "Complex Chemical Entity " + identifier;
    }

    public static class Builder extends ChemicalEntity.Builder<ComplexedChemicalEntity, Builder> {

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
                topLevelObject.addAssociatedPart(chemicalEntity);
            }
            return this;
        }

        public ComplexedChemicalEntity.Builder addAssociatedPart(ChemicalEntity chemicalEntity, int stochiometry) {
            if (chemicalEntity != null) {
                topLevelObject.addAssociatedPart(chemicalEntity, stochiometry);
            }
            return this;
        }

    }


}
