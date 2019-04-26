package bio.singa.chemistry.entities;

import bio.singa.features.identifiers.SimpleStringIdentifier;

/**
 * @author cl
 */
public class ModificationSite extends AbstractChemicalEntity {

    public static Builder create(String identifier) {
        return new Builder(identifier);
    }

    public static Builder create(SimpleStringIdentifier identifier) {
        return new Builder(identifier);
    }

    protected ModificationSite(SimpleStringIdentifier identifier) {
        super(identifier);
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


}
