package de.bioforscher.singa.chemistry.entities;

import de.bioforscher.singa.chemistry.descriptive.features.permeability.OsmoticPermeability;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.features.model.Feature;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class Transporter extends Protein {

    private static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        Transporter.availableFeatures.addAll(ChemicalEntity.availableFeatures);
        availableFeatures.add(OsmoticPermeability.class);
    }

    /**
     * Creates a new Transporter with the given identifier.
     *
     * @param identifier The identifier.
     */
    protected Transporter(SimpleStringIdentifier identifier) {
        super(identifier);
    }

    public Transporter(Protein protein) {
        this(protein.getIdentifier());
        name = protein.name;
        annotations = protein.annotations;
        features = protein.features;
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    public static class Builder extends ChemicalEntity.Builder<Transporter, Builder> {

        public Builder(SimpleStringIdentifier identifier) {
            super(identifier);
        }

        public Builder(String identifier) {
            this(new SimpleStringIdentifier(identifier));
        }

        @Override
        protected Transporter createObject(SimpleStringIdentifier primaryIdentifer) {
            return new Transporter(primaryIdentifer);
        }

        @Override
        protected Transporter.Builder getBuilder() {
            return this;
        }

    }

}
