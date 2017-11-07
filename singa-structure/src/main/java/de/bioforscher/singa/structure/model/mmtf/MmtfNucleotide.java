package de.bioforscher.singa.structure.model.mmtf;

import de.bioforscher.singa.structure.model.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.Nucleotide;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.HashSet;

/**
 * The implementation of {@link Nucleotide}s for mmtf structures.
 *
 * @author cl
 */
public class MmtfNucleotide extends MmtfLeafSubstructure<NucleotideFamily> implements Nucleotide {

    /**
     * Creates a new {@link MmtfNucleotide}.
     *
     * @param data The original data.
     * @param family The leaf family (e.g. {@link NucleotideFamily#GUANOSINE}).
     * @param leafIdentifier The leaf identifier.
     * @param internalGroupIndex The index of this leaf in the data array.
     * @param atomStartIndex The index of the first atom that belong to this leaf.
     * @param atomEndIndex The index of the last atom that belong to this leaf.
     */
    MmtfNucleotide(StructureDataInterface data, byte[] bytes, NucleotideFamily family, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        super(data, bytes, family, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
    }

    /**
     * A copy constructor that passes all attributes of the given {@link MmtfLeafSubstructure} to a new instance.
     *
     * @param mmtfLeafSubstructure The {@link MmtfLeafSubstructure} to copy.
     */
    private MmtfNucleotide(MmtfNucleotide mmtfLeafSubstructure) {
        super(mmtfLeafSubstructure);
        family = mmtfLeafSubstructure.family;
        exchangeableFamilies = new HashSet<>(mmtfLeafSubstructure.exchangeableFamilies);
    }

    @Override
    public boolean isAnnotatedAsHeteroAtom() {
        return false;
    }

    @Override
    public String flatToString() {
        return "Nucleotide (" + getThreeLetterCode() + ") " + getIdentifier();
    }

    @Override
    public String toString() {
        return flatToString();
    }

    @Override
    public Nucleotide getCopy() {
        return new MmtfNucleotide(this);
    }
}
