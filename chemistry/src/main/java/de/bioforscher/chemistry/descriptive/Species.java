package de.bioforscher.chemistry.descriptive;

import de.bioforscher.core.annotations.Annotatable;
import de.bioforscher.core.annotations.Annotation;
import de.bioforscher.core.identifier.ChEBIIdentifier;

import java.util.Map;

public class Species extends ChemicalEntity<ChEBIIdentifier> implements Annotatable {

    private Map<Integer, Annotation> annotations;
    private String smilesRepresentation = "No SMILES representation defined.";

    protected Species(ChEBIIdentifier identifier) {
        super(identifier);
    }

    protected Species(String identifier) {
        this(new ChEBIIdentifier(identifier));
    }

    public String getSmilesRepresentation() {
        return smilesRepresentation;
    }

    public void setSmilesRepresentation(String smilesRepresentation) {
        this.smilesRepresentation = smilesRepresentation;
    }

    @Override
    public String toString() {
        return getIdentifier() + " " + Character.toUpperCase(getName().charAt(0)) + getName().substring(1) + " weight: " + getMolarMass() + " smiles: " + getSmilesRepresentation();
    }

    @Override
    public Map<Integer, Annotation> getAnnotations() {
        return this.annotations;
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

        public Builder addAnnotation(int identifier, Annotation annotation) {
            this.topLevelObject.addAnnotation(identifier, annotation);
            return this;
        }

    }
}
