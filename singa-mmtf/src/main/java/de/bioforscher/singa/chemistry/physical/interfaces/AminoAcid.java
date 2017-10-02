package de.bioforscher.singa.chemistry.physical.interfaces;

import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.model.Exchangeable;

import java.util.Set;

/**
 * A specific type of {@link LeafSubstructure}, representing amino acids.
 *
 * @author cl
 */
public interface AminoAcid extends LeafSubstructure, Exchangeable<AminoAcidFamily> {

    @Override
    String getThreeLetterCode();

    @Override
    AminoAcidFamily getFamily();

    @Override
    Set<AminoAcidFamily> getExchangeableFamilies();

    @Override
    Set<AminoAcidFamily> getContainingFamilies();

    @Override
    void addExchangeableFamily(AminoAcidFamily exchangeableFamily);
}
