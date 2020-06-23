package bio.singa.chemistry.model;


import bio.singa.chemistry.features.logp.LogP;
import bio.singa.chemistry.features.smiles.Smiles;
import bio.singa.features.model.Feature;
import bio.singa.features.quantities.ConcentrationDiffusivity;

import java.util.HashSet;
import java.util.Set;

/**
 * A small molecule should be used to handle everything that can be described with a SMILES (Simplified Molecular
 * Input Line Entry Specification) String, such as small molecules and molecular fragments.
 *
 * @author cl
 * @see <a href="https://de.wikipedia.org/wiki/Simplified_Molecular_Input_Line_Entry_Specification">Wikipedia: SMILES</a>
 */
public class SmallMolecule extends AbstractChemicalEntity {

    public static Builder create(String identifier) {
        return new Builder(identifier);
    }

    public static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        SmallMolecule.availableFeatures.addAll(AbstractChemicalEntity.availableFeatures);
        availableFeatures.add(ConcentrationDiffusivity.class);
        availableFeatures.add(Smiles.class);
        availableFeatures.add(LogP.class);
    }

    /**
     * Creates a new Species with the given identifier
     *
     * @param identifier The identifier.
     */
    protected SmallMolecule(String identifier) {
        super(identifier);
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    public static class Builder extends AbstractChemicalEntity.Builder<SmallMolecule, Builder> {

        private Builder(String identifier) {
            super(identifier);
        }

        @Override
        protected SmallMolecule createObject(String primaryIdentifer) {
            return new SmallMolecule(primaryIdentifer);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }
}
