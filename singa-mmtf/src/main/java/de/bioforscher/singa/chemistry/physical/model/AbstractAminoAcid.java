package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.interfaces.AminoAcid;
import de.bioforscher.singa.chemistry.physical.interfaces.LeafSubstructure;

import java.util.Set;

/**
 * @author cl
 */
public abstract class AbstractAminoAcid implements AminoAcid {

    /**
     * The structural family of this entity
     */
    private AminoAcidFamily family;

    /**
     * The families to which the {@link LeafSubstructure} can be exchanged.
     */
    private Set<AminoAcidFamily> exchangeableFamilies;


    @Override
    public String getThreeLetterCode() {
        return family.getThreeLetterCode();
    }

    @Override
    public AminoAcidFamily getFamily() {
        return family;
    }

    @Override
    public Set<AminoAcidFamily> getExchangeableFamilies() {
        return exchangeableFamilies;
    }

}
