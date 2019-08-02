package bio.singa.chemistry.entities;

import bio.singa.chemistry.features.databases.chebi.ChEBIParserService;
import bio.singa.chemistry.features.logp.LogP;
import bio.singa.chemistry.features.smiles.Smiles;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.features.model.Feature;

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
public class SmallMolecule extends AbstractChemicalEntity {

    public static Builder create(String identifier) {
        return new Builder(identifier);
    }

    public static Builder create(SimpleStringIdentifier identifier) {
        return new Builder(identifier);
    }

    public static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        SmallMolecule.availableFeatures.addAll(AbstractChemicalEntity.availableFeatures);
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
        EntityRegistry.put(identifier.toString(), this);
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

    public static class Builder extends AbstractChemicalEntity.Builder<SmallMolecule, Builder> {

        public Builder(SimpleStringIdentifier identifier) {
            super(identifier);
        }

        private Builder(String identifier) {
            this(new SimpleStringIdentifier(identifier));
        }

        @Override
        protected SmallMolecule createObject(SimpleStringIdentifier primaryIdentifer) {
            return new SmallMolecule(primaryIdentifer);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }
}
