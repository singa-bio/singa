package de.bioforscher.singa.chemistry.descriptive.entities;

import de.bioforscher.singa.chemistry.descriptive.features.logp.LogP;
import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;

import java.util.HashSet;
import java.util.Set;

/**
 * A small molecule should be used to handle everything that can be described with a SMILES (Simplified Molecular
 * Input Line Entry Specification) String, such as small molecules and molecular fragments. Small molecules can be
 * parsed from the ChEBI Database using the {@link ChEBIParserService ChEBIParserService}.
 *
 * @author cl
 * @see ChemicalEntity
 * @see <a href="https://de.wikipedia.org/wiki/Simplified_Molecular_Input_Line_Entry_Specification">Wikipedia: SMILES</a>
 */
public class SmallMolecule extends ChemicalEntity {

    public static final SmallMolecule UNKNOWN_SPECIES = new SmallMolecule.Builder("UNK")
            .name("Unknown chemical species")
            .assignFeature(new MolarMass(10, FeatureOrigin.MANUALLY_ANNOTATED))
            .build();

    protected static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        SmallMolecule.availableFeatures.addAll(ChemicalEntity.availableFeatures);
        availableFeatures.add(Smiles.class);
        availableFeatures.add(LogP.class);
    }

    /**
     * Creates a new Species with the given {@link ChEBIIdentifier}.
     *
     * @param identifier The {@link SimpleStringIdentifier}.
     */
    protected SmallMolecule(SimpleStringIdentifier identifier) {
        super(identifier);
    }

    /**
     * Creates a new Species using a String representation of a {@link SimpleStringIdentifier}.
     *
     * @param identifier A String representation of the {@link SimpleStringIdentifier}.
     */
    protected SmallMolecule(String identifier) {
        this(new SimpleStringIdentifier(identifier));
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    public static class Builder extends ChemicalEntity.Builder<SmallMolecule, Builder> {

        public Builder(SimpleStringIdentifier identifier) {
            super(identifier);
        }

        public Builder(String identifier) {
            this(new SimpleStringIdentifier(identifier));
        }

        @Override
        protected SmallMolecule createObject(SimpleStringIdentifier primaryIdentifer) {
            return new SmallMolecule(primaryIdentifer.getIdentifier());
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }
}
