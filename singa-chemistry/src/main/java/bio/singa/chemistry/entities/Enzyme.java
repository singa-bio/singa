package bio.singa.chemistry.entities;


import bio.singa.chemistry.features.databases.uniprot.UniProtParserService;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.model.Feature;

import java.util.HashSet;
import java.util.Set;

/**
 * An Enzyme is a Protein, that is associated with a catalytic function. For the usage in reactions this chemical
 * entity can be supplied with a Michaelis constant (usually abbreviated with km), an turnover number (abbreviated
 * with kcat), a List of possible substrates and a critical substrate that is rate determining. Additionally multiple
 * predefined Annotations can be set (additional names, organisms, amino acid sequences, ...). Enzymes may be parsed
 * from the UniProt Database using the {@link UniProtParserService UniProtParserService}.
 *
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Michaelis%E2%80%93Menten_kinetics">Wikipedia: Michaelis–Menten
 * kinetics</a>
 */
public class Enzyme extends Protein {

    private static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        Enzyme.availableFeatures.addAll(AbstractChemicalEntity.availableFeatures);
        availableFeatures.add(MichaelisConstant.class);
        availableFeatures.add(TurnoverNumber.class);
    }

    /**
     * Creates a new Enzyme with the given {@link UniProtIdentifier}.
     *
     * @param identifier The {@link UniProtIdentifier}.
     */
    protected Enzyme(SimpleStringIdentifier identifier) {
        super(identifier);
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    @Override
    public String toString() {
        return "Enzyme " + getIdentifier();
    }

    public static class Builder extends AbstractChemicalEntity.Builder<Enzyme, Builder> {

        public Builder(SimpleStringIdentifier identifier) {
            super(identifier);
        }

        public Builder(String identifier) {
            this(new SimpleStringIdentifier(identifier));
        }

        @Override
        protected Enzyme createObject(SimpleStringIdentifier primaryIdentifer) {
            return new Enzyme(primaryIdentifer);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }

}
