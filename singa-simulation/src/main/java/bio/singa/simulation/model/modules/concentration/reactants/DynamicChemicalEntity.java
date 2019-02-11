package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.chemistry.entities.AbstractChemicalEntity;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.List;
import java.util.Set;

/**
 * @author cl
 */
public class DynamicChemicalEntity extends AbstractChemicalEntity {

    public static Builder create(String identifier) {
        return new Builder(identifier);
    }

    private List<EntityCompositionCondition> composition;

    /**
     * Creates a new Chemical Entity with the given identifier.
     *
     * @param identifier The pdbIdentifier.
     */
    protected DynamicChemicalEntity(SimpleStringIdentifier identifier) {
        super(identifier);
    }

    public DynamicChemicalEntity(String identifier) {
        this(new SimpleStringIdentifier(identifier));
    }

    public List<EntityCompositionCondition> getComposition() {
        return composition;
    }

    public void setComposition(List<EntityCompositionCondition> composition) {
        this.composition = composition;
    }

    public void addCompositionCondition(EntityCompositionCondition compositionCondition) {
        composition.add(compositionCondition);
    }

    public List<ChemicalEntity> getMatchingEntities(Updatable updatable) {
        Set<ChemicalEntity> entities = updatable.getConcentrationContainer().getReferencedEntities();
        return EntityReducer.apply(entities, composition);
    }

    public static class Builder extends AbstractChemicalEntity.Builder<DynamicChemicalEntity, Builder> {

        public Builder(SimpleStringIdentifier identifier) {
            super(identifier);
        }

        public Builder(String identifier) {
            this(new SimpleStringIdentifier(identifier));
        }

        @Override
        protected DynamicChemicalEntity createObject(SimpleStringIdentifier primaryIdentifer) {
            return new DynamicChemicalEntity(primaryIdentifer);
        }

        public Builder addCompositionCondition(EntityCompositionCondition compositionCondition) {
            topLevelObject.addCompositionCondition(compositionCondition);
            return this;
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }

}
