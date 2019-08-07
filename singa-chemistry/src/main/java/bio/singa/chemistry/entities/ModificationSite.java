package bio.singa.chemistry.entities;

/**
 * @author cl
 */
public class ModificationSite extends AbstractChemicalEntity {

    private boolean isOccupied;

    public static Builder create(String identifier) {
        return new Builder(identifier);
    }

    protected ModificationSite(String identifier) {
        super(identifier);
        EntityRegistry.put(identifier, this);
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public static class Builder extends AbstractChemicalEntity.Builder<ModificationSite, Builder> {

        public Builder(String identifier) {
            super(identifier);
        }

        @Override
        protected ModificationSite createObject(String primaryIdentifer) {
            return new ModificationSite(primaryIdentifer);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }

    @Override
    public String toString() {
        return "ModificationSite "+identifier;
    }

    public ModificationSite copy() {
        return new ModificationSite(getIdentifier());
    }


}
