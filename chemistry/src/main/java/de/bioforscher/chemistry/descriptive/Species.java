package de.bioforscher.chemistry.descriptive;

import de.bioforscher.core.identifier.ChEBIIdentifier;

public class Species extends ChemicalEntity<ChEBIIdentifier> {

    private String smilesRepresentation = "No SMILES representation defined.";

    protected Species(ChEBIIdentifier identifier) {
        super(identifier);
    }

    protected Species(String identifier) {
        this(new ChEBIIdentifier(identifier));
    }

    public String getSmilesRepresentation() {
        return this.smilesRepresentation;
    }

    public void setSmilesRepresentation(String smilesRepresentation) {
        this.smilesRepresentation = smilesRepresentation;
    }

    @Override
    public String toString() {
        return "Species: " + getIdentifier() + " " + Character.toUpperCase(getName().charAt(0)) + getName().substring(1)
                + " weight: " + getMolarMass() + " smiles: " + getSmilesRepresentation();
    }

    public static class Builder extends ChemicalEntity.Builder<Species, Builder, ChEBIIdentifier> {

        public Builder(ChEBIIdentifier identifier) {
            super(identifier);
        }

        public Builder(String identifier) {
            this(new ChEBIIdentifier(identifier));
        }

        @Override
        protected Species createObject(ChEBIIdentifier identifier) {
            return new Species(identifier);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

        public Builder smilesRepresentation(String smilesRepresentation) {
            if (smilesRepresentation != null) {
                this.topLevelObject.setSmilesRepresentation(smilesRepresentation);
            }
            return this;
        }

    }
}
