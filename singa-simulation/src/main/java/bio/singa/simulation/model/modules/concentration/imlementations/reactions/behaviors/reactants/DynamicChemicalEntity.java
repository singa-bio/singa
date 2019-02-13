package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import bio.singa.chemistry.entities.AbstractChemicalEntity;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.ArrayList;
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
    private List<CellTopology> possibleTopologies;

    /**
     * Creates a new Chemical Entity with the given identifier.
     *
     * @param identifier The pdbIdentifier.
     */
    protected DynamicChemicalEntity(SimpleStringIdentifier identifier) {
        super(identifier);
        composition = new ArrayList<>();
        possibleTopologies = new ArrayList<>();
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

    public List<CellTopology> getPossibleTopologies() {
        return possibleTopologies;
    }

    public void setPossibleTopologies(List<CellTopology> possibleTopologies) {
        this.possibleTopologies = possibleTopologies;
    }

    public void addPossibleTopology(CellTopology possibleTopology) {
        possibleTopologies.add(possibleTopology);
    }

    public List<ChemicalEntity> getMatchingEntities(Updatable updatable, CellTopology topology) {
        Set<ChemicalEntity> entities = updatable.getConcentrationContainer().getPool(topology).getValue().getReferencedEntities();
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

        public Builder addPossibleTopology(CellTopology topology) {
            topLevelObject.addPossibleTopology(topology);
            return this;
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }

}
