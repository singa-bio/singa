package bio.singa.chemistry.entities;

import bio.singa.features.identifiers.SimpleStringIdentifier;

/**
 * @author cl
 */
public class ModificationSite extends AbstractChemicalEntity {

    private boolean isOccupied;

    public static Builder create(String identifier) {
        return new Builder(identifier);
    }

    public static Builder create(SimpleStringIdentifier identifier) {
        return new Builder(identifier);
    }

    protected ModificationSite(SimpleStringIdentifier identifier) {
        super(identifier);
        EntityRegistry.put(identifier.toString(), this);
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public static class Builder extends AbstractChemicalEntity.Builder<ModificationSite, Builder> {

        public Builder(SimpleStringIdentifier identifier) {
            super(identifier);
        }

        public Builder(String identifier) {
            this(new SimpleStringIdentifier(identifier));
        }

        @Override
        protected ModificationSite createObject(SimpleStringIdentifier primaryIdentifer) {
            return new ModificationSite(primaryIdentifer);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }

    public ModificationSite copy() {
        return new ModificationSite(getIdentifier());
    }


}
