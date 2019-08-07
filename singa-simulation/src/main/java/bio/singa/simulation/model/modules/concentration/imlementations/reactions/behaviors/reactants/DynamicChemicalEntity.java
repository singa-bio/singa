package bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants;

import bio.singa.chemistry.entities.AbstractChemicalEntity;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.ArrayList;
import java.util.Collections;
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
    public DynamicChemicalEntity(String identifier) {
        super(identifier);
        composition = new ArrayList<>();
        possibleTopologies = new ArrayList<>();
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
        if (updatable.getConcentrationContainer().getPool(topology) != null) {
            Set<ChemicalEntity> entities = updatable.getConcentrationContainer().getPool(topology).getValue().getReferencedEntities();
            return EntityReducer.apply(entities, composition);
        } else {
            return Collections.emptyList();
        }
    }


    public static class Builder extends AbstractChemicalEntity.Builder<DynamicChemicalEntity, Builder> {

        public Builder(String identifier) {
            super(identifier);
        }

        @Override
        protected DynamicChemicalEntity createObject(String primaryIdentifer) {
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
