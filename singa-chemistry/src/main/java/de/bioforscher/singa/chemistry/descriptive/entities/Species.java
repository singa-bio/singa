package de.bioforscher.singa.chemistry.descriptive.entities;

import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.chemistry.descriptive.features.molarmass.MolarMass;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.identifier.SimpleStringIdentifier;
import de.bioforscher.singa.units.features.model.FeatureOrigin;

/**
 * A chemical species should be used to handle everything that can be described with a SMILES (Simplified Molecular
 * Input Line Entry Specification) String, such as small molecules and molecular fragments. The species needs to be
 * identifiable by a {@link SimpleStringIdentifier}. Species can be parsed from the ChEBI Database using the {@link
 * ChEBIParserService ChEBIParserService}.
 *
 * @author cl
 * @see ChemicalEntity
 * @see <a href="https://de.wikipedia.org/wiki/Simplified_Molecular_Input_Line_Entry_Specification">Wikipedia:
 * SMILES</a>
 */
public class Species extends ChemicalEntity<SimpleStringIdentifier> {

    public final static Species UNKNOWN_SPECIES = new Species.Builder("UNK")
            .name("Unknown chemical species")
            .assignFeature(new MolarMass(10, FeatureOrigin.MANUALLY_ANNOTATED))
            .build();

    /**
     * The SIMLES representation of this species.
     */
    private String smilesRepresentation = "No SMILES representation defined.";

    /**
     * Creates a new Species with the given {@link ChEBIIdentifier}.
     *
     * @param identifier The {@link SimpleStringIdentifier}.
     */
    protected Species(SimpleStringIdentifier identifier) {
        super(identifier);
    }

    /**
     * Creates a new Species using a String representation of a {@link SimpleStringIdentifier}.
     *
     * @param identifier A String representation of the {@link SimpleStringIdentifier}.
     */
    protected Species(String identifier) {
        this(new SimpleStringIdentifier(identifier));
    }

    /**
     * Returns the SMILES representation.
     *
     * @return The SMILES representation.
     */
    public String getSmilesRepresentation() {
        return this.smilesRepresentation;
    }

    /**
     * Sets the SMILES representation.
     *
     * @param smilesRepresentation The SMILES representation.
     */
    public void setSmilesRepresentation(String smilesRepresentation) {
        this.smilesRepresentation = smilesRepresentation;
    }

    public static class Builder extends ChemicalEntity.Builder<Species, Builder, SimpleStringIdentifier> {

        public Builder(SimpleStringIdentifier identifier) {
            super(identifier);
        }

        public Builder(String identifier) {
            this(new SimpleStringIdentifier(identifier));
        }

        @Override
        protected Species createObject(SimpleStringIdentifier primaryIdentifer) {
            return new Species(primaryIdentifer);
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
