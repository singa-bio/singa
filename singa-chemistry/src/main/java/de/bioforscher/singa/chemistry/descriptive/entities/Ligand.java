package de.bioforscher.singa.chemistry.descriptive.entities;

import de.bioforscher.singa.chemistry.descriptive.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.ForwardsRateConstant;
import de.bioforscher.singa.core.identifier.SimpleStringIdentifier;
import de.bioforscher.singa.features.model.Feature;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class Ligand extends ChemicalEntity<SimpleStringIdentifier> {

    private static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        Ligand.availableFeatures.addAll(ChemicalEntity.availableFeatures);
        availableFeatures.add(ForwardsRateConstant.class);
        availableFeatures.add(BackwardsRateConstant.class);
    }

    /**
     * Creates a new Ligand with the given identifier.
     *
     * @param identifier The pdbIdentifier.
     */
    protected Ligand(SimpleStringIdentifier identifier) {
        super(identifier);
    }



}
